package com.example.veterinaria

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.api.InsertarAnimalAPI
import com.example.veterinaria.bd.AnimalesLocalRepository
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch

class InsertarAnimalActivity : AppCompatActivity() {

    private lateinit var edNombre: EditText
    private lateinit var edFechaNac: EditText
    private lateinit var btnInsertarApi: Button
    private lateinit var btnInsertarLocal: Button
    private lateinit var btnTomarFoto: Button
    private lateinit var previewView: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertar_animal)

        edNombre = findViewById(R.id.ed_animal_nombre)
        edFechaNac = findViewById(R.id.ed_animal_fecha_nac)
        btnInsertarApi = findViewById(R.id.btn_insertar_animal_api)
        btnInsertarLocal = findViewById(R.id.btn_insertar_animal_local)
        btnTomarFoto = findViewById(R.id.btn_tomar_foto)
        previewView = findViewById(R.id.camera_preview)

        btnInsertarApi.setOnClickListener { onInsertApiClick() }
        btnInsertarLocal.setOnClickListener { onInsertSQLiteClick() }
        //btnTomarFoto.setOnClickListener { iniciarCamara() }

        // TODO: Requisito 2 (CámaraX)

    }

    private fun leerFormulario(): Pair<String, String>? {
        val nombre = edNombre.text.toString().trim()
        val fecha = edFechaNac.text.toString().trim() // Ej: "2025-01-30"

        if (nombre.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return null
        }
        return Pair(nombre, fecha)
    }

    // Botón 1: INSERT a la API (como el profe)
    private fun onInsertApiClick() {
        if (!ValidarConexionWAN.isOnline(this)) {
            Toast.makeText(this, "SIN CONEXIÓN. No se puede insertar.", Toast.LENGTH_SHORT).show()
            return
        }

        val data = leerFormulario() ?: return
        val (nombre, fecha) = data

        btnInsertarApi.isEnabled = false

        InsertarAnimalAPI.insertarAnimal(
            owner = this,
            context = this,
            nombre = nombre,
            fechaNac = fecha,
            onSuccess = {
                limpiarFormulario()
                btnInsertarApi.isEnabled = true
                finish() // Cierra esta activity y vuelve a la lista
            },
            onError = {
                btnInsertarApi.isEnabled = true
            }
        )
    }

    // Botón 2: Guardar en SQLite (como el profe)
    private fun onInsertSQLiteClick() {
        val data = leerFormulario() ?: return
        val (nombre, fecha) = data

        btnInsertarLocal.isEnabled = false

        lifecycleScope.launch { // Corutina para la BD
            val res = AnimalesLocalRepository.insert(
                context = this@InsertarAnimalActivity,
                nombre = nombre,
                fechaNac = fecha,
                fotoUrl = null // No tenemos foto al insertar localmente
            )
            res.onSuccess { rowId ->
                Toast.makeText(this@InsertarAnimalActivity, "Guardado local (id=$rowId)", Toast.LENGTH_SHORT).show()
                limpiarFormulario()
                finish() // Cierra esta activity y vuelve a la lista
            }.onFailure { e ->
                Toast.makeText(this@InsertarAnimalActivity, "Error SQLite: ${e.message}", Toast.LENGTH_LONG).show()
            }
            btnInsertarLocal.isEnabled = true
        }
    }

    private fun limpiarFormulario() {
        edNombre.text?.clear()
        edFechaNac.text?.clear()
    }
}