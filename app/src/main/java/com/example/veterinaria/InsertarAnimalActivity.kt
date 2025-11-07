package com.example.veterinaria

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.api.InsertarAnimalAPI
import com.example.veterinaria.bd.AnimalesLocalRepository
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch

class InsertarAnimalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_insertar_animal)

        //--- LÓGICA DEL PROFE: FindViewById ---
        val edNombre: EditText = findViewById(R.id.ed_animal_nombre)
        val edFechaNac: EditText = findViewById(R.id.ed_animal_fecha_nac)
        val btnInsertarApi: Button = findViewById(R.id.btn_insertar_animal_api)
        val btnInsertarLocal: Button = findViewById(R.id.btn_insertar_animal_local)
        // val btnTomarFoto: Button = findViewById(R.id.btn_tomar_foto) // Ignorado por ahora

        //--- LÓGICA DEL PROFE: Listeners con lógica DENTRO ---
        // (Igual que el MainActivity4 de tu profe)

        btnInsertarApi.setOnClickListener {
            if (!ValidarConexionWAN.isOnline(this)) {
                Toast.makeText(this, "SIN CONEXIÓN. No se puede insertar.", Toast.LENGTH_SHORT).show()
            } else {
                // Lógica de leer formulario (como en MainActivity4)
                val nombre = edNombre.text.toString().trim()
                val fecha = edFechaNac.text.toString().trim()

                if (nombre.isEmpty() || fecha.isEmpty()) {
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    // Lógica de insertar (como en MainActivity4)
                    btnInsertarApi.isEnabled = false
                    InsertarAnimalAPI.insertarAnimal(
                        owner = this,
                        context = this,
                        nombre = nombre,
                        fechaNac = fecha,
                        onSuccess = {
                            edNombre.text?.clear()
                            edFechaNac.text?.clear()
                            btnInsertarApi.isEnabled = true
                            finish() // Cierra esta activity y vuelve a la lista
                        },
                        onError = {
                            btnInsertarApi.isEnabled = true
                        }
                    )
                }
            }
        }

        btnInsertarLocal.setOnClickListener {
            // Lógica de leer formulario
            val nombre = edNombre.text.toString().trim()
            val fecha = edFechaNac.text.toString().trim()

            if (nombre.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Lógica de insertar en SQLite
                btnInsertarLocal.isEnabled = false
                lifecycleScope.launch {
                    val res = AnimalesLocalRepository.insert(
                        context = this@InsertarAnimalActivity,
                        nombre = nombre,
                        fechaNac = fecha,
                        fotoUrl = null // Ignorado por ahora
                    )
                    res.onSuccess { rowId ->
                        Toast.makeText(this@InsertarAnimalActivity, "Guardado local (id=$rowId)", Toast.LENGTH_SHORT).show()
                        edNombre.text?.clear()
                        edFechaNac.text?.clear()
                        finish() // Cierra esta activity y vuelve a la lista
                    }.onFailure { e ->
                        Toast.makeText(this@InsertarAnimalActivity, "Error SQLite: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    btnInsertarLocal.isEnabled = true
                }
            }
        }

        // btnTomarFoto.setOnClickListener { ... } // Ignorado por ahora

        //--- CÓDIGO BASE DEL PROFE (al final) ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_insertar_animal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}