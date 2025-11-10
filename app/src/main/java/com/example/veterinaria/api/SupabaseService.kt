package com.example.veterinaria.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


data class AnimalInsertRequest(
    val nombre: String,
    val fecha_nacimiento: String, // formato "YYYY-MM-DD"
    val id_sexo: String = "M",
    val id_especie: Long = 1,
    val id_habitat: Long = 1,
    val id_estado_salud: Long = 1,
    val id_area: Long = 1,
    val foto_url: String? = null
)


interface SupabaseService {
    @GET("animal")
    suspend fun getAnimales(
        @Query("select") select: String = "id_animal,nombre,fecha_nacimiento,foto_url,id_area,id_sexo,id_especie,id_habitat,id_estado_salud",
        @Header("Range") range: String = "0-99"
    ): List<Animal>

    @POST("animal")
    suspend fun insertAnimal(
        @Body request: AnimalInsertRequest,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<Animal>


    @GET("animal")
    suspend fun getTotalAnimalesCount(
        @Header("Prefer") prefer: String = "count=exact",
        @Header("Range") range: String = "0-0" // Solo queremos el conteo, no filas
    ): Response<Unit>

    /**
     * pide el conteo de animales críticos (id_estado_salud = 4)
     */
    @GET("animal?id_estado_salud=eq.4") // filtro de Supabase
    suspend fun getCriticosAnimalesCount(
        @Header("Prefer") prefer: String = "count=exact",
        @Header("Range") range: String = "0-0"
    ): Response<Unit>

    //PARA HACER EL LOGIN DEL VETERINARIO
    @GET("veterinario")
    suspend fun loginVeterinario(
        @Query("email") email: String,     // "c.gomez@zoovet.cl"
        @Query("password") password: String, // "vet1"

        // pedimos solo los datos que coinciden con el data class 'veterinario'
        @Query("select") select: String = "id_veterinario,nombre,apellido_p,email"
    ): List<Veterinario>
    // devuelve una lista
    // 1 si el login es exitoso
    // vacia (0) si el login es incorrecto

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

    //PARA EL LISTADO DE ANIMALES DEL INICIO DEL VET
    @GET("vista_animales_listado")
    suspend fun getAnimalesDashboard(
        @Query("limit") limit: Int = 3
    ): List<AnimalListado>

    @GET("vista_animales_listado")
    suspend fun getAllAnimalesListado(): List<AnimalListado>


    /**
     * bbtiene TODAS las alertas para la pantalla de alertas
     * ordenadas por fecha y hora (más nuevas primero)
     */
    @GET("alerta")
    suspend fun getAlertas(
        @Query("select") select: String = "titulo,descripcion,fecha,hora,tipo_alerta:tipo_alerta(id_tipo_alerta,nombre_tipo),area:area_animal(id_area,nombre)",
        // ordena por fecha descendente, y luego por hora descendente
        @Query("order") order: String = "fecha.desc,hora.desc"
    ): List<AlertaUI>

    /**
     * obtiene las citas para el calendario
     * pide todas las citas entre una fecha de inicio y fin
     */
    @GET("cita")
    suspend fun getCitasPorRango(
        @Query("select") select: String = "id_cita,fecha,hora,animal:animal(id_animal,nombre),tipo_cita:tipo_cita(id_tipo_cita,nombre),veterinario:veterinario(id_veterinario,nombre,apellido_p,email)",

        // filtro de fecha "mayor o igual que"
        @Query("fecha") gte: String, // "gte.2025-05-01"

        // filtro de fecha "menor o igual que"
        @Query("fecha") lte: String, // "lte.2026-05-01"

        // prdena por fecha y hora
        @Query("order") order: String = "fecha.asc,hora.asc"

    ): List<CitaUI>

    @POST("cita")
    suspend fun insertCita(
        @Body request: InsertarCita,
        // pedimos que nos devuelva el objeto creado
        @Header("Prefer") prefer: String = "return=representation",

        // le dice a supabase que la respuesta si incluya los joins
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
}


