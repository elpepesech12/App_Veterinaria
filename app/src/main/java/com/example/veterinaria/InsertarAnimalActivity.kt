package com.example.veterinaria

import android.Manifest // Importar Manifest
import android.content.pm.PackageManager // Importar PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView // Importar ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView // Importar PreviewView
import androidx.core.app.ActivityCompat // Importar ActivityCompat
import androidx.core.content.ContextCompat // Importar ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.api.Area
import com.example.veterinaria.api.Especie
import com.example.veterinaria.api.EstadoSalud
import com.example.veterinaria.api.Habitat
import com.example.veterinaria.api.InsertarAnimalAPI
import com.example.veterinaria.api.Sexo
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.bd.AnimalesLocalRepository
import com.example.veterinaria.camara.CamaraUtils // Importar tus helpers
import com.example.veterinaria.camara.CameraManager // Importar tus helpers
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch

class InsertarAnimalActivity : AppCompatActivity() {


    private var cameraManager: CameraManager? = null
    private var fotoEnBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_insertar_animal)

        // --- LÓGICA DEL PROFE: Verificación de Conexión ---
        if (!ValidarConexionWAN.isOnline(this)) {
            Toast.makeText(this, "SIN CONEXIÓN. Datos locales y API deshabilitados.", Toast.LENGTH_LONG).show()
        }

        //--- LÓGICA DEL PROFE: FindViewById (para todo) ---
        val edNombre: EditText = findViewById(R.id.ed_animal_nombre)
        val edFechaNac: EditText = findViewById(R.id.ed_animal_fecha_nac)

        // Spinners
        val spSexo: Spinner = findViewById(R.id.sp_animal_sexo)
        val spEspecie: Spinner = findViewById(R.id.sp_animal_especie)
        val spHabitat: Spinner = findViewById(R.id.sp_animal_habitat)
        val spEstado: Spinner = findViewById(R.id.sp_animal_estado)
        val spArea: Spinner = findViewById(R.id.sp_animal_area)

        // Botones
        val btnInsertarApi: Button = findViewById(R.id.btn_insertar_animal_api)
        val btnInsertarLocal: Button = findViewById(R.id.btn_insertar_animal_local)
        val btnVolver: ImageButton = findViewById(R.id.btn_volver_menu_insertar)

        val previewView: PreviewView = findViewById(R.id.preview_view_animal)
        val imgFotoCapturada: ImageView = findViewById(R.id.img_foto_capturada)
        val btnTomarFoto: Button = findViewById(R.id.btn_tomar_foto_animal)

        if (ValidarConexionWAN.isOnline(this)) {
            lifecycleScope.launch {
                VeterinariaRepository.fetchSexos().onSuccess { /* ... */ spSexo.adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, it) }
                VeterinariaRepository.fetchEspecies().onSuccess { /* ... */ spEspecie.adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, it) }
                VeterinariaRepository.fetchHabitats().onSuccess { /* ... */ spHabitat.adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, it) }
                VeterinariaRepository.fetchEstadosSalud().onSuccess { /* ... */ spEstado.adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, it) }
                VeterinariaRepository.fetchAreas().onSuccess { /* ... */ spArea.adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, it) }
            }
        } else {
            btnInsertarApi.isEnabled = false
            btnInsertarApi.text = "Inserción API (Sin Conexión)"
        }

        btnTomarFoto.isEnabled = false

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            setupCamera(previewView, btnTomarFoto)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                101
            )
        }

        btnVolver.setOnClickListener {
            finish()
        }

        btnTomarFoto.setOnClickListener {
            cameraManager?.takePhoto { bitmap ->
                if (bitmap != null) {
                    imgFotoCapturada.setImageBitmap(bitmap)
                    fotoEnBase64 = CamaraUtils.convertirDeBitMapABase64(bitmap)
                    Log.d("BASE64", fotoEnBase64!!.take(100) + "...")
                    Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al capturar", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(this, "Cámara no lista", Toast.LENGTH_SHORT).show()
        }

        btnInsertarApi.setOnClickListener {
            val nombre = edNombre.text.toString().trim()
            val fecha = edFechaNac.text.toString().trim()

            if (nombre.isEmpty() || fecha.isEmpty() || spSexo.selectedItem == null ||
                spEspecie.selectedItem == null || spHabitat.selectedItem == null ||
                spEstado.selectedItem == null || spArea.selectedItem == null) {
                val toast = Toast.makeText(this, "Completa todos los campos (Nombre, Fecha y Listas)", Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }

            val idSexo = (spSexo.selectedItem as Sexo).id_sexo
            val idEspecie = (spEspecie.selectedItem as Especie).id_especie
            val idHabitat = (spHabitat.selectedItem as Habitat).id_habitat
            val idEstado = (spEstado.selectedItem as EstadoSalud).id_estado_salud
            val idArea = (spArea.selectedItem as Area).id_area

            btnInsertarApi.isEnabled = false
            Toast.makeText(this, "Insertando en API...", Toast.LENGTH_SHORT).show()

            InsertarAnimalAPI.insertarAnimal(
                owner = this,
                context = this,
                nombre = nombre,
                fechaNac = fecha,
                idSexo = idSexo,
                idEspecie = idEspecie,
                idHabitat = idHabitat,
                idEstado = idEstado,
                idArea = idArea,
                fotoBase64 = fotoEnBase64,
                onSuccess = {
                    finish()
                },
                onError = {
                    btnInsertarApi.isEnabled = true
                }
            )
        }

        btnInsertarLocal.setOnClickListener {
            val nombre = edNombre.text.toString().trim()
            val fecha = edFechaNac.text.toString().trim()

            if (spArea.selectedItem == null || nombre.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(this, "Completa Nombre, Fecha y Área", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idArea = (spArea.selectedItem as Area).id_area

            btnInsertarLocal.isEnabled = false
            lifecycleScope.launch {
                val res = AnimalesLocalRepository.insert(
                    context = this@InsertarAnimalActivity,
                    nombre = nombre,
                    fechaNac = fecha,
                    fotoUrl = fotoEnBase64,
                    idArea = idArea
                )
                res.onSuccess {
                    finish()
                }.onFailure { e ->
                    Toast.makeText(this@InsertarAnimalActivity, "Error SQLite: ${e.message}", Toast.LENGTH_LONG).show()
                }
                btnInsertarLocal.isEnabled = true
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_insertar_animal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


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
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCamera(previewView: PreviewView, btnTomarFoto: Button) {
        cameraManager = CameraManager(this)
        cameraManager?.startCamera(previewView)
        btnTomarFoto.isEnabled = true
    }
}