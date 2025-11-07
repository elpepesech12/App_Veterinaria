package com.example.veterinaria.PantallasVet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.veterinaria.R

class RegistrosVet : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registros_vet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí es donde pones tus findViewById y la lógica de la pantalla.

        // Ejemplo:
        // val textoBienvenida = view.findViewById<TextView>(R.id.texto_bienvenida_home)
        // val botonVerMascotas = view.findViewById<Button>(R.id.boton_ver_mascotas)

        // textoBienvenida.text = "¡Bienvenido a tu veterinaria!"

        // botonVerMascotas.setOnClickListener {
        //    // Hacer algo al presionar el botón
        // }
    }
}