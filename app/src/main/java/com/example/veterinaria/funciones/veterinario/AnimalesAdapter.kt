package com.example.veterinaria.funciones.veterinario

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.veterinaria.DetalleAnimalActivity
import com.example.veterinaria.R
import com.example.veterinaria.api.AnimalListado
import com.example.veterinaria.camara.CamaraUtils

class AnimalesAdapter(
    private var animales: List<AnimalListado>
) : RecyclerView.Adapter<AnimalesAdapter.AnimalViewHolder>() {

    inner class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAnimal: ImageView = itemView.findViewById(R.id.img_animal)
        val txtNombre: TextView = itemView.findViewById(R.id.txt_animal_nombre)
        val txtEspecie: TextView = itemView.findViewById(R.id.txt_animal_especie)
        val txtEstado: TextView = itemView.findViewById(R.id.txt_estado_animal)
        val txtEdad: TextView = itemView.findViewById(R.id.txt_animal_edad)
        val txtArea: TextView = itemView.findViewById(R.id.txt_animal_area)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun getItemCount(): Int = animales.size

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = animales[position]
        val context = holder.itemView.context

        holder.txtNombre.text = animal.nombre
        holder.txtEspecie.text = animal.especie
        holder.txtEdad.text = "${animal.edad ?: '?'} años"
        holder.txtArea.text = animal.area

        holder.imgAnimal.load(null)

        val fotoUrl = animal.fotoUrl

        if (!fotoUrl.isNullOrEmpty()) {
            val fotoLimpia = fotoUrl.trim()

            if (fotoLimpia.startsWith("http")) {
                holder.imgAnimal.load(fotoLimpia) {
                    crossfade(true)
                    placeholder(R.drawable.ic_pulso)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                val imageBytes = CamaraUtils.base64ToByteArray(fotoLimpia)

                if (imageBytes != null) {
                    holder.imgAnimal.load(imageBytes) {
                        crossfade(true)
                        placeholder(R.drawable.ic_pulso)
                        error(android.R.drawable.ic_menu_report_image)
                    }
                } else {
                    holder.imgAnimal.setImageResource(android.R.drawable.ic_menu_camera)
                }
            }
        } else {

            holder.imgAnimal.setImageResource(android.R.drawable.ic_menu_camera)
        }

        holder.txtEstado.text = animal.estado
        val colorBackground = when (animal.estado.lowercase()) {
            "saludable" -> R.drawable.bg_tag_saludable
            "crítico" -> R.drawable.bg_tag_critico
            "en observación", "en tratamiento", "en cuarentena" -> R.drawable.bg_tag_monitoreo
            else -> R.drawable.bg_tag_monitoreo
        }
        holder.txtEstado.background = ContextCompat.getDrawable(context, colorBackground)
        holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.black))

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleAnimalActivity::class.java).apply {
                putExtra("ANIMAL_ID", animal.id)
                putExtra("ANIMAL_NOMBRE", animal.nombre)
                putExtra("ANIMAL_FOTO", animal.fotoUrl)
                putExtra("ANIMAL_FECHA", "${animal.edad ?: 0} años")
                putExtra("ANIMAL_SEXO", "?")
                putExtra("ES_LOCAL", false)
            }
            context.startActivity(intent)
        }
    }

    fun updateData(nuevosAnimales: List<AnimalListado>) {
        this.animales = nuevosAnimales
        notifyDataSetChanged()
    }
}