package com.example.veterinaria.funciones.veterinario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinaria.R
import com.example.veterinaria.api.FichaMedicaLectura

class HistorialAdapter(private var lista: List<FichaMedicaLectura>) :
    RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txAnimal: TextView = view.findViewById(R.id.tx_item_animal)
        val txFecha: TextView = view.findViewById(R.id.tx_item_fecha)
        val txDiag: TextView = view.findViewById(R.id.tx_item_diagnostico)
        val txVet: TextView = view.findViewById(R.id.tx_item_vet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.txAnimal.text = item.animal?.nombre ?: "Desconocido"
        holder.txFecha.text = item.fecha_realizada
        holder.txDiag.text = item.diagnostico_general

        val nombreVet = item.veterinario?.nombre ?: ""
        val apellidoVet = item.veterinario?.apellido_p ?: ""
        holder.txVet.text = "Vet: $nombreVet $apellidoVet"
    }

    override fun getItemCount() = lista.size

    fun updateData(nuevaLista: List<FichaMedicaLectura>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}