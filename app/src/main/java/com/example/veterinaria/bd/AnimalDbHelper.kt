package com.example.veterinaria.bd

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


data class AnimalLocal(
    val id: Int,
    val nombre: String,
    val fechaNacimiento: String,
    val fotoUrl: String?,
    val idArea: Int // <-- ¡¡AÑADIR ESTA LÍNEA!!
) {
    override fun toString(): String {
        return "$nombre (Nac: $fechaNacimiento) [Local]"
    }
}


class AnimalDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_ANIMALES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_FECHA_NAC TEXT NOT NULL,
                $COL_FOTO_URL TEXT,
                $COL_ID_AREA INTEGER NOT NULL DEFAULT 1 -- ¡¡AÑADIR ESTA LÍNEA!!
            );
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMALES")
        onCreate(db)
    }

    fun insert(nombre: String, fechaNac: String, fotoUrl: String?, idArea: Long): Long {
        val cv = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_FECHA_NAC, fechaNac)
            put(COL_FOTO_URL, fotoUrl)
            put(COL_ID_AREA, idArea) // <-- ¡¡AÑADIR ESTA LÍNEA!!
        }
        return writableDatabase.insert(TABLE_ANIMALES, null, cv)
    }

    fun getAll(): List<AnimalLocal> {
        val out = mutableListOf<AnimalLocal>()
        // ¡Modificar el SQL!
        val sql = "SELECT $COL_ID, $COL_NOMBRE, $COL_FECHA_NAC, $COL_FOTO_URL, $COL_ID_AREA FROM $TABLE_ANIMALES ORDER BY $COL_ID DESC"
        val c: Cursor = readableDatabase.rawQuery(sql, null)
        c.use {
            while (it.moveToNext()) {
                out += AnimalLocal(
                    id = it.getInt(0),
                    nombre = it.getString(1),
                    fechaNacimiento = it.getString(2),
                    fotoUrl = it.getString(3),
                    idArea = it.getInt(4)
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
        private const val DB_VERSION = 2

        const val TABLE_ANIMALES = "animal_local"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_FECHA_NAC = "fecha_nacimiento"
        const val COL_FOTO_URL = "foto_url"
        const val COL_ID_AREA = "id_area"
    }
}