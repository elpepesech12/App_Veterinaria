package com.example.veterinaria.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Lo que enviaremos a la API para insertar.
 */
data class AnimalInsertRequest(
    val nombre: String,
    val fecha_nacimiento: String, // formato "YYYY-MM-DD"
    val id_sexo: String = "I",
    val id_especie: Long = 1,
    val id_habitat: Long = 1,
    val id_estado_salud: Long = 1
)

/**
 * Interfaz de Retrofit (como ApiDuocService.kt del profe)
 */
interface SupabaseService {

    @GET("animal") // "animal" es el nombre de tu tabla
    suspend fun getAnimales(
        @Query("select") select: String = "id_animal,nombre,fecha_nacimiento,foto_url",
        @Header("Range") range: String = "0-99"
    ): List<Animal>

    @POST("animal")
    suspend fun insertAnimal(
        @Body request: AnimalInsertRequest,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<Animal>

}