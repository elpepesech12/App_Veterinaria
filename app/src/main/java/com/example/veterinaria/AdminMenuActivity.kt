package com.example.veterinaria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.veterinaria.funciones.ValidarConexionWAN

class AdminMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_menu)


        if (ValidarConexionWAN.isOnline(this)) {
        } else {
            val toast = Toast.makeText(this, "SIN CONEXIÓN", Toast.LENGTH_SHORT)
            toast.show()
        }

        val txBienvenida: TextView = findViewById(R.id.tx_bienvenida_admin)
        val usuarioDesdeLogin = intent.getStringExtra("sesion")
        txBienvenida.text = "Bienvenido $usuarioDesdeLogin"


        val btnVerAnimales: Button = findViewById(R.id.btn_menu_ver_animales)
        val btnAgregarAnimal: Button = findViewById(R.id.btn_menu_agregar_animal)
        val btnCerrarSesion: Button = findViewById(R.id.btn_menu_cerrar_sesion)

        btnVerAnimales.setOnClickListener {
            val nuevaVentana = Intent(this, ListarAnimalesActivity::class.java)
            startActivity(nuevaVentana)
        }

        btnAgregarAnimal.setOnClickListener {
            // 2. Abre el formulario para insertar
            val nuevaVentana = Intent(this, InsertarAnimalActivity::class.java)
            startActivity(nuevaVentana)
        }

        btnCerrarSesion.setOnClickListener {
            val nuevaVentana = Intent(this, MainActivity::class.java)
            nuevaVentana.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(nuevaVentana)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_admin_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}