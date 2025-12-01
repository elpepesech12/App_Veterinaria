package com.example.veterinaria.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

// 1. AGREGAMOS EL CAMPO 'ACTIVO' AL INSERT/UPDATE
data class AnimalInsertRequest(
    val nombre: String,
    val fecha_nacimiento: String, // formato "YYYY-MM-DD"
    val id_sexo: String = "M",
    val id_especie: Long = 1,
    val id_habitat: Long = 1,
    val id_estado_salud: Long = 1,
    val id_area: Long = 1,
    val foto_url: String? = null,
    val activo: Boolean = true // <--- NUEVO CAMPO (Por defecto true)
)

// Data class auxiliar pequeña para el borrado lógico
data class EstadoRequest(val activo: Boolean)

data class FichaMedicaInsert(
    val fecha_realizada: String,    // "YYYY-MM-DD"
    val diagnostico_general: String,
    val id_animal: Long,
    val id_veterinario: Long
)

data class FichaMedicaLectura(
    val id_ficha_m: Long,
    val fecha_realizada: String,
    val diagnostico_general: String,
    val animal: AnimalSimple?,       // Relación
    val veterinario: VeterinarioSimple? // Relación
)

data class VeterinarioSimple(val nombre: String, val apellido_p: String)

interface SupabaseService {

    @GET("animal")
    suspend fun getAnimales(
        @Query("select") select: String = "id_animal,nombre,fecha_nacimiento,foto_url,id_area,id_sexo,id_especie,id_habitat,id_estado_salud,activo",
        @Header("Range") range: String = "0-99"
    ): List<Animal>

    @POST("animal")
    suspend fun insertAnimal(
        @Body request: AnimalInsertRequest,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<Animal>

    // --- CORRECCIÓN EN CONTADORES (Filtrar solo activos) ---

    @GET("animal?activo=eq.true")
    suspend fun getTotalAnimalesCount(
        @Header("Prefer") prefer: String = "count=exact",
        @Header("Range") range: String = "0-0"
    ): Response<Unit>

    /**
     * pide el conteo de animales críticos (id_estado_salud = 4) Y activos
     */
    @GET("animal?id_estado_salud=eq.4&activo=eq.true")
    suspend fun getCriticosAnimalesCount(
        @Header("Prefer") prefer: String = "count=exact",
        @Header("Range") range: String = "0-0"
    ): Response<Unit>

    // ------------------------------------------------------

    // PARA HACER EL LOGIN DEL VETERINARIO
    @GET("veterinario")
    suspend fun loginVeterinario(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("select") select: String = "id_veterinario,nombre,apellido_p,email"
    ): List<Veterinario>

    @GET("sexo")
    suspend fun getSexos(
        @Query("select") select: String = "id_sexo,descripcion"
    ): List<Sexo>

    @GET("especie")
    suspend fun getEspecies(
        @Query("select") select: String = "id_especie,nombre_comun"
    ): List<Especie>

    @GET("habitat")
    suspend fun getHabitats(
        @Query("select") select: String = "id_habitat,nombre"
    ): List<Habitat>

    @GET("estado_salud")
    suspend fun getEstadosSalud(
        @Query("select") select: String = "id_estado_salud,estado"
    ): List<EstadoSalud>

    @GET("area_animal")
    suspend fun getAreas(
        @Query("select") select: String = "id_area,nombre"
    ): List<Area>

    // PARA EL LISTADO DE ANIMALES DEL INICIO DEL VET
    @GET("vista_animales_listado")
    suspend fun getAnimalesDashboard(
        @Query("limit") limit: Int = 3
    ): List<AnimalListado>

    @GET("vista_animales_listado")
    suspend fun getAllAnimalesListado(): List<AnimalListado>


    @GET("alerta")
    suspend fun getAlertas(
        @Query("select") select: String = "titulo,descripcion,fecha,hora,tipo_alerta:tipo_alerta(id_tipo_alerta,nombre_tipo),area:area_animal(id_area,nombre)",
        @Query("order") order: String = "fecha.desc,hora.desc"
    ): List<AlertaUI>


    @GET("cita")
    suspend fun getCitasPorRango(
        @Query("select") select: String = "id_cita,fecha,hora,animal:animal(id_animal,nombre,activo,foto_url),tipo_cita:tipo_cita(id_tipo_cita,nombre),veterinario:veterinario(id_veterinario,nombre,apellido_p,email)",

        @Query("fecha") gte: String,
        @Query("fecha") lte: String,
        @Query("order") order: String = "fecha.asc,hora.asc"
    ): List<CitaUI>

    @POST("cita")
    suspend fun insertCita(
        @Body request: InsertarCita,
        @Header("Prefer") prefer: String = "return=representation",
        @Query("select") select: String = "id_cita,fecha,hora,animal:animal(id_animal,nombre),tipo_cita:tipo_cita(id_tipo_cita,nombre),veterinario:veterinario(id_veterinario,nombre,apellido_p,email)"
    ): List<CitaUI>

    @GET("tipo_cita")
    suspend fun getTiposCita(
        @Query("select") select: String = "id_tipo_cita,nombre"
    ): List<TipoCitaSimple>

    @GET("especie")
    suspend fun getEspecieById(
        @Query("id_especie") idQuery: String,
        @Query("select") select: String = "id_especie,nombre_comun"
    ): List<Especie>

    @GET("habitat")
    suspend fun getHabitatById(
        @Query("id_habitat") idQuery: String,
        @Query("select") select: String = "id_habitat,nombre"
    ): List<Habitat>

    @GET("estado_salud")
    suspend fun getEstadoById(
        @Query("id_estado_salud") idQuery: String,
        @Query("select") select: String = "id_estado_salud,estado"
    ): List<EstadoSalud>

    @GET("area_animal")
    suspend fun getAreaById(
        @Query("id_area") idQuery: String,
        @Query("select") select: String = "id_area,nombre"
    ): List<Area>


    @PATCH("animal")
    suspend fun cambiarEstadoAnimal(
        @Query("id_animal") idQuery: String,
        @Body body: EstadoRequest
    ): Response<Unit>

    // EDITAR
    @PATCH("animal")
    suspend fun updateAnimal(
        @Query("id_animal") idQuery: String,
        @Body request: AnimalInsertRequest,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<Animal>

    @POST("ficha_medica")
    suspend fun crearFichaMedica(
        @Body ficha: FichaMedicaInsert,
        @Header("Prefer") prefer: String = "return=minimal"
    ): Response<Unit>

    @GET("ficha_medica")
    suspend fun getHistorialMedico(
        @Query("select") select: String = "id_ficha_m,fecha_realizada,diagnostico_general,animal:animal(id_animal,nombre),veterinario:veterinario(nombre,apellido_p)",
        @Query("order") order: String = "fecha_realizada.desc"
    ): List<FichaMedicaLectura>
}