package com.example.veterinaria.api

import com.squareup.moshi.Json

// molde para el Animal
data class AnimalSimple(
    @Json(name = "id_animal") val id: Long,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "foto_url") val fotoUrl: String? = null, // Agregado
    @Json(name = "activo") val activo: Boolean = true     // Agregado
)

// molde para el tipo de cita
data class TipoCitaSimple(
    @Json(name = "id_tipo_cita") val id: Long,
    @Json(name = "nombre") val nombre: String
)

// molde para la cita
data class CitaUI(
    @Json(name = "id_cita") val id: Long,
    @Json(name = "fecha") val fecha: String,
    @Json(name = "hora") val hora: String,
    @Json(name = "animal") val animal: AnimalSimple,
    @Json(name = "tipo_cita") val tipoCita: TipoCitaSimple,
    @Json(name = "veterinario") val veterinario: Veterinario
)