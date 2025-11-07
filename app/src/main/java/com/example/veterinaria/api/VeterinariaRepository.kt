package com.example.veterinaria.api

import com.example.veterinaria.funciones.veterinario.DashboardStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response


object VeterinariaRepository {

    suspend fun fetchAnimales(): Result<List<Animal>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.getAnimales())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertAnimal(request: AnimalInsertRequest): Result<List<Animal>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.insertAnimal(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDashboardStats(): Result<DashboardStats> = withContext(Dispatchers.IO) {
        try {
            // 1. Llama a las dos nuevas funciones del servicio
            val totalResponse = SupabaseClient.service.getTotalAnimalesCount()
            val criticosResponse = SupabaseClient.service.getCriticosAnimalesCount()

            // 2. Extrae el conteo de los headers
            val total = parseCountFromHeader(totalResponse)
            val criticos = parseCountFromHeader(criticosResponse)

            // 3. Devuelve los datos en nuestro "molde"
            Result.success(DashboardStats(totalAnimales = total, totalCriticos = criticos))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ¡AÑADE ESTA FUNCIÓN "HELPER"! ---

    /**
     * Función privada que lee el header 'Content-Range' (ej: "0-0/247")
     * y extrae el número final (ej: 247).
     */
    private fun parseCountFromHeader(response: Response<Unit>): Long {
        // Si la llamada falló (ej. 404), lanza un error
        if (!response.isSuccessful) {
            throw Exception("Error en la respuesta: ${response.code()}")
        }

        // 1. Obtiene el header
        val rangeHeader = response.headers().get("Content-Range") // "0-0/247"

        // 2. Lo divide por "/" y toma la última parte
        val totalString = rangeHeader?.split("/")?.lastOrNull()

        // 3. Lo convierte a número
        return totalString?.toLongOrNull() ?: 0L
    }
}