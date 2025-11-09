package com.example.veterinaria.api

import com.squareup.moshi.Json

data class AnimalListado(
    @Json(name = "id_animal") val id: Long,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "foto_url") val fotoUrl: String?,
    @Json(name = "edad") val edad: Int?, // Ej: 15
    @Json(name = "especie") val especie: String, // Ej: Elefante Africano
    @Json(name = "area") val area: String, // Ej: Área de Sabana
    @Json(name = "estado_salud") val estado: String // Ej: Saludable
)