package com.example.veterinaria.PantallasVet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinaria.R
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.funciones.ValidarConexionWAN
import com.example.veterinaria.funciones.veterinario.AnimalesAdapter
import kotlinx.coroutines.launch

class VerAnimales : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ver_animales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvAnimales : RecyclerView = view.findViewById(R.id.rv_animales)
        val btnVolver: ImageButton = view.findViewById(R.id.btn_volver)
        var animalesAdapter: AnimalesAdapter? = null

        fun setupRecyclerView() {
            animalesAdapter = AnimalesAdapter(emptyList())
            rvAnimales.layoutManager = LinearLayoutManager(requireContext())
            rvAnimales.adapter = animalesAdapter
        }

        fun cargarTodosLosAnimales() {
            if (!ValidarConexionWAN.isOnline(requireContext())) {
                Toast.makeText(requireContext(), "Sin conexión.", Toast.LENGTH_LONG).show()
                return
            }
            viewLifecycleOwner.lifecycleScope.launch {
                // ¡Llama a la NUEVA función que trae TODOS!
                val animalesResult = VeterinariaRepository.fetchAllAnimalesListado()

                animalesResult.onSuccess { listaDeAnimales ->
                    animalesAdapter?.updateData(listaDeAnimales)
                }.onFailure { e ->
                    Toast.makeText(requireContext(), "Error Animales: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnVolver.setOnClickListener {
            // 'parentFragmentManager' es el gestor de fragments
            // 'popBackStack()' es la acción de "presionar atrás"
            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()
        cargarTodosLosAnimales()
    }
}