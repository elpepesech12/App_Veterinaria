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
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.funciones.ValidarConexionWAN
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //--- Verificación de Conexión ---
        if (ValidarConexionWAN.isOnline(this)) {
            Log.d("LOGIN", "Dispositivo conectado a internet.")
        } else {
            Log.w("LOGIN", "Dispositivo sin conexión.")
            Toast.makeText(this, "SIN CONEXIÓN", Toast.LENGTH_SHORT).show()
        }

        //variables
        val edUsuario: EditText = findViewById(R.id.ed_usuario)
        val edPassword: EditText = findViewById(R.id.ed_password)
        val btnLogin: Button = findViewById(R.id.btn_login)
        val txMensaje: TextView = findViewById(R.id.tx_mensaje)

        // Variables para comparar
        val adminUser = "admin"
        val adminPass = "admin"

        btnLogin.setOnClickListener {
            val userEmail = edUsuario.text.toString().trim()
            val pass = edPassword.text.toString().trim()

            // 1. Check de Admin
            if (userEmail == adminUser && pass == adminPass) {
                // ROL ADMIN
                val intent = Intent(this, AdminMenuActivity::class.java)
                intent.putExtra("sesion", userEmail)
                startActivity(intent)
                Toast.makeText(this, "Bienvenido Admin: $userEmail", Toast.LENGTH_SHORT).show()
                txMensaje.text = "login OK"
            }

            else {
                // ROL VETERINARIO
                btnLogin.isEnabled = false
                txMensaje.text = "Validando..."

                // Usamos una corrutina para llamar a la API
                lifecycleScope.launch {
                    val loginResult = VeterinariaRepository.login(userEmail, pass)

                    // Verificamos el resultado de la API
                    loginResult.onSuccess { veterinario ->
                        txMensaje.text = "login VET OK"
                        Toast.makeText(this@MainActivity, "Bienvenido Dr. ${veterinario.nombre}", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@MainActivity, InicioVet::class.java)

                        // Enviamos los datos del veterinario a la siguiente pantalla
                        intent.putExtra("ID_VET", veterinario.id)
                        intent.putExtra("NOMBRE_VET", veterinario.nombre)

                        startActivity(intent)

                    }.onFailure { error ->
                        Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                        txMensaje.text = "login NO"
                    }

                    // 3. Vuelve a habilitar el botón (ya sea si falló o fue exitoso)
                    btnLogin.isEnabled = true
                }
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}