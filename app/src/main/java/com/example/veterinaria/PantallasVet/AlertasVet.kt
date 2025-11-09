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
import com.example.veterinaria.api.AlertaUI
import com.example.veterinaria.api.AlertasAdapter
import com.example.veterinaria.api.VeterinariaRepository
import com.google.android.material.button.MaterialButtonToggleGroup
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
        val toggleGroup: MaterialButtonToggleGroup = view.findViewById(R.id.toggleGroup_alertas)

        var adapterAlertas : AlertasAdapter? = null

        val listaMaestraDeAlertas = mutableListOf<AlertaUI>()

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

            lifecycleScope.launch {
                try {
                    val resultado = VeterinariaRepository.fetchAlertas()

                    if (resultado.isSuccess) {
                        val alertas = resultado.getOrNull()
                        if (!alertas.isNullOrEmpty()) {

                            // Guardamos la lista completa en nuestra variable maestra
                            listaMaestraDeAlertas.clear()
                            listaMaestraDeAlertas.addAll(alertas)

                            // por defecto, mostramos "Todas"
                            // (Aseguramos que el botón "Todas" esté marcado)
                            toggleGroup.check(R.id.btn_filtro_todas)
                            // Actualizamos el adapter con la lista completa
                            adapterAlertas?.updateData(listaMaestraDeAlertas)

                        } else {
                            Toast.makeText(context, "No se encontraron alertas", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("AlertasVet", "Error al cargar alertas: ${resultado.exceptionOrNull()?.message}")
                        Toast.makeText(context, "Error al cargar alertas", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("AlertasVet", "Excepción inesperada: ${e.message}")
                    Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show()
                } finally {
                    progressBar.visibility = View.GONE
                    recyclerAlertas.visibility = View.VISIBLE
                }
            }
        }

        //los filtros de arriba de la pestaña
        fun setupFiltros() {
            toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
                // Solo reaccionamos al botón que se acaba de marcar
                if (isChecked) {
                    // Creamos una nueva lista filtrada basándonos en la listaMaestra
                    val listaFiltrada = when (checkedId) {

                        R.id.btn_filtro_criticas -> {
                            listaMaestraDeAlertas.filter {
                                it.tipoAlerta.nombre.equals("Crítico", ignoreCase = true)
                            }
                        }

                        R.id.btn_filtro_advertencias -> {
                            listaMaestraDeAlertas.filter {
                                it.tipoAlerta.nombre.equals("Advertencia", ignoreCase = true)
                            }
                        }

                        R.id.btn_filtro_info -> {
                            listaMaestraDeAlertas.filter {
                                // Filtramos por "Informativo" O por "Exitosa" (como en tu adapter)
                                it.tipoAlerta.nombre.equals("Informativo", ignoreCase = true) ||
                                        it.titulo.contains("Exitosa", ignoreCase = true)
                            }
                        }

                        // Caso por defecto: R.id.btn_filtro_todas
                        else -> {
                            listaMaestraDeAlertas // Devolvemos la lista completa
                        }
                    }

                    // Actualizamos el adapter con la nueva lista filtrada
                    adapterAlertas?.updateData(listaFiltrada)
                }
            }
        }


        setupRecyclerView()
        cargarDatosDeAlertas()
        setupFiltros()
    }
}