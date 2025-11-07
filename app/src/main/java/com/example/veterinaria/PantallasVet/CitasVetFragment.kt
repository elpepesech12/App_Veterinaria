package com.example.veterinaria.PantallasVet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.veterinaria.R

class CitasVetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_citas_vet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ¡AQUÍ VA TU CÓDIGO!
        // Aquí puedes usar findViewById para buscar cosas en tu XML.

        // Ejemplo:
        // val miTexto = view.findViewById<TextView>(R.id.texto_de_citas)
        // val miBoton = view.findViewById<Button>(R.id.boton_agendar_cita)

        // miTexto.text = "Citas programadas"
        // miBoton.setOnClickListener {
        //    // hacer algo...
        // }
    }

}