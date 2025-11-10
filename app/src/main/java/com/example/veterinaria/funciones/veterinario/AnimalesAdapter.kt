package com.example.veterinaria.funciones.veterinario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load // ¡IMPORTANTE!
import com.example.veterinaria.R
import com.example.veterinaria.api.AnimalListado

class AnimalesAdapter(
    private var animales: List<AnimalListado>
) : RecyclerView.Adapter<AnimalesAdapter.AnimalViewHolder>() {

    // ViewHolder "sostiene" las vistas de un solo item
    inner class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAnimal: ImageView = itemView.findViewById(R.id.img_animal)
        val txtNombre: TextView = itemView.findViewById(R.id.txt_animal_nombre)
        val txtEspecie: TextView = itemView.findViewById(R.id.txt_animal_especie)
        val txtEstado: TextView = itemView.findViewById(R.id.txt_estado_animal)
        val txtEdad: TextView = itemView.findViewById(R.id.txt_animal_edad)
        val txtArea: TextView = itemView.findViewById(R.id.txt_animal_area)
    }

    // se llama cuando se necesita crear un nuevo ViewHolder (fila)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false) // Usa el nuevo item_animal.xml
        return AnimalViewHolder(view)
    }

    // devuelve la cantidad de items
    override fun getItemCount(): Int = animales.size

    // conecta los datos (AnimalListado) con las vistas (ViewHolder)
    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = animales[position]
        val context = holder.itemView.context

        // seteamos textos
        holder.txtNombre.text = animal.nombre
        holder.txtEspecie.text = animal.especie
        holder.txtEdad.text = "${animal.edad ?: '?'} años"
        holder.txtArea.text = animal.area

        // cargar imagen con coil
        // (se usa la url de supabase storage que guardaste en la columna foto_url)
        holder.imgAnimal.load(animal.fotoUrl) {
            placeholder(R.drawable.ic_pulso)
            error(R.drawable.ic_error)
        }

        // lógica para el tag de estado
        holder.txtEstado.text = animal.estado
        when (animal.estado.lowercase()) { // "Saludable", "Crítico", etc.
            "saludable" -> {
                holder.txtEstado.background = ContextCompat.getDrawable(context, R.drawable.bg_tag_saludable)
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            "crítico" -> {
                holder.txtEstado.background = ContextCompat.getDrawable(context, R.drawable.bg_tag_critico)
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            "en observación", "en tratamiento", "en cuarentena" -> {
                holder.txtEstado.background = ContextCompat.getDrawable(context, R.drawable.bg_tag_monitoreo)
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            else -> {
                holder.txtEstado.background = ContextCompat.getDrawable(context, R.drawable.bg_tag_monitoreo)
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    }

    // función para actualizar la lista desde el fragment
    fun updateData(nuevosAnimales: List<AnimalListado>) {
        this.animales = nuevosAnimales
        notifyDataSetChanged() // refresca la lista
    }
}