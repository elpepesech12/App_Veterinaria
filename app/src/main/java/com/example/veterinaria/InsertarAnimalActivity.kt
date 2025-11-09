package com.example.veterinaria

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch

class InsertarAnimalActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_insertar_animal)

        if (!ValidarConexionWAN.isOnline(this)) {
            Toast.makeText(this, "SIN CONEXIÓN. Datos locales y API deshabilitados.", Toast.LENGTH_LONG).show()
        }

        val edNombre: EditText = findViewById(R.id.ed_animal_nombre)
        val edFechaNac: EditText = findViewById(R.id.ed_animal_fecha_nac)

        // Spinners (definidos como 'val' locales, igual que tu profe)
        val spSexo: Spinner = findViewById(R.id.sp_animal_sexo)
        val spEspecie: Spinner = findViewById(R.id.sp_animal_especie)
        val spHabitat: Spinner = findViewById(R.id.sp_animal_habitat)
        val spEstado: Spinner = findViewById(R.id.sp_animal_estado)
        val spArea: Spinner = findViewById(R.id.sp_animal_area)

        // Botones
        val btnInsertarApi: Button = findViewById(R.id.btn_insertar_animal_api)
        val btnInsertarLocal: Button = findViewById(R.id.btn_insertar_animal_local)
        val btnVolver: Button = findViewById(R.id.btn_volver_menu_insertar)


        if (ValidarConexionWAN.isOnline(this)) {

            lifecycleScope.launch {

                VeterinariaRepository.fetchSexos().onSuccess { lista ->
                    val adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, lista)
                    spSexo.adapter = adapter // 'spSexo' es visible porque está en el scope de onCreate
                }.onFailure {
                    Toast.makeText(this@InsertarAnimalActivity, "Error al cargar Sexos", Toast.LENGTH_SHORT).show()
                }

                // 2. Cargar Especies
                VeterinariaRepository.fetchEspecies().onSuccess { lista ->
                    val adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, lista)
                    spEspecie.adapter = adapter
                }.onFailure {
                    Toast.makeText(this@InsertarAnimalActivity, "Error al cargar Especies", Toast.LENGTH_SHORT).show()
                }

                // 3. Cargar Hábitats
                VeterinariaRepository.fetchHabitats().onSuccess { lista ->
                    val adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, lista)
                    spHabitat.adapter = adapter
                }.onFailure {
                    Toast.makeText(this@InsertarAnimalActivity, "Error al cargar Hábitats", Toast.LENGTH_SHORT).show()
                }

                // 4. Cargar Estados de Salud
                VeterinariaRepository.fetchEstadosSalud().onSuccess { lista ->
                    val adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, lista)
                    spEstado.adapter = adapter
                }.onFailure {
                    Toast.makeText(this@InsertarAnimalActivity, "Error al cargar Estados", Toast.LENGTH_SHORT).show()
                }

                // 5. Cargar Áreas
                VeterinariaRepository.fetchAreas().onSuccess { lista ->
                    val adapter = ArrayAdapter(this@InsertarAnimalActivity, android.R.layout.simple_spinner_dropdown_item, lista)
                    spArea.adapter = adapter
                }.onFailure {
                    Toast.makeText(this@InsertarAnimalActivity, "Error al cargar Áreas", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            btnInsertarApi.isEnabled = false
            btnInsertarApi.text = "Inserción API (Sin Conexión)"
        }



        btnVolver.setOnClickListener {
            finish() // Cierra esta activity y vuelve al menú
        }

        btnInsertarApi.setOnClickListener {
            // 1. Validar campos
            val nombre = edNombre.text.toString().trim()
            val fecha = edFechaNac.text.toString().trim()

            if (nombre.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(this, "Completa Nombre y Fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Validar Spinners (que no estén vacíos)
            // (Accedemos a 'spSexo', 'spEspecie', etc. porque están definidas arriba)
            if (spSexo.selectedItem == null || spEspecie.selectedItem == null ||
                spHabitat.selectedItem == null || spEstado.selectedItem == null || spArea.selectedItem == null) {
                Toast.makeText(this, "Espera a que carguen las listas desplegables", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Obtener IDs de los Spinners
            val idSexo = (spSexo.selectedItem as Sexo).id_sexo
            val idEspecie = (spEspecie.selectedItem as Especie).id_especie
            val idHabitat = (spHabitat.selectedItem as Habitat).id_habitat
            val idEstado = (spEstado.selectedItem as EstadoSalud).id_estado_salud
            val idArea = (spArea.selectedItem as Area).id_area

            // 4. Deshabilitar botón y llamar a la API
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
                onSuccess = {
                    edNombre.text?.clear()
                    edFechaNac.text?.clear()
                    btnInsertarApi.isEnabled = true
                    finish() // Cierra esta activity y vuelve a la lista
                },
                onError = {
                    btnInsertarApi.isEnabled = true // Re-habilitamos si falla
                }
            )
        }

        btnInsertarLocal.setOnClickListener {
            // Lógica de leer formulario
            val nombre = edNombre.text.toString().trim()
            val fecha = edFechaNac.text.toString().trim()

            if (spArea.selectedItem == null) {
                Toast.makeText(this, "Espera a que cargue la lista de Áreas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // La BD Local solo pide nombre, fecha, foto y AREA_ID
            val idArea = (spArea.selectedItem as Area).id_area

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
                        fotoUrl = null, // Ignorado por ahora
                        idArea = idArea // Pasamos el ID del Spinner
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_insertar_animal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}