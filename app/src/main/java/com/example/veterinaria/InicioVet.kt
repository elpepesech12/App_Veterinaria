package com.example.veterinaria

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.veterinaria.PantallasVet.AlertasVet
import com.example.veterinaria.PantallasVet.CitasVet
import com.example.veterinaria.PantallasVet.HomeVet
import com.example.veterinaria.PantallasVet.RegistrosVet
import com.google.android.material.bottomnavigation.BottomNavigationView

class InicioVet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_vet)


        //BARRA DE NAV
        val barraNav: BottomNavigationView = findViewById(R.id.nav_bar)

        fun loadFragment(fragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }

        barraNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    loadFragment(HomeVet())
                    true
                }
                R.id.nav_citas -> {
                    loadFragment(CitasVet())
                    true
                }
                R.id.nav_registros -> {
                    loadFragment(RegistrosVet())
                    true
                }
                R.id.nav_alertas -> {
                    loadFragment(AlertasVet())
                    true
                }
                else -> false
            }
        }


        if (savedInstanceState == null) {
            barraNav.selectedItemId = R.id.nav_inicio
            loadFragment(HomeVet())
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}