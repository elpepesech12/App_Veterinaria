package com.example.veterinaria

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.camara.CamaraUtils
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch

class DetalleAnimalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_animal)

        val btn_detalle_volver: ImageButton = findViewById(R.id.btn_detalle_volver)
        val img_detalle_foto: ImageView = findViewById(R.id.img_detalle_foto)
        val tx_detalle_nombre: TextView = findViewById(R.id.tx_detalle_nombre)
        val tx_detalle_id: TextView = findViewById(R.id.tx_detalle_id)
        val tx_detalle_fecha_nac: TextView = findViewById(R.id.tx_detalle_fecha_nac)
        val tx_detalle_sexo: TextView = findViewById(R.id.tx_detalle_sexo)
        val tx_detalle_estado: TextView = findViewById(R.id.tx_detalle_estado)
        val tx_detalle_especie: TextView = findViewById(R.id.tx_detalle_especie)
        val tx_detalle_habitat: TextView = findViewById(R.id.tx_detalle_habitat)
        val tx_detalle_area: TextView = findViewById(R.id.tx_detalle_area)

        btn_detalle_volver.setOnClickListener {
            finish()
        }

        // cargar datos
        val animalId = intent.getLongExtra("ANIMAL_ID", 0L)
        val animalNombre = intent.getStringExtra("ANIMAL_NOMBRE")
        val animalFecha = intent.getStringExtra("ANIMAL_FECHA")
        val animalSexo = intent.getStringExtra("ANIMAL_SEXO")
        val animalEspecieId = intent.getLongExtra("ANIMAL_ESPECIE", 0L)
        val animalHabitatId = intent.getLongExtra("ANIMAL_HABITAT", 0L)
        val animalEstadoId = intent.getLongExtra("ANIMAL_ESTADO", 0L)
        val animalAreaId = intent.getLongExtra("ANIMAL_AREA", 0L)
        val esLocal = intent.getBooleanExtra("ES_LOCAL", false)
        val animalFotoUrl = intent.getStringExtra("ANIMAL_FOTO")

        // datos de texto
        val tipoId = if (esLocal) "Local" else "API"

        tx_detalle_nombre.text = animalNombre
        tx_detalle_id.text = "ID ($tipoId): $animalId"
        tx_detalle_fecha_nac.text = "Nacimiento: $animalFecha"
        tx_detalle_sexo.text = "Sexo ID: $animalSexo"

        // mostrar la foto
        val bitmapFoto = CamaraUtils.convertirDeBase64ABitmap(animalFotoUrl)
        if (bitmapFoto != null) {
            img_detalle_foto.setImageBitmap(bitmapFoto)
        } else {
            Log.w("DetalleAnimal", "No se pudo cargar la foto o es nula")
        }

        // buscar nombres de id
        if (ValidarConexionWAN.isOnline(this)) {
            // ponemos un placeholder mientras carga
            tx_detalle_estado.text = "Estado: (Cargando...)"
            tx_detalle_especie.text = "Especie: (Cargando...)"
            tx_detalle_habitat.text = "Hábitat: (Cargando...)"
            tx_detalle_area.text = "Área: (Cargando...)"

            lifecycleScope.launch {
                VeterinariaRepository.fetchEspecieById(animalEspecieId).onSuccess {
                    tx_detalle_especie.text = "Especie: ${it.nombre_comun}"
                }.onFailure {
                    tx_detalle_especie.text = "Especie ID: $animalEspecieId (Error)"
                }
            }
            lifecycleScope.launch {
                VeterinariaRepository.fetchHabitatById(animalHabitatId).onSuccess {
                    tx_detalle_habitat.text = "Hábitat: ${it.nombre}"
                }.onFailure {
                    tx_detalle_habitat.text = "Hábitat ID: $animalHabitatId (Error)"
                }
            }
            lifecycleScope.launch {
                VeterinariaRepository.fetchEstadoById(animalEstadoId).onSuccess {
                    tx_detalle_estado.text = "Estado: ${it.estado}"
                }.onFailure {
                    tx_detalle_estado.text = "Estado ID: $animalEstadoId (Error)"
                }
            }
            lifecycleScope.launch {
                VeterinariaRepository.fetchAreaById(animalAreaId).onSuccess {
                    tx_detalle_area.text = "Área: ${it.nombre}"
                }.onFailure {
                    tx_detalle_area.text = "Área ID: $animalAreaId (Error)"
                }
            }
        } else {
            tx_detalle_estado.text = "Estado ID: $animalEstadoId"
            tx_detalle_especie.text = "Especie ID: $animalEspecieId"
            tx_detalle_habitat.text = "Hábitat ID: $animalHabitatId"
            tx_detalle_area.text = "Área ID: $animalAreaId"
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_detalle_animal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}