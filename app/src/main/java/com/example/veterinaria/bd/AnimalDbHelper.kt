package com.example.veterinaria.bd

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Data class para la BD Local (como AlumnoLocal.kt del profe)
 */
data class AnimalLocal(
    val id: Int,
    val nombre: String,
    val fechaNacimiento: String,
    val fotoUrl: String?
)

/**
 * Helper de SQLite (como AlumnoDbHelper.kt del profe)
 */
class AnimalDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_ANIMALES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_FECHA_NAC TEXT NOT NULL,
                $COL_FOTO_URL TEXT
            );
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMALES")
        onCreate(db)
    }

    fun insert(nombre: String, fechaNac: String, fotoUrl: String?): Long {
        val cv = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_FECHA_NAC, fechaNac)
            put(COL_FOTO_URL, fotoUrl)
        }
        return writableDatabase.insert(TABLE_ANIMALES, null, cv)
    }

    fun getAll(): List<AnimalLocal> {
        val out = mutableListOf<AnimalLocal>()
        val sql = "SELECT $COL_ID, $COL_NOMBRE, $COL_FECHA_NAC, $COL_FOTO_URL FROM $TABLE_ANIMALES ORDER BY $COL_ID DESC"
        val c: Cursor = readableDatabase.rawQuery(sql, null)
        c.use {
            while (it.moveToNext()) {
                out += AnimalLocal(
                    id = it.getInt(0),
                    nombre = it.getString(1),
                    fechaNacimiento = it.getString(2),
                    fotoUrl = it.getString(3)
                )
            }
        }
        return out
    }

    fun clear() {
        writableDatabase.delete(TABLE_ANIMALES, null, null)
    }

    companion object {
        private const val DB_NAME = "veterinaria.db"
        private const val DB_VERSION = 1

        const val TABLE_ANIMALES = "animal_local"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_FECHA_NAC = "fecha_nacimiento"
        const val COL_FOTO_URL = "foto_url"
    }
}