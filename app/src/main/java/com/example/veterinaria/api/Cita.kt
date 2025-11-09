package com.example.veterinaria.api

import com.squareup.moshi.Json

// Molde para el Animal
data class AnimalSimple(
    @Json(name = "id_animal") val id: Long,
    @Json(name = "nombre") val nombre: String
)

// Molde para el Tipo de Cita
data class TipoCitaSimple(
    @Json(name = "id_tipo_cita") val id: Long,
    @Json(name = "nombre") val nombre: String // Ej: "Control Anual"
)

// Molde para la Cita
data class CitaUI(
    @Json(name = "id_cita") val id: Long,
    @Json(name = "fecha") val fecha: String, // "2025-11-09"
    @Json(name = "hora") val hora: String, // "10:00:00"

    // Objeto anidado para el Animal
    @Json(name = "animal") val animal: AnimalSimple,

    // Objeto anidado para el Tipo de Cita
    @Json(name = "tipo_cita") val tipoCita: TipoCitaSimple,

    // Objeto anidado para el Veterinario (¡Importante!)
    @Json(name = "veterinario") val veterinario: Veterinario
)