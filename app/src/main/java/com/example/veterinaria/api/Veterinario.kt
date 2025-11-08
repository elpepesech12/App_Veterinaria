package com.example.veterinaria.api

import com.squareup.moshi.Json

data class Veterinario(
    @Json(name = "id_veterinario")
    val id: Long,

    @Json(name = "nombre")
    val nombre: String,

    @Json(name = "apellido_p")
    val apellido: String,

    @Json(name = "email")
    val email: String
)
