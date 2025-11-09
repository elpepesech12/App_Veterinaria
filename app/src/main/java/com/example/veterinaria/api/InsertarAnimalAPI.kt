package com.example.veterinaria.api

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

object InsertarAnimalAPI {

    fun insertarAnimal(
        owner: LifecycleOwner,
        context: Context,
        // --- ¡¡TODOS LOS NUEVOS PARÁMETROS!! ---
        nombre: String,
        fechaNac: String,
        idSexo: String,
        idEspecie: Long,
        idHabitat: Long,
        idEstado: Long,
        idArea: Long,
        // -- Callbacks
        onSuccess: ((String) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        // Creamos el objeto request con los datos recibidos
        val request = AnimalInsertRequest(
            nombre = nombre.trim(),
            fecha_nacimiento = fechaNac.trim(),
            id_sexo = idSexo,
            id_especie = idEspecie,
            id_habitat = idHabitat,
            id_estado_salud = idEstado,
            id_area = idArea
        )

        owner.lifecycleScope.launch {
            // Usamos la función del Repositorio (no necesita cambios)
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