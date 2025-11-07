package com.example.veterinaria.funciones

import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.api.VeterinariaRepository
import com.example.veterinaria.bd.AnimalesLocalRepository
import kotlinx.coroutines.launch

object CargarAnimalesApi {

    fun cargarAnimales(owner: LifecycleOwner, listView: ListView) {
        owner.lifecycleScope.launch {
            val res = VeterinariaRepository.fetchAnimales()

            res.onSuccess { animales ->
                if (animales.isEmpty()) {
                    Toast.makeText(listView.context, "Sin datos desde Supabase", Toast.LENGTH_SHORT).show()
                }

                // 1. Mapeamos los datos para el ListView (como el profe)
                val datos = animales.map { a ->
                    "NOMBRE: ${a.nombre}\n" +
                            "NAC: ${a.fechaNacimiento}"
                }

                // 2. Pintamos en el ListView
                listView.adapter = ArrayAdapter(
                    listView.context,
                    android.R.layout.simple_list_item_1, // Layout simple
                    datos
                )

                // 3. Sincronizamos con la BD Local
                AnimalesLocalRepository.clear(listView.context).onSuccess {
                    AnimalesLocalRepository.insertFromApiList(listView.context, animales)
                }

                android.util.Log.d("VETERINARIA_API", "items=${animales.size}")
            }.onFailure { e ->
                android.util.Log.e("VETERINARIA_API_ERR", "falló", e)
                Toast.makeText(listView.context, "Error al cargar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}