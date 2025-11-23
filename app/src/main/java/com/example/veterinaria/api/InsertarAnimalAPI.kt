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
        nombre: String,
        fechaNac: String,
        idSexo: String,
        idEspecie: Long,
        idHabitat: Long,
        idEstado: Long,
        idArea: Long,
        fotoBase64: String?,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        owner.lifecycleScope.launch {
            val nuevoAnimal = AnimalInsertRequest(
                nombre = nombre,
                fecha_nacimiento = fechaNac,
                id_sexo = idSexo,
                id_especie = idEspecie,
                id_habitat = idHabitat,
                id_estado_salud = idEstado,
                id_area = idArea,
                foto_url = fotoBase64
            )

            val resultado = VeterinariaRepository.insertAnimal(nuevoAnimal)

            resultado.onSuccess {
                Toast.makeText(context, "¡Animal creado con foto!", Toast.LENGTH_SHORT).show()
                onSuccess()
            }.onFailure { e ->
                Toast.makeText(context, "Error API: ${e.message}", Toast.LENGTH_LONG).show()
                onError()
            }
        }
    }
}