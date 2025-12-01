package com.example.veterinaria.api

import android.content.Context
import com.example.veterinaria.api.Veterinario

object SesionManager {

    private const val PREFS_NAME = "VeterinariaPrefs"
    private const val KEY_VET_ID = "vet_id"
    private const val KEY_VET_NOMBRE = "vet_nombre"


    fun saveLogin(context: Context, veterinario: Veterinario) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putLong(KEY_VET_ID, veterinario.id)
            putString(KEY_VET_NOMBRE, "${veterinario.nombre} ${veterinario.apellido}")
            apply()
        }
    }


    fun getVeterinarioId(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_VET_ID, -1L)
    }


    fun clearLogin(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            clear()
            apply()
        }
    }
}