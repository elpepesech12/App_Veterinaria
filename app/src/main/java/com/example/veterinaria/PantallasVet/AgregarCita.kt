package com.example.veterinaria.PantallasVet

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.veterinaria.R
import com.example.veterinaria.api.AnimalSimple
import com.example.veterinaria.api.InsertarCita
import com.example.veterinaria.api.SesionManager
import com.example.veterinaria.api.TipoCitaSimple
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.funciones.notificacion.Notificador
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AgregarCita : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_agregar_cita, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerAnimal: AutoCompleteTextView = view.findViewById(R.id.autocomplete_animal)
        val spinnerTipoCita: AutoCompleteTextView = view.findViewById(R.id.autocomplete_tipo_cita)
        val inputFecha: TextInputEditText = view.findViewById(R.id.input_fecha)
        val inputHora: TextInputEditText = view.findViewById(R.id.input_hora)
        val btnGuardar: Button = view.findViewById(R.id.btn_guardar_cita)
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar_guardar)

        var listaAnimales = listOf<AnimalSimple>()
        var listaTiposCita = listOf<TipoCitaSimple>()

        var animalSeleccionadoId: Long? = null
        var tipoCitaSeleccionadoId: Long? = null
        var fechaSeleccionadaApi: String = "" // "YYYY-MM-DD"
        var horaSeleccionadaApi: String = "" // "HH:MM:SS"

        fun cargarDatosSpinners() {
            progressBar.isVisible = true
            lifecycleScope.launch {
                // cargar animales
                val resultAnimales = VeterinariaRepository.fetchAnimalesParaSpinner()
                resultAnimales.onSuccess { animales ->
                    listaAnimales = animales // Guarda la lista
                    val nombresAnimales = animales.map { it.nombre }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresAnimales)
                    spinnerAnimal.setAdapter(adapter)
                }.onFailure {
                    Toast.makeText(context, "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }


                // cargar tipos de cita
                val resultTiposCita = VeterinariaRepository.fetchTiposCita()
                resultTiposCita.onSuccess { tipos ->
                    listaTiposCita = tipos // guarda la lista
                    val nombresTiposCita = tipos.map { it.nombre }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresTiposCita)
                    spinnerTipoCita.setAdapter(adapter)
                }.onFailure {
                    Toast.makeText(context, "Error al cargar tipos de cita", Toast.LENGTH_SHORT).show()
                }

                progressBar.isVisible = false
            }
        }

        fun setupSpinners() {
            spinnerAnimal.setOnItemClickListener { parent, view, position, id ->
                animalSeleccionadoId = listaAnimales[position].id
            }

            spinnerTipoCita.setOnItemClickListener { parent, view, position, id ->
                tipoCitaSeleccionadoId = listaTiposCita[position].id
            }
        }

        fun setupDateTimePickers() {
            val calendario = Calendar.getInstance()

            // --- selector de fecha ---
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendario.set(Calendar.YEAR, year)
                calendario.set(Calendar.MONTH, month)
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // formato para mostrar al usuario (ej: 09/11/2025)
                val formatoVista = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                inputFecha.setText(formatoVista.format(calendario.time))

                // formato para enviar a la api (ej: 2025-11-09)
                val formatoAPI = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                fechaSeleccionadaApi = formatoAPI.format(calendario.time)
            }

            inputFecha.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            // --- selector de hora ---
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendario.set(Calendar.MINUTE, minute)

                // formato para mostrar al usuario (ej: 14:30)
                val formatoVista = SimpleDateFormat("HH:mm", Locale.getDefault())
                inputHora.setText(formatoVista.format(calendario.time))

                // formato para enviar a la API (ej: 14:30:00)
                val formatoAPI = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                horaSeleccionadaApi = formatoAPI.format(calendario.time)
            }

            inputHora.setOnClickListener {
                TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    calendario.get(Calendar.HOUR_OF_DAY),
                    calendario.get(Calendar.MINUTE),
                    true // Formato 24 horas
                ).show()
            }
        }

        /**
         * valida que todos los campos estén llenos
         */
        fun validarFormulario(): Boolean {
            if (animalSeleccionadoId == null) {
                Toast.makeText(context, "Por favor, selecciona un animal", Toast.LENGTH_SHORT).show()
                spinnerAnimal.error = "Requerido"
                return false
            }
            if (tipoCitaSeleccionadoId == null) {
                Toast.makeText(context, "Por favor, selecciona un tipo de cita", Toast.LENGTH_SHORT).show()
                spinnerTipoCita.error = "Requerido"
                return false
            }
            if (fechaSeleccionadaApi.isBlank()) {
                Toast.makeText(context, "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
                inputFecha.error = "Requerido"
                return false
            }
            if (horaSeleccionadaApi.isBlank()) {
                Toast.makeText(context, "Por favor, selecciona una hora", Toast.LENGTH_SHORT).show()
                inputHora.error = "Requerido"
                return false
            }
            // limpiamos errores
            spinnerAnimal.error = null
            spinnerTipoCita.error = null
            inputFecha.error = null
            inputHora.error = null
            return true
        }

        /**
         * llama al repositorio para guardar la cita en la api
         */
        fun enviarCita(cita: InsertarCita) {
            progressBar.isVisible = true
            btnGuardar.isEnabled = false

            lifecycleScope.launch {
                try {
                    val resultado = VeterinariaRepository.insertCita(cita)
                    resultado.onSuccess { citaCreada ->
                        Toast.makeText(context, "¡Cita creada con éxito!", Toast.LENGTH_LONG).show()

                        Notificador.enviarNotificacionCita(
                            requireContext(),
                            spinnerAnimal.text.toString(),
                            spinnerTipoCita.text.toString(),
                            inputHora.text.toString()
                        )

                        // volvemos a la pantalla anterior (el calendario)
                        findNavController().popBackStack()
                    }.onFailure {
                        Log.e("AgregarCita", "Error al guardar: ${it.message}")
                        Toast.makeText(context, "Error al guardar la cita", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("AgregarCita", "Excepción al guardar: ${e.message}")
                    Toast.makeText(context, "Error inesperado", Toast.LENGTH_LONG).show()
                } finally {
                    progressBar.isVisible = false
                    btnGuardar.isEnabled = true
                }
            }
        }

        /**
         * configura el listener del botón de guardar
         */
        fun setupBotonGuardar() {
            btnGuardar.setOnClickListener {
                // se valida
                if (!validarFormulario()) return@setOnClickListener

                // se obtiene el id del veterinario de la sesión
                val idVeterinarioLogueado = SesionManager.getVeterinarioId(requireContext())
                if (idVeterinarioLogueado == -1L) {
                    Toast.makeText(context, "Error fatal: No se encontró sesión de veterinario.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // crea el objeto
                val nuevaCita = InsertarCita(
                    id_animal = animalSeleccionadoId!!,
                    id_veterinario = idVeterinarioLogueado,
                    id_tipo_cita = tipoCitaSeleccionadoId!!,
                    fecha = fechaSeleccionadaApi,
                    hora = horaSeleccionadaApi
                )

                // lo envia
                enviarCita(nuevaCita)
            }
        }

        setupSpinners()
        setupDateTimePickers()
        setupBotonGuardar()
        cargarDatosSpinners()

    }
}