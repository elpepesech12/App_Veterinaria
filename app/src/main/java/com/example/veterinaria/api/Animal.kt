package com.example.veterinaria.api

import com.squareup.moshi.Json


data class Animal(
    @Json(name = "id_animal")
    val id: Long,

    @Json(name = "nombre")
    val nombre: String,

    @Json(name = "fecha_nacimiento")
    val fechaNacimiento: String,

    @Json(name = "foto_url")
    val fotoUrl: String?
)