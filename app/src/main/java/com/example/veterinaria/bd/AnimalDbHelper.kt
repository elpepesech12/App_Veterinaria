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
    val idArea: Int,

    val idSexo: String,
    val idEspecie: Int,
    val idHabitat: Int,
    val idEstadoSalud: Int
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
                $COL_ID_AREA INTEGER NOT NULL DEFAULT 1,
                
                -- ¡¡AÑADIR ESTAS 4 LÍNEAS!! --
                $COL_SEXO CHAR(1) NOT NULL DEFAULT 'M',
                $COL_ESPECIE_ID INTEGER NOT NULL DEFAULT 1,
                $COL_HABITAT_ID INTEGER NOT NULL DEFAULT 1,
                $COL_ESTADO_ID INTEGER NOT NULL DEFAULT 1
            );
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMALES")
        onCreate(db)
    }

    fun insert(nombre: String, fechaNac: String, fotoUrl: String?,
               idArea: Long, idSexo: String, idEspecie: Long,
               idHabitat: Long, idEstado: Long): Long {

        val cv = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_FECHA_NAC, fechaNac)
            put(COL_FOTO_URL, fotoUrl)
            put(COL_ID_AREA, idArea)

            put(COL_SEXO, idSexo)
            put(COL_ESPECIE_ID, idEspecie)
            put(COL_HABITAT_ID, idHabitat)
            put(COL_ESTADO_ID, idEstado)
        }
        return writableDatabase.insert(TABLE_ANIMALES, null, cv)
    }

    fun getAll(): List<AnimalLocal> {
        val out = mutableListOf<AnimalLocal>()
        val sql = "SELECT * FROM $TABLE_ANIMALES ORDER BY $COL_ID DESC" // Simplificado
        val c: Cursor = readableDatabase.rawQuery(sql, null)
        c.use {
            while (it.moveToNext()) {
                out += AnimalLocal(
                    // Los números de columna cambian
                    id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                    nombre = it.getString(it.getColumnIndexOrThrow(COL_NOMBRE)),
                    fechaNacimiento = it.getString(it.getColumnIndexOrThrow(COL_FECHA_NAC)),
                    fotoUrl = it.getString(it.getColumnIndexOrThrow(COL_FOTO_URL)),
                    idArea = it.getInt(it.getColumnIndexOrThrow(COL_ID_AREA)),

                    // -- ¡¡AÑADIR ESTAS 4 LÍNEAS!! --
                    idSexo = it.getString(it.getColumnIndexOrThrow(COL_SEXO)),
                    idEspecie = it.getInt(it.getColumnIndexOrThrow(COL_ESPECIE_ID)),
                    idHabitat = it.getInt(it.getColumnIndexOrThrow(COL_HABITAT_ID)),
                    idEstadoSalud = it.getInt(it.getColumnIndexOrThrow(COL_ESTADO_ID))
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
        private const val DB_VERSION = 3

        const val TABLE_ANIMALES = "animal_local"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_FECHA_NAC = "fecha_nacimiento"
        const val COL_FOTO_URL = "foto_url"
        const val COL_ID_AREA = "id_area"
        const val COL_SEXO = "id_sexo"
        const val COL_ESPECIE_ID = "id_especie"
        const val COL_HABITAT_ID = "id_habitat"
        const val COL_ESTADO_ID = "id_estado_salud"
    }
}