package com.example.veterinaria

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.api.*
import com.example.veterinaria.camara.CamaraUtils
import com.example.veterinaria.camara.CameraManager
import com.example.veterinaria.funciones.ValidarConexionWAN
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import coil.load

class EditarAnimalActivity : AppCompatActivity() {

    // Estas variables van aquí para que se puedan usar en toda la actividad
    // (Igual que en InsertarAnimalActivity)
    private var cameraManager: CameraManager? = null
    private var fotoEnBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Asegúrate de tener el layout correcto: R.layout.activity_editar_animal
        setContentView(R.layout.activity_editar_animal)

        // --- 1. Referencias a la UI ---
        val btnVolver: android.view.View = findViewById(R.id.btn_volver_editar)
        val txIdReferencia: TextView = findViewById(R.id.tx_id_referencia)

        val edNombre: TextInputEditText = findViewById(R.id.ed_animal_nombre)
        val edFechaNac: TextInputEditText = findViewById(R.id.ed_animal_fecha_nac)

        val spSexo: Spinner = findViewById(R.id.sp_animal_sexo)
        val spEspecie: Spinner = findViewById(R.id.sp_animal_especie)
        val spHabitat: Spinner = findViewById(R.id.sp_animal_habitat)
        val spEstado: Spinner = findViewById(R.id.sp_animal_estado)
        val spArea: Spinner = findViewById(R.id.sp_animal_area)

        val previewView: PreviewView = findViewById(R.id.preview_view_animal)
        val btnTomarFoto: Button = findViewById(R.id.btn_tomar_foto_animal)
        val imgFotoCapturada: ImageView = findViewById(R.id.img_foto_capturada)

        val btnGuardarCambios: Button = findViewById(R.id.btn_guardar_cambios)

        // --- 2. Recibir Datos del Intent (Vienen desde DetalleAnimalActivity) ---
        val idAnimal = intent.getLongExtra("ID_ANIMAL", -1L)

        // Si no llega ID, mostramos error y salimos
        if (idAnimal == -1L) {
            Toast.makeText(this, "Error al cargar ID del animal", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val nombreActual = intent.getStringExtra("NOMBRE") ?: ""
        val fechaActual = intent.getStringExtra("FECHA") ?: ""
        val fotoActual = intent.getStringExtra("FOTO")

        // IDs para seleccionar automáticamente en los spinners
        val idSexoActual = intent.getStringExtra("SEXO_ID")
        val idEspecieActual = intent.getLongExtra("ESPECIE_ID", -1)
        val idHabitatActual = intent.getLongExtra("HABITAT_ID", -1)
        val idEstadoActual = intent.getLongExtra("ESTADO_ID", -1)
        val idAreaActual = intent.getLongExtra("AREA_ID", -1)

        // --- 3. Mostrar los datos en pantalla ---
        txIdReferencia.text = "ID: $idAnimal"
        edNombre.setText(nombreActual)
        edFechaNac.setText(fechaActual)

        // Guardamos la foto actual en la variable por si el usuario NO toma una nueva
        fotoEnBase64 = fotoActual

        // Si ya tenía foto, la mostramos
// Cargar foto vieja si existe
        if (!fotoActual.isNullOrEmpty()) {
            if (fotoActual.startsWith("http")) {
                // Si es URL (viejo), usamos Coil
                imgFotoCapturada.load(fotoActual) {
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_background)
                }
            } else {
                // Si es Base64 (nuevo), usamos nuestro convertidor
                val bitmap = CamaraUtils.convertirDeBase64ABitmap(fotoActual)
                if (bitmap != null) {
                    imgFotoCapturada.setImageBitmap(bitmap)
                }
            }
        }

        // --- 4. Cargar Listas (Spinners) y seleccionar lo que ya tenía el animal ---
        if (!ValidarConexionWAN.isOnline(this)) {
            Toast.makeText(this, "SIN CONEXIÓN: No se puede editar.", Toast.LENGTH_LONG).show()
            btnGuardarCambios.isEnabled = false
        } else {
            lifecycleScope.launch {
                // Carga Sexos
                val listaSexos = VeterinariaRepository.fetchSexos().getOrDefault(emptyList())
                spSexo.adapter = ArrayAdapter(this@EditarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, listaSexos)
                val idxSexo = listaSexos.indexOfFirst { it.id_sexo == idSexoActual }
                if (idxSexo >= 0) spSexo.setSelection(idxSexo)

                // Carga Especies
                val listaEspecies = VeterinariaRepository.fetchEspecies().getOrDefault(emptyList())
                spEspecie.adapter = ArrayAdapter(this@EditarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, listaEspecies)
                val idxEspecie = listaEspecies.indexOfFirst { it.id_especie == idEspecieActual }
                if (idxEspecie >= 0) spEspecie.setSelection(idxEspecie)

                // Carga Habitats
                val listaHabitats = VeterinariaRepository.fetchHabitats().getOrDefault(emptyList())
                spHabitat.adapter = ArrayAdapter(this@EditarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, listaHabitats)
                val idxHabitat = listaHabitats.indexOfFirst { it.id_habitat == idHabitatActual }
                if (idxHabitat >= 0) spHabitat.setSelection(idxHabitat)

                // Carga Estados
                val listaEstados = VeterinariaRepository.fetchEstadosSalud().getOrDefault(emptyList())
                spEstado.adapter = ArrayAdapter(this@EditarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, listaEstados)
                val idxEstado = listaEstados.indexOfFirst { it.id_estado_salud == idEstadoActual }
                if (idxEstado >= 0) spEstado.setSelection(idxEstado)

                // Carga Areas
                val listaAreas = VeterinariaRepository.fetchAreas().getOrDefault(emptyList())
                spArea.adapter = ArrayAdapter(this@EditarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, listaAreas)
                val idxArea = listaAreas.indexOfFirst { it.id_area == idAreaActual }
                if (idxArea >= 0) spArea.setSelection(idxArea)
            }
        }

        // --- 5. Configuración de Cámara ---
        // Se usa cameraManager (variable de clase)
        btnTomarFoto.isEnabled = false
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupCamera(previewView, btnTomarFoto)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }

        btnTomarFoto.setOnClickListener {
            cameraManager?.takePhoto { bitmap ->
                if (bitmap != null) {
                    imgFotoCapturada.setImageBitmap(bitmap)
                    // Actualizamos fotoEnBase64 (variable de clase) con la NUEVA foto
                    fotoEnBase64 = CamaraUtils.convertirDeBitMapABase64(bitmap)
                    Toast.makeText(this, "Nueva foto capturada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al capturar", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(this, "Cámara no lista", Toast.LENGTH_SHORT).show()
        }

        // --- 6. Botón Volver ---
        btnVolver.setOnClickListener {
            finish()
        }

        // --- 7. Botón Guardar Cambios ---
        btnGuardarCambios.setOnClickListener {
            val nuevoNombre = edNombre.text.toString().trim()
            val nuevaFecha = edFechaNac.text.toString().trim()

            if (nuevoNombre.isEmpty() || nuevaFecha.isEmpty()) {
                Toast.makeText(this, "Nombre y Fecha obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (spSexo.selectedItem == null || spEspecie.selectedItem == null) {
                Toast.makeText(this, "Cargando datos...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el objeto con los datos nuevos (o viejos si no se tocaron)
            val requestUpdate = AnimalInsertRequest(
                nombre = nuevoNombre,
                fecha_nacimiento = nuevaFecha,
                id_sexo = (spSexo.selectedItem as Sexo).id_sexo,
                id_especie = (spEspecie.selectedItem as Especie).id_especie,
                id_habitat = (spHabitat.selectedItem as Habitat).id_habitat,
                id_estado_salud = (spEstado.selectedItem as EstadoSalud).id_estado_salud,
                id_area = (spArea.selectedItem as Area).id_area,
                foto_url = fotoEnBase64 // Usamos la variable de clase
            )

            btnGuardarCambios.isEnabled = false
            Toast.makeText(this, "Actualizando...", Toast.LENGTH_SHORT).show()

            lifecycleScope.launch {
                val resultado = VeterinariaRepository.updateAnimal(idAnimal, requestUpdate)

                resultado.onSuccess {
                    Toast.makeText(this@EditarAnimalActivity, "¡Animal Actualizado!", Toast.LENGTH_LONG).show()
                    finish()
                }.onFailure { e ->
                    Toast.makeText(this@EditarAnimalActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    btnGuardarCambios.isEnabled = true
                }
            }
        }

        // --- 8. Ajustes visuales finales ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_editar_animal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // --- MÉTODOS AUXILIARES (Fuera del onCreate, pero dentro de la clase) ---

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            val previewView: PreviewView = findViewById(R.id.preview_view_animal)
            val btnTomarFoto: Button = findViewById(R.id.btn_tomar_foto_animal)
            setupCamera(previewView, btnTomarFoto)
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCamera(previewView: PreviewView, btnTomarFoto: Button) {
        cameraManager = CameraManager(this)
        cameraManager?.startCamera(previewView)
        btnTomarFoto.isEnabled = true
    }
}