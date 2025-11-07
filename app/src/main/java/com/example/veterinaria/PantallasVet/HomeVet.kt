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
import com.example.veterinaria.R
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.funciones.ValidarConexionWAN
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

        fun cargarDatosDashboard() {
            // Verificamos conexión (como hace tu amigo)
            if (!ValidarConexionWAN.isOnline(requireContext())) {
                Toast.makeText(requireContext(), "Sin conexión. No se pueden cargar estadísticas.", Toast.LENGTH_LONG).show()
                txtStatTotal.text = "N/A"
                txtStatCriticos.text = "N/A"
                return // Salir de la función si no hay internet
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Llama a la nueva función del repositorio que usa Retrofit
            val statsResult = VeterinariaRepository.getDashboardStats()

            // Revisa si el resultado fue exitoso o falló
            statsResult.onSuccess { stats ->
                // ¡Éxito! Actualiza la UI
                txtStatTotal.text = stats.totalAnimales.toString()
                txtStatCriticos.text = stats.totalCriticos.toString()

            }.onFailure { e ->
                // Maneja el error
                Toast.makeText(requireContext(), "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
                txtStatTotal.text = "Error"
                txtStatCriticos.text = "Error"
            }
        }

        cargarDatosDashboard()

        val btnAjustes: ImageButton = view.findViewById(R.id.btn_ajustes)
        //PARA PROBAR EL BTN AJUSTES
        btnAjustes.setOnClickListener {
            // 'requireContext()' se usa en Fragments en lugar de 'this'
            Toast.makeText(requireContext(), "Clic en Ajustes", Toast.LENGTH_SHORT).show()
        }
    }
}