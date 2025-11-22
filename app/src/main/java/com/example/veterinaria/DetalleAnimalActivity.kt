package com.example.veterinaria

import android.content.Intent
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
import coil.load  // IMPORTANTE
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.camara.CamaraUtils
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch

class DetalleAnimalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_animal)

        // Referencias UI
        val btnVolver: ImageButton = findViewById(R.id.btn_detalle_volver)
        val imgFoto: ImageView = findViewById(R.id.img_detalle_foto)
        val txNombre: TextView = findViewById(R.id.tx_detalle_nombre)
        val txId: TextView = findViewById(R.id.tx_detalle_id)
        val txFecha: TextView = findViewById(R.id.tx_detalle_fecha_nac)
        val txSexo: TextView = findViewById(R.id.tx_detalle_sexo)

        val txEstado: TextView = findViewById(R.id.tx_detalle_estado)
        val txEspecie: TextView = findViewById(R.id.tx_detalle_especie)
        val txHabitat: TextView = findViewById(R.id.tx_detalle_habitat)
        val txArea: TextView = findViewById(R.id.tx_detalle_area)

        val btnEditar: Button = findViewById(R.id.btn_detalle_editar)
        val btnEliminar: Button = findViewById(R.id.btn_detalle_eliminar)

        btnVolver.setOnClickListener { finish() }

        // Recibir datos
        val animalId = intent.getLongExtra("ANIMAL_ID", 0L)
        val animalNombre = intent.getStringExtra("ANIMAL_NOMBRE") ?: ""
        val animalFecha = intent.getStringExtra("ANIMAL_FECHA") ?: ""
        val animalSexo = intent.getStringExtra("ANIMAL_SEXO") ?: ""
        val animalEspecieId = intent.getLongExtra("ANIMAL_ESPECIE", 0L)
        val animalHabitatId = intent.getLongExtra("ANIMAL_HABITAT", 0L)
        val animalEstadoId = intent.getLongExtra("ANIMAL_ESTADO", 0L)
        val animalAreaId = intent.getLongExtra("ANIMAL_AREA", 0L)
        val animalFotoUrl = intent.getStringExtra("ANIMAL_FOTO")
        val esLocal = intent.getBooleanExtra("ES_LOCAL", false)

        // Mostrar datos básicos
        val origen = if (esLocal) "Local" else "API"
        txNombre.text = animalNombre
        txId.text = "ID ($origen): $animalId"
        txFecha.text = "Nacimiento: $animalFecha"
        txSexo.text = "Sexo: $animalSexo"

        // --- SOLUCIÓN FOTO ---
        if (!animalFotoUrl.isNullOrEmpty()) {
            // 1. Limpiamos el string de espacios invisibles
            val fotoLimpia = animalFotoUrl.trim()

            if (fotoLimpia.startsWith("http")) {
                // CASO A: Es un LINK (Supabase)
                imgFoto.load(fotoLimpia) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_background)
                }
            } else {
                // CASO B: Es Base64 (Foto nueva)
                val bitmap = CamaraUtils.convertirDeBase64ABitmap(fotoLimpia)
                if (bitmap != null) {
                    imgFoto.setImageBitmap(bitmap)
                } else {
                    imgFoto.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }

        // Cargar datos de API
        if (ValidarConexionWAN.isOnline(this)) {
            txEstado.text = "Cargando..."
            lifecycleScope.launch {
                VeterinariaRepository.fetchEspecieById(animalEspecieId).onSuccess { txEspecie.text = "Especie: ${it.nombre_comun}" }
                VeterinariaRepository.fetchHabitatById(animalHabitatId).onSuccess { txHabitat.text = "Hábitat: ${it.nombre}" }
                VeterinariaRepository.fetchEstadoById(animalEstadoId).onSuccess { txEstado.text = "Estado: ${it.estado}" }
                VeterinariaRepository.fetchAreaById(animalAreaId).onSuccess { txArea.text = "Área: ${it.nombre}" }
            }
        } else {
            txEstado.text = "Estado ID: $animalEstadoId"
            txEspecie.text = "Especie ID: $animalEspecieId"
            txHabitat.text = "Hábitat ID: $animalHabitatId"
            txArea.text = "Área ID: $animalAreaId"
        }

        // Botones CRUD
        if (esLocal) {
            btnEditar.visibility = View.GONE
            btnEliminar.visibility = View.GONE
        } else {
            btnEliminar.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Eliminar")
                    .setMessage("¿Eliminar a $animalNombre?")
                    .setPositiveButton("Sí") { _, _ ->
                        lifecycleScope.launch {
                            VeterinariaRepository.deleteAnimal(animalId).onSuccess {
                                Toast.makeText(this@DetalleAnimalActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                    .setNegativeButton("No", null).show()
            }

            btnEditar.setOnClickListener {
                val intentEditar = Intent(this, EditarAnimalActivity::class.java)
                intentEditar.putExtra("ID_ANIMAL", animalId)
                intentEditar.putExtra("NOMBRE", animalNombre)
                intentEditar.putExtra("FECHA", animalFecha)
                intentEditar.putExtra("FOTO", animalFotoUrl)
                intentEditar.putExtra("SEXO_ID", animalSexo)
                intentEditar.putExtra("ESPECIE_ID", animalEspecieId)
                intentEditar.putExtra("HABITAT_ID", animalHabitatId)
                intentEditar.putExtra("ESTADO_ID", animalEstadoId)
                intentEditar.putExtra("AREA_ID", animalAreaId)
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