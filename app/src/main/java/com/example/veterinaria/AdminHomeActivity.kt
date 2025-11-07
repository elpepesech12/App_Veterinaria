package com.example.veterinaria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veterinaria.funciones.CargarAnimalesApi
import com.example.veterinaria.funciones.LeerAnimalesLocal
import com.example.veterinaria.funciones.ValidarConexionWAN

/**
 * Pantalla principal para el Administrador.
 * Muestra el ListView y el botón para agregar.
 */
class AdminHomeActivity : AppCompatActivity() {

    private lateinit var lvAnimales: ListView
    private lateinit var btnIrAInsertar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        lvAnimales = findViewById(R.id.lv_animales_admin)
        btnIrAInsertar = findViewById(R.id.btn_ir_a_insertar)

        btnIrAInsertar.setOnClickListener {
            val intent = Intent(this, InsertarAnimalActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Cargamos los datos cada vez que la pantalla se muestra (refresca)
        cargarDatos()
    }

    private fun cargarDatos() {
        if (ValidarConexionWAN.isOnline(this)) {
            // Hay internet: Cargar desde API (y guardar en local)
            Toast.makeText(this, "Cargando desde API...", Toast.LENGTH_SHORT).show()
            CargarAnimalesApi.cargarAnimales(this, lvAnimales)
        } else {
            // No hay internet: Cargar desde BD Local
            Toast.makeText(this, "SIN CONEXIÓN. Cargando datos locales.", Toast.LENGTH_LONG).show()
            LeerAnimalesLocal.cargarEnListView(this, this, lvAnimales)
        }
    }
}