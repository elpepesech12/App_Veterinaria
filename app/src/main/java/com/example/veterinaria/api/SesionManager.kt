package com.example.veterinaria.api

import android.content.Context
import com.example.veterinaria.api.Veterinario

object SesionManager {

    private const val PREFS_NAME = "VeterinariaPrefs"
    private const val KEY_VET_ID = "vet_id"
    private const val KEY_VET_NOMBRE = "vet_nombre"

    /**
     * guarda los datos del veterinario al iniciar sesión
     */
    fun saveLogin(context: Context, veterinario: Veterinario) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putLong(KEY_VET_ID, veterinario.id)
            putString(KEY_VET_NOMBRE, "${veterinario.nombre} ${veterinario.apellido}")
            apply()
        }
    }

    /**
     * obtiene el ID del veterinario logueado
     */
    fun getVeterinarioId(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Devuelve -1 si no hay nadie logueado
        return prefs.getLong(KEY_VET_ID, -1L)
    }

    /**
     * borra todos los datos de la sesión al cerrar sesión
     */
    fun clearLogin(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            clear() // Borra todas las claves (ID y Nombre)
            apply() // Aplica los cambios
        }
    }
}