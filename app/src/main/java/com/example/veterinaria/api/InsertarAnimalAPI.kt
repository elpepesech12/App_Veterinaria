package com.example.veterinaria.api

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Helper para insertar (como InsertarAlumnosAPI.kt del profe)
 */
object InsertarAnimalAPI {

    fun insertarAnimal(
        owner: LifecycleOwner,
        context: Context,
        nombre: String,
        fechaNac: String,
        onSuccess: ((String) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        val request = AnimalInsertRequest(
            nombre = nombre.trim(),
            fecha_nacimiento = fechaNac.trim()
        )

        owner.lifecycleScope.launch {
            val res = VeterinariaRepository.insertAnimal(request)
            res.onSuccess { animalesInsertados ->
                val nombreAnimal = animalesInsertados.firstOrNull()?.nombre ?: "Animal"
                val msg = "Se insertó $nombreAnimal correctamente"
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                onSuccess?.invoke(msg)
            }.onFailure { e ->
                Toast.makeText(context, "Error insertando: ${e.message}", Toast.LENGTH_LONG).show()
                onError?.invoke(e)
            }
        }
    }
}