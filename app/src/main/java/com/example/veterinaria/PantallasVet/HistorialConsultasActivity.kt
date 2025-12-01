package com.example.veterinaria

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinaria.api.FichaMedicaLectura
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.funciones.veterinario.HistorialAdapter
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HistorialConsultasActivity : AppCompatActivity() {

    private lateinit var adapter: HistorialAdapter
    private var listaFichas: List<FichaMedicaLectura> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_consultas)

        val btnVolver: ImageButton = findViewById(R.id.btn_volver_historial)
        val btnPdf: ImageButton = findViewById(R.id.btn_exportar_pdf)
        val rvHistorial: RecyclerView = findViewById(R.id.rv_historial)
        val pbCargando: ProgressBar = findViewById(R.id.pb_historial)

        adapter = HistorialAdapter(emptyList())
        rvHistorial.layoutManager = LinearLayoutManager(this)
        rvHistorial.adapter = adapter

        // --- Cargar Datos (API) ---
        lifecycleScope.launch {
            val resultado = VeterinariaRepository.fetchHistorialMedico()

            resultado.onSuccess { datos ->
                listaFichas = datos
                adapter.updateData(datos)
                pbCargando.visibility = View.GONE
            }.onFailure { e ->
                Toast.makeText(this@HistorialConsultasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                pbCargando.visibility = View.GONE
            }
        }

        // ---  (Botones) ---
        btnVolver.setOnClickListener {
            finish()
        }

        btnPdf.setOnClickListener {
            if (listaFichas.isNotEmpty()) {
                generarPDF(listaFichas)
            } else {
                Toast.makeText(this, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_historial)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun generarPDF(datos: List<FichaMedicaLectura>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        titlePaint.textSize = 20f
        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = android.graphics.Typeface.DEFAULT_BOLD

        paint.textSize = 12f
        paint.color = Color.BLACK

        canvas.drawText("Reporte de Consultas Veterinarias", 297f, 50f, titlePaint)

        var y = 100f

        paint.isFakeBoldText = true
        canvas.drawText("Fecha", 50f, y, paint)
        canvas.drawText("Animal", 150f, y, paint)
        canvas.drawText("Diagnóstico", 280f, y, paint)
        canvas.drawText("Veterinario", 480f, y, paint)

        paint.isFakeBoldText = false
        y += 20f
        canvas.drawLine(40f, y - 15, 550f, y - 15, paint)

        for (item in datos) {
            if (y > 800) break

            // Columna Fecha
            canvas.drawText(item.fecha_realizada, 50f, y, paint)

            // Columna Animal
            val nombreAnimal = item.animal?.nombre ?: "-"
            canvas.drawText(nombreAnimal.take(12), 150f, y, paint)

            // Columna Diagnóstico
            val diag = item.diagnostico_general.replace("\n", " ")
            canvas.drawText(diag.take(30) + "...", 280f, y, paint)

            // Columna Veterinario
            val nomVet = item.veterinario?.nombre ?: "-"
            canvas.drawText(nomVet.take(10), 480f, y, paint)

            y += 25f // saltar de pagina
        }

        pdfDocument.finishPage(page)

        // 4. Guardar el archivo
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ReporteVeterinaria.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF guardado en Descargas: ${file.name}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }
}