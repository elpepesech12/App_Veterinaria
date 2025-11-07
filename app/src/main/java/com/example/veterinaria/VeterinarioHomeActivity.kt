package com.example.veterinaria

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.veterinaria.funciones.CargarAnimalesApi
import com.example.veterinaria.funciones.LeerAnimalesLocal
import com.example.veterinaria.funciones.ValidarConexionWAN
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class VeterinarioHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_veterinario_home)

        //--- LÓGICA DEL PROFE: Verificación de Conexión (al inicio) ---
        if (ValidarConexionWAN.isOnline(this)) {
            Toast.makeText(this, "CON CONEXIÓN", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SIN CONEXIÓN. Cargando datos locales.", Toast.LENGTH_LONG).show()
        }

        //--- LÓGICA DEL PROFE: FindViewById (después de setContentView) ---
        val lvAnimales: ListView = findViewById(R.id.lv_animales_vet)

        //--- LÓGICA DEL PROFE: Cargar datos (directo en onCreate) ---
        if (ValidarConexionWAN.isOnline(this)) {
            CargarAnimalesApi.cargarAnimales(this, lvAnimales)
        } else {
            LeerAnimalesLocal.cargarEnListView(this, this, lvAnimales)
        }

        //--- CÓDIGO BASE DEL PROFE (al final) ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_vet_home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Sobrescribimos onResume para recargar los datos
     */
    override fun onResume() {
        super.onResume()
        val lvAnimales: ListView = findViewById(R.id.lv_animales_vet)
        if (ValidarConexionWAN.isOnline(this)) {
            CargarAnimalesApi.cargarAnimales(this, lvAnimales)
        } else {
            LeerAnimalesLocal.cargarEnListView(this, this, lvAnimales)
        }
    }
}