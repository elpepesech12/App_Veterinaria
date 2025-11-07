package com.example.veterinaria

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veterinaria.funciones.CargarAnimalesApi
import com.example.veterinaria.funciones.LeerAnimalesLocal
import com.example.veterinaria.funciones.ValidarConexionWAN

/**
 * Pantalla principal para el Veterinario.
 * Muestra el ListView (SIN botón de agregar).
 */
class VeterinarioHomeActivity : AppCompatActivity() {

    private lateinit var lvAnimales: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_veterinario_home)

        lvAnimales = findViewById(R.id.lv_animales_vet)
    }

    override fun onResume() {
        super.onResume()
        // Cargamos los datos cada vez que la pantalla se muestra (refresca)
        cargarDatos()
    }

    private fun cargarDatos() {
        if (ValidarConexionWAN.isOnline(this)) {
            // Hay internet: Cargar desde API
            Toast.makeText(this, "Cargando desde API...", Toast.LENGTH_SHORT).show()
            CargarAnimalesApi.cargarAnimales(this, lvAnimales)
        } else {
            // No hay internet: Cargar desde BD Local
            Toast.makeText(this, "SIN CONEXIÓN. Cargando datos locales.", Toast.LENGTH_LONG).show()
            LeerAnimalesLocal.cargarEnListView(this, this, lvAnimales)
        }
    }
}