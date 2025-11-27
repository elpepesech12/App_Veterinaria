package com.example.veterinaria.api

import com.squareup.moshi.Json

data class AnimalListado(
    @Json(name = "id_animal") val id: Long,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "foto_url") val fotoUrl: String?,
    @Json(name = "edad") val edad: Int?,
    @Json(name = "especie") val especie: String,
    @Json(name = "area") val area: String,
    @Json(name = "estado_salud") val estado: String,

    // --- NUEVO CAMPO ---
    @Json(name = "activo")
    val activo: Boolean = true
)