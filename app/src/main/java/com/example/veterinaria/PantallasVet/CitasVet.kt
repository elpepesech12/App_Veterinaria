package com.example.veterinaria.PantallasVet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kizitonwose.calendar.view.CalendarView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinaria.R
import com.example.veterinaria.api.CitaUI
import com.example.veterinaria.api.CitasAdapter
import com.example.veterinaria.api.VeterinariaRepository
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class CitasVet : Fragment() {

    // --- Variables que el Calendario NECESITA compartir ---
    // (Estas deben estar afuera para que el 'dayBinder' las vea)
    private var diaSeleccionado: LocalDate? = null
    private val hoy = LocalDate.now()
    private val listaMaestraDeCitas = mutableListOf<CitaUI>()
    private var diasConCitas = setOf<LocalDate>()
    private var adapterCitas: CitasAdapter? = null // La haremos nulleable, como te gusta

    // Formateadores de fecha (útiles)
    private val formateadorTitulo = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
    private val formateadorMes = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_citas_vet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarioVista: CalendarView = view.findViewById(R.id.calendario_vista)
        val recyclerCitas: RecyclerView = view.findViewById(R.id.recycler_citas_del_dia)
        val txtNoCitas: TextView = view.findViewById(R.id.txt_no_citas)
        val txtTituloCitasDelDia: TextView = view.findViewById(R.id.txt_titulo_citas_del_dia)
        val txtMesCalendario: TextView = view.findViewById(R.id.txt_mes_calendario)
        val layoutDiasSemana: LinearLayout = view.findViewById(R.id.layout_dias_semana)
        val btnMesSiguiente: ImageButton = view.findViewById(R.id.btn_mes_siguiente)
        val btnMesAnterior: ImageButton = view.findViewById(R.id.btn_mes_anterior)


        // Función para actualizar la lista de abajo
        fun filtrarCitasParaDia(fecha: LocalDate) {
            // Actualiza el título
            txtTituloCitasDelDia.text = "Citas para ${fecha.format(formateadorTitulo)}"

            // Filtra la lista maestra
            val citasDelDia = listaMaestraDeCitas.filter { LocalDate.parse(it.fecha) == fecha }

            // Actualiza el adapter (con check de nulo)
            adapterCitas?.actualizarDatos(citasDelDia)

            // Muestra u oculta el mensaje de "No hay citas"
            txtNoCitas.isVisible = citasDelDia.isEmpty()
            recyclerCitas.isVisible = citasDelDia.isNotEmpty()
        }

        // Función para manejar la selección de un día
        fun seleccionarDia(fecha: LocalDate) {
            val fechaAntigua = diaSeleccionado
            diaSeleccionado = fecha

            // Refrescamos el calendario
            calendarioVista.notifyDateChanged(fecha)
            fechaAntigua?.let { calendarioVista.notifyDateChanged(it) }

            // Filtramos la lista de abajo
            filtrarCitasParaDia(fecha)
        }

        // Clase interna para el "molde" de cada día
        // (Esta SÍ tiene que ser 'inner class' para acceder a 'diaSeleccionado')
        class ContenedorDia(view: View) : ViewContainer(view) {
            val txtDia = view.findViewById<TextView>(R.id.txt_dia_calendario)
            val vistaPunto = view.findViewById<View>(R.id.vista_punto_cita)
            val vistaSeleccionado = view.findViewById<View>(R.id.vista_dia_seleccionado)
            lateinit var dia: CalendarDay

            init {
                view.setOnClickListener {
                    if (dia.position == DayPosition.MonthDate) {
                        if (diaSeleccionado != dia.date) {
                            seleccionarDia(dia.date) // Llama a la función de arriba
                        }
                    }
                }
            }
        }

        fun setupRecyclerCitas() {
            adapterCitas = CitasAdapter(emptyList())
            recyclerCitas.layoutManager = LinearLayoutManager(requireContext())
            recyclerCitas.adapter = adapterCitas
        }

        fun setupCalendario() {
            // Config 1: El Binder (conecta datos al layout del día)
            calendarioVista.dayBinder = object : MonthDayBinder<ContenedorDia> {
                override fun create(view: View) = ContenedorDia(view)

                override fun bind(container: ContenedorDia, data: CalendarDay) {
                    container.dia = data
                    val txtDia = container.txtDia
                    val vistaPunto = container.vistaPunto
                    val vistaSeleccionado = container.vistaSeleccionado

                    txtDia.text = data.date.dayOfMonth.toString()

                    if (data.position == DayPosition.MonthDate) {
                        txtDia.isVisible = true

                        when (data.date) {
                            diaSeleccionado -> {
                                vistaSeleccionado.isVisible = true
                                txtDia.setTextColor(requireContext().getColor(R.color.black))
                            }
                            hoy -> {
                                vistaSeleccionado.isVisible = false
                                // ¡OJO! Asegúrate de tener este color en 'colors.xml'
                                // Si no, cámbialo por: android.R.color.holo_blue_dark
                                txtDia.setTextColor(resources.getColor(com.google.android.material.R.color.design_default_color_primary))
                            }
                            else -> {
                                vistaSeleccionado.isVisible = false
                                txtDia.setTextColor(requireContext().getColor(R.color.black))
                            }
                        }
                        vistaPunto.isVisible = diasConCitas.contains(data.date)
                    } else {
                        txtDia.isVisible = false
                        vistaPunto.isVisible = false
                        vistaSeleccionado.isVisible = false
                    }
                }
            }

            // Config 2: Rango del Calendario
            val mesActual = YearMonth.now()
            val primerMes = mesActual.minusMonths(6)
            val ultimoMes = mesActual.plusMonths(6)
            val primerDiaSemana = firstDayOfWeekFromLocale()

            calendarioVista.setup(primerMes, ultimoMes, primerDiaSemana)
            calendarioVista.scrollToMonth(mesActual)

            // Config 3: Títulos (Dom, Lun, Mar...)
            val diasSemana = daysOfWeek(firstDayOfWeek = primerDiaSemana)
            (layoutDiasSemana.children as Sequence<TextView>).forEachIndexed { index, textView ->
                val dia = diasSemana[index]
                val nombre = dia.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
                textView.text = nombre
            }

            // Config 4: Listener de Scroll (para cambiar "Noviembre 2025")
            calendarioVista.monthScrollListener = object : MonthScrollListener {
                override fun invoke(month: CalendarMonth) {
                    txtMesCalendario.text = month.yearMonth.format(formateadorMes).replaceFirstChar { it.uppercase() }
                }
            }

            // Config 5: Botones de Flecha
            btnMesSiguiente.setOnClickListener {
                calendarioVista.findFirstVisibleMonth()?.let {
                    calendarioVista.smoothScrollToMonth(it.yearMonth.plusMonths(1))
                }
            }
            btnMesAnterior.setOnClickListener {
                calendarioVista.findFirstVisibleMonth()?.let {
                    calendarioVista.smoothScrollToMonth(it.yearMonth.minusMonths(1))
                }
            }
        }

        // Función para cargar los datos desde la API
        fun cargarDatosDeCitas(fechaInicio: String, fechaFin: String) {
            lifecycleScope.launch {
                try {
                    val resultado = VeterinariaRepository.fetchCitasPorRango(fechaInicio, fechaFin)
                    if (resultado.isSuccess) {
                        val citas = resultado.getOrNull() ?: emptyList()

                        listaMaestraDeCitas.clear()
                        listaMaestraDeCitas.addAll(citas)
                        diasConCitas = citas.map { LocalDate.parse(it.fecha) }.toSet()

                        calendarioVista.notifyCalendarChanged()

                        // Seleccionamos HOY al cargar
                        seleccionarDia(hoy)

                    } else {
                        Log.e("CitasVet", "Error al cargar citas: ${resultado.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    Log.e("CitasVet", "Excepción al cargar citas: ${e.message}")
                }
            }
        }


        setupRecyclerCitas()
        setupCalendario()

        val fechaInicio = hoy.minusMonths(6).withDayOfMonth(1).toString()
        val fechaFin = hoy.plusMonths(6).withDayOfMonth(31).toString()
        cargarDatosDeCitas(fechaInicio, fechaFin)
    }

}