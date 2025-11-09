package com.example.veterinaria.api

import com.squareup.moshi.Json

data class InsertarCita(
    @Json(name = "id_animal") val id_animal: Long,
    @Json(name = "id_veterinario") val id_veterinario: Long,
    @Json(name = "id_tipo_cita") val id_tipo_cita: Long,
    @Json(name = "fecha") val fecha: String, // "YYYY-MM-DD"
    @Json(name = "hora") val hora: String // "HH:MM:SS"
)