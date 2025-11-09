package com.example.veterinaria.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinaria.R

class CitasAdapter(
    private var citas: List<CitaUI>
) : RecyclerView.Adapter<CitasAdapter.CitaViewHolder>() {

    inner class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHora: TextView = itemView.findViewById(R.id.txt_cita_hora)
        val txtTitulo: TextView = itemView.findViewById(R.id.txt_cita_titulo)
        val txtAnimal: TextView = itemView.findViewById(R.id.txt_cita_animal)
        val txtVeterinario: TextView = itemView.findViewById(R.id.txt_cita_veterinario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun getItemCount(): Int = citas.size

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]

        holder.txtHora.text = cita.hora.substring(0, 5) // Muestra "10:00"
        holder.txtTitulo.text = cita.tipoCita.nombre
        holder.txtAnimal.text = "con ${cita.animal.nombre}"

        holder.txtVeterinario.text = "Dr. ${cita.veterinario.nombre}"
    }

    fun actualizarDatos(nuevasCitas: List<CitaUI>) {
        this.citas = nuevasCitas
        notifyDataSetChanged()
    }
}