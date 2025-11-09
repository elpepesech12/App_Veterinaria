package com.example.veterinaria.api

import com.squareup.moshi.Json

data class Especie(
    @Json(name = "id_especie")
    val id_especie: Long,
    @Json(name = "nombre_comun")
    val nombre_comun: String
) {
    override fun toString(): String = nombre_comun
}

data class Habitat(
    @Json(name = "id_habitat")
    val id_habitat: Long,
    @Json(name = "nombre")
    val nombre: String
) {
    override fun toString(): String = nombre
}

data class Sexo(
    @Json(name = "id_sexo")
    val id_sexo: String,
    @Json(name = "descripcion")
    val descripcion: String
) {
    override fun toString(): String = descripcion
}

data class EstadoSalud(
    @Json(name = "id_estado_salud")
    val id_estado_salud: Long,
    @Json(name = "estado")
    val estado: String
) {
    override fun toString(): String = estado
}

data class Area(
    @Json(name = "id_area")
    val id_area: Long,
    @Json(name = "nombre")
    val nombre: String
) {
    override fun toString(): String = nombre
}