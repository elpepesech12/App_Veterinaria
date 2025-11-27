package com.example.veterinaria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.veterinaria.funciones.CargarAnimalesApi
import com.example.veterinaria.funciones.LeerAnimalesLocal
import com.example.veterinaria.funciones.ValidarConexionWAN
import com.example.veterinaria.api.Animal
import com.example.veterinaria.bd.AnimalLocal

class ListarAnimalesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_animales)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_listar_animales)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ValidarConexionWAN.isOnline(this)) {
            Toast.makeText(this, "CON CONEXIÓN", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SIN CONEXIÓN. Datos locales.", Toast.LENGTH_LONG).show()
        }

        val lvAnimales: ListView = findViewById(R.id.lv_animales_admin)
        val btnVolver: Button = findViewById(R.id.btn_volver_al_menu)

        btnVolver.setOnClickListener {
            finish()
        }

        lvAnimales.setOnItemClickListener { parent, _, position, _ ->
            val item = parent.getItemAtPosition(position)
            val intent = Intent(this, DetalleAnimalActivity::class.java)

            if (item is Animal) {
                intent.putExtra("ANIMAL_ID", item.id)
                intent.putExtra("ANIMAL_NOMBRE", item.nombre)
                intent.putExtra("ANIMAL_FECHA", item.fechaNacimiento)
                intent.putExtra("ANIMAL_SEXO", item.idSexo)
                intent.putExtra("ANIMAL_ESPECIE", item.idEspecie)
                intent.putExtra("ANIMAL_HABITAT", item.idHabitat)
                intent.putExtra("ANIMAL_ESTADO", item.idEstadoSalud)
                intent.putExtra("ANIMAL_AREA", item.idArea)
                intent.putExtra("ES_LOCAL", false)
                intent.putExtra("ANIMAL_FOTO", item.fotoUrl)

                // --- AGREGADO: Pasar el estado activo ---
                intent.putExtra("ANIMAL_ACTIVO", item.activo)

                startActivity(intent)

            } else if (item is AnimalLocal) {
                intent.putExtra("ANIMAL_ID", item.id.toLong())
                intent.putExtra("ANIMAL_NOMBRE", item.nombre)
                intent.putExtra("ANIMAL_FECHA", item.fechaNacimiento)
                intent.putExtra("ANIMAL_SEXO", item.idSexo)
                intent.putExtra("ANIMAL_ESPECIE", item.idEspecie.toLong())
                intent.putExtra("ANIMAL_HABITAT", item.idHabitat.toLong())
                intent.putExtra("ANIMAL_ESTADO", item.idEstadoSalud.toLong())
                intent.putExtra("ANIMAL_AREA", item.idArea.toLong())
                intent.putExtra("ES_LOCAL", true)
                intent.putExtra("ANIMAL_FOTO", item.fotoUrl)

                // En local asumimos true si no tienes el campo en la BD interna
                intent.putExtra("ANIMAL_ACTIVO", true)

                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: Objeto no reconocido", Toast.LENGTH_SHORT).show()
            }
        }

        cargarDatos(lvAnimales)
    }

    private fun cargarDatos(listView: ListView) {
        if (ValidarConexionWAN.isOnline(this)) {
            CargarAnimalesApi.cargarAnimales(this, listView)
        } else {
            LeerAnimalesLocal.cargarEnListView(this, this, listView)
        }
    }

    override fun onResume() {
        super.onResume()
        val lvAnimales: ListView = findViewById(R.id.lv_animales_admin)
        cargarDatos(lvAnimales)
    }
}