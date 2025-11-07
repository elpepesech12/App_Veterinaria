package com.example.veterinaria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veterinaria.funciones.ValidarConexionWAN

/**
 * Esta es la Activity de Login (la primera en lanzarse).
 * Tiene la lógica simple de Admin/Vet.
 */
class MainActivity : AppCompatActivity() {

    // Declaramos las vistas
    private lateinit var edUsuario: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referenciamos las vistas (al estilo del profe)
        edUsuario = findViewById(R.id.ed_usuario)
        edPassword = findViewById(R.id.ed_password)
        btnLogin = findViewById(R.id.btn_login)

        // Validamos conexión (como el profe)
        if (!ValidarConexionWAN.isOnline(this)) {
            Toast.makeText(this, "SIN CONEXION A INTERNET", Toast.LENGTH_LONG).show()
        }

        // Listener para el botón
        btnLogin.setOnClickListener {
            validarLogin()
        }
    }

    private fun validarLogin() {
        val usuario = edUsuario.text.toString().trim()
        val password = edPassword.text.toString().trim()

        // Lógica de validación simple (hardcoded)
        when (usuario) {
            "admin" -> {
                if (password == "admin") {
                    // Ir a la Home de Admin
                    Toast.makeText(this, "Bienvenido Admin", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminHomeActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra el Login
                } else {
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
            "vet" -> {
                if (password == "vet") {
                    // Ir a la Home de Veterinario
                    Toast.makeText(this, "Bienvenido Veterinario", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, VeterinarioHomeActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra el Login
                } else {
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}