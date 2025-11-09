package com.example.veterinaria.PantallasVet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinaria.R
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.funciones.ValidarConexionWAN
import com.example.veterinaria.funciones.veterinario.AnimalesAdapter
import kotlinx.coroutines.launch

class HomeVet : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_vet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtStatTotal : TextView = view.findViewById(R.id.txt_stat_total_animales)
        val txtStatCriticos : TextView = view.findViewById(R.id.txt_stat_criticos)
        val txtVerTodos : TextView = view.findViewById(R.id.txt_ver_todos)
        val rvAnimales : RecyclerView = view.findViewById(R.id.rv_animales)
        val btnAjustes: ImageButton = view.findViewById(R.id.btn_ajustes)
        var animalesAdapter: AnimalesAdapter? = null

        fun setupRecyclerView() {
            animalesAdapter = AnimalesAdapter(emptyList())
            rvAnimales.layoutManager = LinearLayoutManager(requireContext())
            rvAnimales.adapter = animalesAdapter
            rvAnimales.isNestedScrollingEnabled = false
        }

        fun cargarAnimales() {
            if (!ValidarConexionWAN.isOnline(requireContext())) {
                return
            }
            viewLifecycleOwner.lifecycleScope.launch {
                val animalesResult = VeterinariaRepository.fetchAnimalesDashboard()
                animalesResult.onSuccess { listaDeAnimales ->

                    animalesAdapter?.updateData(listaDeAnimales)
                }.onFailure { e ->
                    Toast.makeText(requireContext(), "Error Animales: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        fun cargarDatosDashboard() {
            if (!ValidarConexionWAN.isOnline(requireContext())) {
                Toast.makeText(requireContext(), "Sin conexión.", Toast.LENGTH_LONG).show()
                txtStatTotal.text = "N/A"
                txtStatCriticos.text = "N/A"
                return
            }
            viewLifecycleOwner.lifecycleScope.launch {
                val statsResult = VeterinariaRepository.getDashboardStats()
                statsResult.onSuccess { stats ->
                    txtStatTotal.text = stats.totalAnimales.toString()
                    txtStatCriticos.text = stats.totalCriticos.toString()
                }.onFailure { e ->
                    Toast.makeText(requireContext(), "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
                    txtStatTotal.text = "Error"
                    txtStatCriticos.text = "Error"
                }
            }
        }

        setupRecyclerView()
        cargarAnimales()
        cargarDatosDashboard()

        txtVerTodos.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, VerAnimales())
                .addToBackStack(null) // Permite volver atrás con el botón del teléfono
                .commit()
        }

        //PARA PROBAR EL BTN AJUSTES
        btnAjustes.setOnClickListener {
            // 'requireContext()' se usa en Fragments en lugar de 'this'
            Toast.makeText(requireContext(), "Clic en Ajustes", Toast.LENGTH_SHORT).show()
        }
    }
}