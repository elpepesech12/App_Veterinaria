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
    val id_area: Long = 1 // <-- ¡¡AÑADIR ESTA LÍNEA!!
)


interface SupabaseService {
    @GET("animal")
    suspend fun getAnimales(
        // ¡Modificar esta línea!
        @Query("select") select: String = "id_animal,nombre,fecha_nacimiento,foto_url,id_area",
        @Header("Range") range: String = "0-99"
    ): List<Animal>

    @POST("animal")
    suspend fun insertAnimal(
        @Body request: AnimalInsertRequest,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<Animal>

    /**
     * Pide el conteo total de animales.
     * No devuelve cuerpo (Response<Unit>), el dato está en el header.
     */
    @GET("animal")
    suspend fun getTotalAnimalesCount(
        @Header("Prefer") prefer: String = "count=exact",
        @Header("Range") range: String = "0-0" // Solo queremos el conteo, no filas
    ): Response<Unit>

    /**
     * Pide el conteo de animales críticos (id_estado_salud = 4)
     */
    @GET("animal?id_estado_salud=eq.4") // Filtro de Supabase
    suspend fun getCriticosAnimalesCount(
        @Header("Prefer") prefer: String = "count=exact",
        @Header("Range") range: String = "0-0"
    ): Response<Unit>

    //PARA HACER EL LOGIN DEL VETERINARIO
    @GET("veterinario")
    suspend fun loginVeterinario(
        @Query("email") email: String,     // ej: "eq.c.gomez@zoovet.cl"
        @Query("password") password: String, // ej: "eq.vet1"

        // Pedimos solo los datos que coinciden con el data class 'Veterinario'
        @Query("select") select: String = "id_veterinario,nombre,apellido_p,email"
    ): List<Veterinario>
    // Devuelve una lista:
    // - Con 1 'Veterinario' si el login es exitoso
    // - Vacía (size 0) si el login es incorrecto

    @GET("sexo") // Nombre de la tabla
    suspend fun getSexos(
        @Query("select") select: String = "id_sexo,descripcion"
    ): List<Sexo>



    @GET("especie") // Nombre de la tabla
    suspend fun getEspecies(
        @Query("select") select: String = "id_especie,nombre_comun"
    ): List<Especie>



    @GET("habitat") // Nombre de la tabla
    suspend fun getHabitats(
        @Query("select") select: String = "id_habitat,nombre"
    ): List<Habitat>



    @GET("estado_salud") // Nombre de la tabla
    suspend fun getEstadosSalud(
        @Query("select") select: String = "id_estado_salud,estado"
    ): List<EstadoSalud>



    @GET("area_animal") // <-- ¡Este es el nombre real de tu tabla!
    suspend fun getAreas(
        @Query("select") select: String = "id_area,nombre"
    ): List<Area>

    //PARA EL LISTADO DE ANIMALES DEL INICIO DEL VET
    @GET("vista_animales_listado")
    suspend fun getAnimalesDashboard(
        @Query("limit") limit: Int = 3
    ): List<AnimalListado>

    // Llama a la MISMA vista, pero sin el límite
    @GET("vista_animales_listado")
    suspend fun getAllAnimalesListado(): List<AnimalListado>


    /**
     * Obtiene TODAS las alertas para la pantalla de Alertas
     * Ordenadas por fecha y hora (más nuevas primero)
     */
    @GET("alerta")
    suspend fun getAlertas(
        @Query("select") select: String = "titulo,descripcion,fecha,hora,tipo_alerta:tipo_alerta(id_tipo_alerta,nombre_tipo),area:area_animal(id_area,nombre)",
        // Ordena por fecha descendente, y luego por hora descendente
        @Query("order") order: String = "fecha.desc,hora.desc"
    ): List<AlertaUI>

    /**
     * Obtiene las citas para el calendario.
     * Pide todas las citas entre una fecha de inicio y fin.
     */
    @GET("cita") // <-- Llama a la tabla 'cita' (minúscula)
    suspend fun getCitasPorRango(
        // Pide las relaciones (joins)
        @Query("select") select: String = "id_cita,fecha,hora,animal:animal(id_animal,nombre),tipo_cita:tipo_cita(id_tipo_cita,nombre),veterinario:veterinario(id_veterinario,nombre,apellido_p,email)",

        // Filtro de fecha "mayor o igual que"
        @Query("fecha") gte: String, // "gte.2025-05-01"

        // Filtro de fecha "menor o igual que"
        @Query("fecha") lte: String, // "lte.2026-05-01"

        // Ordena por fecha y hora
        @Query("order") order: String = "fecha.asc,hora.asc"

    ): List<CitaUI>

    @POST("cita")
    suspend fun insertCita(
        @Body request: InsertarCita,
        // Pedimos que nos devuelva el objeto creado
        @Header("Prefer") prefer: String = "return=representation",

        // Le dice a Supabase que la respuesta SÍ incluya los joins
        @Query("select") select: String = "id_cita,fecha,hora,animal:animal(id_animal,nombre),tipo_cita:tipo_cita(id_tipo_cita,nombre),veterinario:veterinario(id_veterinario,nombre,apellido_p,email)"

    ): List<CitaUI>

    @GET("tipo_cita")
    suspend fun getTiposCita(
        @Query("select") select: String = "id_tipo_cita,nombre"
    ): List<TipoCitaSimple>
}

