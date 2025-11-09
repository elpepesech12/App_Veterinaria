package com.example.veterinaria.funciones

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.veterinaria.bd.AnimalesLocalRepository
import kotlinx.coroutines.launch


object LeerAnimalesLocal {

    fun cargarEnListView(
        owner: LifecycleOwner,
        context: Context,
        listView: ListView
    ) {
        owner.lifecycleScope.launch {
            val res = AnimalesLocalRepository.getAll(context)
            res.onSuccess { lista ->
                listView.adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1,
                    lista
                )
            }.onFailure { e ->
                Toast.makeText(context, "Error leyendo SQLite: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}