package com.example.veterinaria.funciones

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.bd.AnimalesLocalRepository
import kotlinx.coroutines.launch

/**
 * Carga datos de BD Local y los pone en un ListView (como LeerAlumnosLocalSQL.kt del profe)
 */
object LeerAnimalesLocal {

    fun cargarEnListView(
        owner: LifecycleOwner,
        context: Context,
        listView: ListView
    ) {
        owner.lifecycleScope.launch {
            val res = AnimalesLocalRepository.getAll(context)
            res.onSuccess { lista ->
                val datos = lista.map { a ->
                    "(LOCAL) ${a.nombre}\nNac: ${a.fechaNacimiento}"
                }
                listView.adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1,
                    datos
                )
            }.onFailure { e ->
                Toast.makeText(context, "Error leyendo SQLite: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}