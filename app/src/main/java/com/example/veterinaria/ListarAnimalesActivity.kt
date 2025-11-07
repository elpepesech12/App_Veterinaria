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

// 1. Renombramos la clase
class ListarAnimalesActivity : AppCompatActivity() {

    // Variable de clase para el ListView, para usarla en onResume
    private lateinit var lvAnimales: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 2. Apunta al nuevo layout
        setContentView(R.layout.activity_listar_animales)

        //--- LÓGICA DEL PROFE: Verificación de Conexión ---
        if (ValidarConexionWAN.isOnline(this)) {
            val toast = Toast.makeText(this, "CON CONEXIÓN", Toast.LENGTH_SHORT)
            toast.show()
        } else {
            val toast = Toast.makeText(this, "SIN CONEXIÓN. Cargando datos locales.", Toast.LENGTH_LONG)
            toast.show()
        }

        //--- LÓGICA DEL PROFE: FindViewById ---
        // 3. Hacemos la variable de clase
        lvAnimales = findViewById(R.id.lv_animales_admin)
        // 4. Agregamos el botón de volver
        val btnVolver: Button = findViewById(R.id.btn_volver_al_menu)
        // 5. El botón de insertar ya no existe aquí
        // val btnIrAInsertar: Button = findViewById(R.id.btn_ir_a_insertar)

        //--- LÓGICA DEL PROFE: Listeners ---

        // 6. Listener para el botón Volver
        btnVolver.setOnClickListener {
            finish() // Cierra esta activity y vuelve al menú
        }

        // 7. Listener para ELIMINAR (como en MainActivity2)
        lvAnimales.setOnItemClickListener { parent, view, position, id ->
            val itemSeleccionado = parent.getItemAtPosition(position).toString()

            // Aquí puedes mostrar un Toast o un Diálogo de confirmación
            // Por ahora, solo un Toast para confirmar la selección
            val toast = Toast.makeText(this, "Opción Eliminar para: $itemSeleccionado", Toast.LENGTH_SHORT)
            toast.show()

            // --- LÓGICA PENDIENTE DE ELIMINACIÓN ---
            // Aquí deberías llamar a la función para eliminar de la API o SQLite
            // (ej. EliminarAnimalApi.eliminar(this, idAnimal) )
            // Y después de eliminar, volver a cargar los datos:
            // cargarDatos()
            // --- FIN LÓGICA PENDIENTE ---
        }

        // 8. El listener de "btnIrAInsertar" se elimina

        //--- LÓGICA DEL PROFE: Cargar datos (igual que tenías) ---
        // Esta parte la dejamos igual que tu código original
        cargarDatos()

        //--- CÓDIGO BASE DEL PROFE (al final) ---
        // 9. Asegúrate que el ID sea el del nuevo layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_listar_animales)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Función privada para cargar datos
     */
    private fun cargarDatos() {
        if (ValidarConexionWAN.isOnline(this)) {
            CargarAnimalesApi.cargarAnimales(this, lvAnimales)
        } else {
            LeerAnimalesLocal.cargarEnListView(this, this, lvAnimales)
        }
    }

    /**
     * Sobrescribimos onResume para recargar los datos
     * (Esto es necesario para que la lista se actualice
     * al volver de "Insertar" o al "Eliminar")
     */
    override fun onResume() {
        super.onResume()
        cargarDatos()
    }
}