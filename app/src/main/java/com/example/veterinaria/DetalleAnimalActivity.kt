package com.example.veterinaria

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.camara.CamaraUtils
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch

class DetalleAnimalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_animal)

        // --- REFERENCIAS UI ---
        val btnVolver: ImageButton = findViewById(R.id.btn_detalle_volver)
        val imgFoto: ImageView = findViewById(R.id.img_detalle_foto)
        val txNombre: TextView = findViewById(R.id.tx_detalle_nombre)
        val txId: TextView = findViewById(R.id.tx_detalle_id)
        val txStatus: TextView = findViewById(R.id.tx_detalle_activo_status)

        val txFecha: TextView = findViewById(R.id.tx_detalle_fecha_nac)
        val txSexo: TextView = findViewById(R.id.tx_detalle_sexo)
        val txEspecie: TextView = findViewById(R.id.tx_detalle_especie)

        val txArea: TextView = findViewById(R.id.tx_detalle_area)
        val txHabitat: TextView = findViewById(R.id.tx_detalle_habitat)
        val txEstado: TextView = findViewById(R.id.tx_detalle_estado)

        val btnEditar: Button = findViewById(R.id.btn_detalle_editar)
        val btnEliminar: Button = findViewById(R.id.btn_detalle_eliminar)

        btnVolver.setOnClickListener { finish() }

        // --- RECIBIR DATOS ---
        val animalId = intent.getLongExtra("ANIMAL_ID", 0L)
        val animalNombre = intent.getStringExtra("ANIMAL_NOMBRE") ?: ""
        val animalFecha = intent.getStringExtra("ANIMAL_FECHA") ?: ""
        val animalSexo = intent.getStringExtra("ANIMAL_SEXO") ?: ""
        val animalFotoUrl = intent.getStringExtra("ANIMAL_FOTO")

        val esLocal = intent.getBooleanExtra("ES_LOCAL", false)
        val esActivo = intent.getBooleanExtra("ANIMAL_ACTIVO", true)

        val animalEspecieId = intent.getLongExtra("ANIMAL_ESPECIE", 0L)
        val animalHabitatId = intent.getLongExtra("ANIMAL_HABITAT", 0L)
        val animalEstadoId = intent.getLongExtra("ANIMAL_ESTADO", 0L)
        val animalAreaId = intent.getLongExtra("ANIMAL_AREA", 0L)

        // --- SETEAR TEXTOS ---
        val origen = if (esLocal) "Local" else "API"
        txNombre.text = animalNombre
        txId.text = "ID ($origen): $animalId"
        txFecha.text = animalFecha
        txSexo.text = animalSexo

        // --- ESTADO ACTIVO/INACTIVO ---
        if (esActivo) {
            txStatus.text = "ACTIVO"
            txStatus.setTextColor(Color.parseColor("#006400")) // Verde
            txStatus.setBackgroundResource(R.drawable.bg_tag_saludable)
        } else {
            txStatus.text = "INACTIVO"
            txStatus.setTextColor(Color.parseColor("#B00020")) // Rojo
            txStatus.setBackgroundResource(R.drawable.bg_tag_critico)
        }

        // --- FOTO ---
        imgFoto.load(null)
        if (!animalFotoUrl.isNullOrEmpty()) {
            val fotoLimpia = animalFotoUrl.trim()
            if (fotoLimpia.startsWith("http")) {
                imgFoto.load(fotoLimpia) {
                    crossfade(true)
                    placeholder(R.drawable.ic_pulso)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                val imageBytes = CamaraUtils.base64ToByteArray(fotoLimpia)
                if (imageBytes != null) {
                    imgFoto.load(imageBytes) {
                        crossfade(true)
                        placeholder(R.drawable.ic_pulso)
                    }
                } else {
                    imgFoto.setImageResource(android.R.drawable.ic_menu_camera)
                }
            }
        } else {
            imgFoto.setImageResource(android.R.drawable.ic_menu_camera)
        }

        // --- CARGA DE API ---
        if (ValidarConexionWAN.isOnline(this) && !esLocal) {
            txEspecie.text = "Cargando..."
            txHabitat.text = "Cargando..."
            txEstado.text = "Cargando..."
            txArea.text = "Cargando..."

            lifecycleScope.launch {
                runCatching {
                    if(animalEspecieId != 0L) VeterinariaRepository.fetchEspecieById(animalEspecieId).onSuccess { txEspecie.text = it.nombre_comun }
                    if(animalHabitatId != 0L) VeterinariaRepository.fetchHabitatById(animalHabitatId).onSuccess { txHabitat.text = it.nombre }
                    if(animalEstadoId != 0L) VeterinariaRepository.fetchEstadoById(animalEstadoId).onSuccess { txEstado.text = it.estado }
                    if(animalAreaId != 0L) VeterinariaRepository.fetchAreaById(animalAreaId).onSuccess { txArea.text = it.nombre }
                }
            }
        } else {
            txEspecie.text = "ID: $animalEspecieId"
            txHabitat.text = "ID: $animalHabitatId"
            txEstado.text = "ID: $animalEstadoId"
            txArea.text = "ID: $animalAreaId"
        }

        // --- BOTONES INTELIGENTES ---
        if (esLocal) {
            btnEditar.visibility = View.GONE
            btnEliminar.visibility = View.GONE
        } else {
            // 1. Configuración visual del botón según estado
            if (esActivo) {
                btnEliminar.text = "Desactivar"
                btnEliminar.setBackgroundColor(Color.parseColor("#B00020")) // Rojo
                btnEliminar.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_close_clear_cancel, 0, 0, 0)
            } else {
                btnEliminar.text = "Reactivar"
                btnEliminar.setBackgroundColor(Color.parseColor("#006400")) // Verde
                btnEliminar.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_add, 0, 0, 0)
            }

            // 2. Acción al hacer click
            btnEliminar.setOnClickListener {
                val nuevoEstado = !esActivo // Invertimos el estado
                val accionTexto = if (nuevoEstado) "Reactivar" else "Desactivar"

                AlertDialog.Builder(this)
                    .setTitle("$accionTexto Animal")
                    .setMessage("¿Estás seguro de $accionTexto a $animalNombre?")
                    .setPositiveButton("Sí") { _, _ ->
                        lifecycleScope.launch {
                            // Llamamos a la función de cambiar estado del repositorio
                            VeterinariaRepository.cambiarEstado(animalId, nuevoEstado).onSuccess {
                                Toast.makeText(this@DetalleAnimalActivity, "Estado cambiado!", Toast.LENGTH_SHORT).show()
                                finish()
                            }.onFailure {
                                Toast.makeText(this@DetalleAnimalActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null).show()
            }

            btnEditar.setOnClickListener {
                val intentEditar = Intent(this, EditarAnimalActivity::class.java).apply {
                    putExtra("ID_ANIMAL", animalId)
                    putExtra("NOMBRE", animalNombre)
                    putExtra("FECHA", animalFecha)
                    putExtra("FOTO", animalFotoUrl)
                    putExtra("SEXO_ID", animalSexo)
                    putExtra("ESPECIE_ID", animalEspecieId)
                    putExtra("HABITAT_ID", animalHabitatId)
                    putExtra("ESTADO_ID", animalEstadoId)
                    putExtra("AREA_ID", animalAreaId)
                    putExtra("ACTIVO", esActivo)
                }
                startActivity(intentEditar)
                finish()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_detalle_animal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}