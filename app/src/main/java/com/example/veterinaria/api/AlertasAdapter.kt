package com.example.veterinaria.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinaria.R
import com.example.veterinaria.api.AlertaUI

class AlertasAdapter(
    private var alertas: List<AlertaUI>
) : RecyclerView.Adapter<AlertasAdapter.AlertaViewHolder>() {

    // ViewHolder: "Sostiene" las vistas de un solo item
    inner class AlertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcono: ImageView = itemView.findViewById(R.id.img_alerta_icon)
        val txtTag: TextView = itemView.findViewById(R.id.txt_alerta_tag)
        val txtTitulo: TextView = itemView.findViewById(R.id.txt_alerta_titulo)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txt_alerta_descripcion)
        val txtTimestamp: TextView = itemView.findViewById(R.id.txt_alerta_timestamp)
        val txtArea: TextView = itemView.findViewById(R.id.txt_alerta_area)
    }

    // Se llama cuando se necesita crear un nuevo ViewHolder (fila)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertaViewHolder {
        // (Asegúrate de tener un 'item_alerta.xml' en tu layout)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alerta, parent, false)
        return AlertaViewHolder(view)
    }

    // Devuelve la cantidad de items
    override fun getItemCount(): Int = alertas.size

    // Conecta los datos (AlertaUI) con las vistas (ViewHolder)
    override fun onBindViewHolder(holder: AlertaViewHolder, position: Int) {
        val alerta = alertas[position]
        val context = holder.itemView.context

        // Seteamos textos
        holder.txtTitulo.text = alerta.titulo
        holder.txtDescripcion.text = alerta.descripcion
        holder.txtArea.text = alerta.area.nombre // <-- Viene del Join

        // Por ahora, mostramos la fecha y hora
        holder.txtTimestamp.text = "${alerta.fecha} - ${alerta.hora.substring(0, 5)}"


        // Lógica para el Tag e Icono
        // La UI de tu screenshot tiene lógica basada en el TÍTULO,
        // no solo en el TIPO (ej: "Éxito" y "Info" son ambos "Informativo" en la DB)

        val tipoAlerta = alerta.tipoAlerta.nombre.lowercase()

        when {
            alerta.titulo.contains("Exitosa") -> {
                holder.txtTag.text = "Éxito"
                holder.imgIcono.setImageResource(R.drawable.ic_check_circle)
                holder.txtTag.background = ContextCompat.getDrawable(context, R.drawable.bg_tag_exito)
            }

            tipoAlerta == "advertencia" -> {
                holder.txtTag.text = "Advertencia"
                holder.imgIcono.setImageResource(R.drawable.ic_warning)
                holder.txtTag.background = ContextCompat.getDrawable(context, R.drawable.bg_tag_advertencia)
            }

            tipoAlerta == "crítico" -> {
                holder.txtTag.text = "Crítica"
                holder.imgIcono.setImageResource(R.drawable.ic_error)
                holder.txtTag.background = ContextCompat.getDrawable(context, R.drawable.bg_tag_critico)
            }

            else -> {
                holder.txtTag.text = "Info"
                holder.imgIcono.setImageResource(R.drawable.ic_info)
                holder.txtTag.background = ContextCompat.getDrawable(context, R.drawable.bg_tag_info)
            }
        }
    }

    // 5. Función para actualizar la lista desde el Fragment/ViewModel
    fun updateData(nuevasAlertas: List<AlertaUI>) {
        this.alertas = nuevasAlertas
        notifyDataSetChanged() // Refresca la lista
    }
}