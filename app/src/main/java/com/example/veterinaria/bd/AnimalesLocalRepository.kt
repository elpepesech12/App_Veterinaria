package com.example.veterinaria.bd

import android.content.Context
import com.example.veterinaria.api.Animal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio de BD Local (como AlumnosLocalRepository.kt del profe)
 */
object AnimalesLocalRepository {

    suspend fun insert(context: Context, nombre: String, fechaNac: String, fotoUrl: String?): Result<Long> =
        withContext(Dispatchers.IO) {
            runCatching {
                AnimalDbHelper(context).use { it.insert(nombre, fechaNac, fotoUrl) }
            }
        }

    /** Helper para insertar desde una lista de la API */
    suspend fun insertFromApiList(context: Context, animales: List<Animal>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                AnimalDbHelper(context).use { db ->
                    animales.forEach { animal ->
                        db.insert(animal.nombre, animal.fechaNacimiento, animal.fotoUrl)
                    }
                }
            }
        }


    suspend fun getAll(context: Context): Result<List<AnimalLocal>> =
        withContext(Dispatchers.IO) {
            runCatching {
                AnimalDbHelper(context).use { it.getAll() }
            }
        }

    suspend fun clear(context: Context): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                AnimalDbHelper(context).use { it.clear() }
            }
        }
}