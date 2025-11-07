package com.example.veterinaria.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
}