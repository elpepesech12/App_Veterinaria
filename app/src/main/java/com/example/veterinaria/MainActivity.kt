package com.example.veterinaria

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.veterinaria.funciones.ValidarConexionWAN

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // <-- CÓDIGO BASE DEL PROFE
        setContentView(R.layout.activity_main) // <-- CÓDIGO BASE DEL PROFE

        //--- LÓGICA DEL PROFE: Verificación de Conexión ---
        if (ValidarConexionWAN.isOnline(this)) {
            Log.d("LOGIN", "Dispositivo conectado a internet.")
        } else {
            Log.w("LOGIN", "Dispositivo sin conexión.")
            Toast.makeText(this, "SIN CONEXIÓN", Toast.LENGTH_SHORT).show()
        }

        //--- LÓGICA DEL PROFE: Inicializar variables ---
        val edUsuario: EditText = findViewById(R.id.ed_usuario) // Tu ID
        val edPassword: EditText = findViewById(R.id.ed_password) // Tu ID
        val btnLogin: Button = findViewById(R.id.btn_login)
        val txMensaje: TextView = findViewById(R.id.tx_mensaje)

        // Variables para comparar
        val adminUser = "admin"
        val adminPass = "admin"
        val vetUser = "vet"
        val vetPass = "vet"

        btnLogin.setOnClickListener {
            val user = edUsuario.text.toString()
            val pass = edPassword.text.toString()

            if (user == adminUser && pass == adminPass) {
                // ROL ADMIN
                val intent = Intent(this, AdminHomeActivity::class.java)
                intent.putExtra("sesion", user)
                startActivity(intent)
                Toast.makeText(this, "Bienvenido Admin: $user", Toast.LENGTH_SHORT).show()
                txMensaje.text = "login OK"

            } else if (user == vetUser && pass == vetPass) {
                // ROL VETERINARIO
                val intent = Intent(this, VeterinarioHomeActivity::class.java)
                intent.putExtra("sesion", user)
                startActivity(intent)
                Toast.makeText(this, "Bienvenido Vet: $user", Toast.LENGTH_SHORT).show()
                txMensaje.text = "login OK"

            } else {
                // ERROR
                Toast.makeText(this, "Error: Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                txMensaje.text = "login NO"
            }
        }

        //--- CÓDIGO BASE DEL PROFE (EdgeToEdge) ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //--- FIN CÓDIGO BASE ---
    }
}