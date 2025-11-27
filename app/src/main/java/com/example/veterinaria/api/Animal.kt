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
    val fotoUrl: String?,
    @Json(name = "id_area")
    val idArea: Long,

    @Json(name = "id_sexo")
    val idSexo: String,
    @Json(name = "id_especie")
    val idEspecie: Long,
    @Json(name = "id_habitat")
    val idHabitat: Long,
    @Json(name = "id_estado_salud")
    val idEstadoSalud: Long,

    // --- NUEVO CAMPO ---
    @Json(name = "activo")
    val activo: Boolean = true // Por defecto true para evitar nulos antiguos
) {
    override fun toString(): String {
        return "$nombre (Nac: $fechaNacimiento)"
    }
}