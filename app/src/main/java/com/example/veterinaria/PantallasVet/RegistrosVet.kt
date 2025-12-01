package com.example.veterinaria.PantallasVet

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.R
import com.example.veterinaria.api.Animal
import com.example.veterinaria.api.SesionManager // Asegúrate de tener esto o usa SharedPreferences
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch
import java.util.Calendar

class RegistrosVet : Fragment() {

    private lateinit var spAnimal: Spinner
    private lateinit var edFecha: EditText
    private lateinit var edDiagnostico: EditText
    private lateinit var btnGuardar: Button

    // Lista para guardar los objetos Animal completos y poder sacar su ID
    private var listaAnimales: List<Animal> = emptyList()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registros_vet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias
        spAnimal = view.findViewById(R.id.sp_registro_animal)
        edFecha = view.findViewById(R.id.ed_registro_fecha)
        edDiagnostico = view.findViewById(R.id.ed_registro_diagnostico)
        btnGuardar = view.findViewById(R.id.btn_guardar_registro)

        // En onViewCreated:
        val btnHistorial = view.findViewById<Button>(R.id.btn_ver_historial_completo)

        btnHistorial.setOnClickListener {
            val intent = android.content.Intent(requireContext(), com.example.veterinaria.HistorialConsultasActivity::class.java)
            startActivity(intent)
        }
        // 1. Configurar Calendario para la fecha
        edFecha.setOnClickListener {
            mostrarCalendario()
        }

        // 2. Cargar Spinner con animales
        cargarSpinnerAnimales()

        // 3. Botón Guardar
        btnGuardar.setOnClickListener {
            guardarRegistro()
        }
    }

    private fun mostrarCalendario() {
        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            // Formato para SQL: YYYY-MM-DD
            // Sumamos 1 al mes porque en Java Enero es 0
            val fechaFormateada = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
            edFecha.setText(fechaFormateada)
        }, anio, mes, dia)

        datePicker.show()
    }

    private fun cargarSpinnerAnimales() {
        if (!ValidarConexionWAN.isOnline(requireContext())) {
            Toast.makeText(context, "Sin conexión para cargar animales", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Reutilizamos la función que trae todos los animales
            VeterinariaRepository.fetchAnimales().onSuccess { animales ->
                // Filtramos solo los activos si quieres (opcional, pero recomendado)
                listaAnimales = animales.filter { it.activo }

                // Creamos una lista solo con los nombres para el Spinner
                val nombres = listaAnimales.map { it.nombre }

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
                spAnimal.adapter = adapter

            }.onFailure {
                Toast.makeText(context, "Error cargando animales", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarRegistro() {
        // A. Validaciones
        val fecha = edFecha.text.toString()
        val diagnostico = edDiagnostico.text.toString()

        if (fecha.isEmpty() || diagnostico.isEmpty()) {
            Toast.makeText(context, "Completa fecha y diagnóstico", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAnimales.isEmpty()) {
            Toast.makeText(context, "No hay animales seleccionados", Toast.LENGTH_SHORT).show()
            return
        }

        // B. Obtener IDs
        val posicionSeleccionada = spAnimal.selectedItemPosition
        val animalSeleccionado = listaAnimales[posicionSeleccionada]
        val idAnimal = animalSeleccionado.id

        // OBTENER ID DEL VETERINARIO (Usuario Logueado)
        // Opción 1: Si usas SesionManager
        val idVet = SesionManager.getVeterinarioId(requireContext())

        // Opción 2: Si no tienes SesionManager, usa un ID fijo temporalmente para probar (ej: 1)
        // val idVet = 1L

        if (idVet == -1L) {
            Toast.makeText(context, "Error: Sesión de veterinario no encontrada", Toast.LENGTH_LONG).show()
            return
        }

        // C. Enviar a Supabase
        btnGuardar.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            val resultado = VeterinariaRepository.crearFicha(fecha, diagnostico, idAnimal, idVet)

            resultado.onSuccess {
                Toast.makeText(context, "¡Consulta registrada!", Toast.LENGTH_LONG).show()
                // Limpiar campos
                edDiagnostico.setText("")
                edFecha.setText("")
                btnGuardar.isEnabled = true
            }.onFailure { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnGuardar.isEnabled = true
            }
        }
    }
}