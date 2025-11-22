package com.example.veterinaria.api

import com.example.veterinaria.funciones.veterinario.DashboardStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response


object VeterinariaRepository {

    suspend fun fetchAnimales(): Result<List<Animal>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.getAnimales())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertAnimal(request: AnimalInsertRequest): Result<List<Animal>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.insertAnimal(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //DATOS PARA EL INICIO DEL VET
    suspend fun getDashboardStats(): Result<DashboardStats> = withContext(Dispatchers.IO) {
        try {
            // 1. Llama a las dos nuevas funciones del servicio
            val totalResponse = SupabaseClient.service.getTotalAnimalesCount()
            val criticosResponse = SupabaseClient.service.getCriticosAnimalesCount()

            // 2. Extrae el conteo de los headers
            val total = parseCountFromHeader(totalResponse)
            val criticos = parseCountFromHeader(criticosResponse)

            // 3. Devuelve los datos en nuestro "molde"
            Result.success(DashboardStats(totalAnimales = total, totalCriticos = criticos))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FUNCIÓN "HELPER" ---

    /**
     * función privada que lee el header 'content-range' (ej: "0-0/247")
     * y extrae el número final (ej: 247).
     */
    private fun parseCountFromHeader(response: Response<Unit>): Long {
        // si da 404, lanza un error
        if (!response.isSuccessful) {
            throw Exception("Error en la respuesta: ${response.code()}")
        }

        // obtiene el header
        val rangeHeader = response.headers().get("Content-Range") // "0-0/247"

        // lo divide por "/" y toma la última parte
        val totalString = rangeHeader?.split("/")?.lastOrNull()

        // lo convierte a número
        return totalString?.toLongOrNull() ?: 0L
    }


    //FUNCION PARA EL LOGIN DEL VET
    suspend fun login(email: String, pass: String): Result<Veterinario> = withContext(Dispatchers.IO) {
        try {
            val emailQuery = "eq.$email"
            val passQuery = "eq.$pass"

            // llamamos a la nueva función del servicio
            val vetList = SupabaseClient.service.loginVeterinario(emailQuery, passQuery)

            // verificamos la respuesta
            vetList.firstOrNull()?.let { veterinarioEncontrado ->
                // se devuelve
                Result.success(veterinarioEncontrado)
            } ?: run {
                // la lista vino vacia. credenciales incorrectas.
                Result.failure(Exception("Email o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //para el listado de animales para el inicio del vet
    suspend fun fetchAnimalesDashboard(): Result<List<AnimalListado>> = withContext(Dispatchers.IO) {
        try {
            // Llama a la función del dashboard (con límite 3)
            Result.success(SupabaseClient.service.getAnimalesDashboard(3))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAllAnimalesListado(): Result<List<AnimalListado>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.getAllAnimalesListado())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchSexos(): Result<List<Sexo>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.getSexos())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchEspecies(): Result<List<Especie>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.getEspecies())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchHabitats(): Result<List<Habitat>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.getHabitats())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchEstadosSalud(): Result<List<EstadoSalud>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.getEstadosSalud())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAreas(): Result<List<Area>> = withContext(Dispatchers.IO) {
        try {
            Result.success(SupabaseClient.service.getAreas())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAlertas(): Result<List<AlertaUI>> = withContext(Dispatchers.IO) {
        try {
            // llama a la nueva función del servicio
            Result.success(SupabaseClient.service.getAlertas())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun fetchCitasPorRango(
        fechaInicio: String, // "2025-05-01"
        fechaFin: String     // "2026-05-01"
    ): Result<List<CitaUI>> = withContext(Dispatchers.IO) {
        try {
            // gte. (mayor o igual) y lte. (menor o igual)
            val queryGte = "gte.$fechaInicio"
            val queryLte = "lte.$fechaFin"

            val citas = SupabaseClient.service.getCitasPorRango(
                select = "id_cita,fecha,hora,animal:animal(id_animal,nombre),tipo_cita:tipo_cita(id_tipo_cita,nombre),veterinario:veterinario(id_veterinario,nombre,apellido_p,email)",
                gte = queryGte,
                lte = queryLte
            )
            Result.success(citas)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAnimalesParaSpinner(): Result<List<AnimalSimple>> = withContext(Dispatchers.IO) {
        try {

            val animalesCompletos = SupabaseClient.service.getAnimales()
            val animalesSimples = animalesCompletos.map {
                AnimalSimple(id = it.id, nombre = it.nombre)
            }
            Result.success(animalesSimples)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTiposCita(): Result<List<TipoCitaSimple>> = withContext(Dispatchers.IO) {
        try {

            Result.success(SupabaseClient.service.getTiposCita())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertCita(request: InsertarCita): Result<CitaUI> = withContext(Dispatchers.IO) {
        try {
            val responseList = SupabaseClient.service.insertCita(request)
            responseList.firstOrNull()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("La API no devolvió la cita creada"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchEspecieById(id: Long): Result<Especie> = withContext(Dispatchers.IO) {
        try {
            val lista = SupabaseClient.service.getEspecieById("eq.$id")
            lista.firstOrNull()?.let { Result.success(it) }
                ?: Result.failure(Exception("No se encontró Especie con ID $id"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun fetchHabitatById(id: Long): Result<Habitat> = withContext(Dispatchers.IO) {
        try {
            val lista = SupabaseClient.service.getHabitatById("eq.$id")
            lista.firstOrNull()?.let { Result.success(it) }
                ?: Result.failure(Exception("No se encontró Hábitat con ID $id"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun fetchEstadoById(id: Long): Result<EstadoSalud> = withContext(Dispatchers.IO) {
        try {
            val lista = SupabaseClient.service.getEstadoById("eq.$id")
            lista.firstOrNull()?.let { Result.success(it) }
                ?: Result.failure(Exception("No se encontró Estado con ID $id"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun fetchAreaById(id: Long): Result<Area> = withContext(Dispatchers.IO) {
        try {
            val lista = SupabaseClient.service.getAreaById("eq.$id")
            lista.firstOrNull()?.let { Result.success(it) }
                ?: Result.failure(Exception("No se encontró Área con ID $id"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteAnimal(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val query = "eq.$id"
            val response = SupabaseClient.service.deleteAnimal(query)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // [NUEVO] Lógica para editar
    suspend fun updateAnimal(id: Long, datos: AnimalInsertRequest): Result<Animal> = withContext(Dispatchers.IO) {
        try {
            val query = "eq.$id"
            val lista = SupabaseClient.service.updateAnimal(query, datos)
            if (lista.isNotEmpty()) {
                Result.success(lista[0])
            } else {
                Result.failure(Exception("No se pudo actualizar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
