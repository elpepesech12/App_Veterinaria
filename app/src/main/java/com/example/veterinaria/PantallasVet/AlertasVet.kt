package com.example.veterinaria.PantallasVet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinaria.R
import com.example.veterinaria.api.AlertasAdapter
import com.example.veterinaria.api.VeterinariaRepository
import kotlinx.coroutines.launch

class AlertasVet : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alertas_vet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerAlertas : RecyclerView = view.findViewById(R.id.recycler_view_alertas)
        val progressBar : ProgressBar = view.findViewById(R.id.progress_bar_alertas)

        var adapterAlertas : AlertasAdapter? = null

        fun setupRecyclerView() {
            // Inicializa el adapter con una lista vacía
            adapterAlertas = AlertasAdapter(emptyList())
            recyclerAlertas.layoutManager = LinearLayoutManager(requireContext())
            recyclerAlertas.adapter = adapterAlertas
        }

        fun cargarDatosDeAlertas() {
            // Muestra la barra de progreso
            progressBar.visibility = View.VISIBLE
            recyclerAlertas.visibility = View.GONE

            // Usa 'lifecycleScope.launch' para llamar a la función suspend del repo
            lifecycleScope.launch {
                try {
                    // 5. Llama al Repositorio
                    val resultado = VeterinariaRepository.fetchAlertas()

                    // 6. Verifica el resultado
                    if (resultado.isSuccess) {
                        val alertas = resultado.getOrNull()
                        if (!alertas.isNullOrEmpty()) {
                            // ¡Éxito! Actualiza el adapter
                            adapterAlertas?.updateData(alertas)
                        } else {
                            // Éxito, pero no vinieron datos
                            Toast.makeText(context, "No se encontraron alertas", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        // Fallo (error de red, JSON, etc.)
                        Log.e(
                            "AlertasVet",
                            "Error al cargar alertas: ${resultado.exceptionOrNull()?.message}"
                        )
                        Toast.makeText(context, "Error al cargar alertas", Toast.LENGTH_SHORT)
                            .show()
                    }

                } catch (e: Exception) {
                    // Captura cualquier otra excepción
                    Log.e("AlertasVet", "Excepción inesperada: ${e.message}")
                    Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show()
                } finally {
                    // 7. Oculta la barra de progreso
                    progressBar.visibility = View.GONE
                    recyclerAlertas.visibility = View.VISIBLE
                }
            }
        }

        setupRecyclerView()
        cargarDatosDeAlertas()
    }
}