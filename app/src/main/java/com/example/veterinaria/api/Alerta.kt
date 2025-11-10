package com.example.veterinaria.api

import com.squareup.moshi.Json

data class TipoAlerta(
    @Json(name = "id_tipo_alerta") val id: Long,
    @Json(name = "nombre_tipo") val nombre: String
)


data class AlertaUI(
    @Json(name = "titulo") val titulo: String,
    @Json(name = "descripcion") val descripcion: String,
    @Json(name = "fecha") val fecha: String,
    @Json(name = "hora") val hora: String,
    @Json(name = "tipo_alerta") val tipoAlerta: TipoAlerta,
    @Json(name = "area") val area: Area
)