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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)

        //--- LÓGICA DEL PROFE: Verificación de Conexión (al inicio) ---
        if (ValidarConexionWAN.isOnline(this)) {
            Toast.makeText(this, "CON CONEXIÓN", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SIN CONEXIÓN. Cargando datos locales.", Toast.LENGTH_LONG).show()
        }

        //--- LÓGICA DEL PROFE: FindViewById (después de setContentView) ---
        val lvAnimales: ListView = findViewById(R.id.lv_animales_admin)
        val btnIrAInsertar: Button = findViewById(R.id.btn_ir_a_insertar)

        //--- LÓGICA DEL PROFE: Listeners ---
        btnIrAInsertar.setOnClickListener {
            val intent = Intent(this, InsertarAnimalActivity::class.java)
            startActivity(intent)
        }

        //--- LÓGICA DEL PROFE: Cargar datos (directo en onCreate) ---
        // Se elimina onResume y cargarDatos() y se pone la lógica aquí
        // Re-validamos por si el usuario activó/desactivó el wifi mientras estaba en la app
        if (ValidarConexionWAN.isOnline(this)) {
            CargarAnimalesApi.cargarAnimales(this, lvAnimales)
        } else {
            LeerAnimalesLocal.cargarEnListView(this, this, lvAnimales)
        }

        //--- CÓDIGO BASE DEL PROFE (al final) ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_admin_home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Sobrescribimos onResume para recargar los datos
     * (El profe carga todo en onCreate, pero si no hacemos esto,
     * la lista no se actualizará al volver de "Insertar")
     */
    override fun onResume() {
        super.onResume()
        // Esta es una pequeña mejora a la lógica del profe,
        // para que la lista se refresque al volver.
        val lvAnimales: ListView = findViewById(R.id.lv_animales_admin)
        if (ValidarConexionWAN.isOnline(this)) {
            CargarAnimalesApi.cargarAnimales(this, lvAnimales)
        } else {
            LeerAnimalesLocal.cargarEnListView(this, this, lvAnimales)
        }
    }
}