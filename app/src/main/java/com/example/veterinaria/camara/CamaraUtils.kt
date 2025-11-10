package com.example.veterinaria.camara

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

/**
 * objeto Camara Utils
 * convertira la imagen de formato BitMap a Base 64 para enviarla por APi
 */
object CamaraUtils {
    fun convertirDeBitMapABase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)

    }

    fun convertirDeBase64ABitmap(base64String: String?): Bitmap? {
        if (base64String == null || base64String.isEmpty()) {
            return null
        }

        return try {
            val decodedBytes = Base64.decode(base64String, Base64.NO_WRAP)

            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        } catch (e: Exception) {
            Log.e("CamaraUtils", "Error al decodificar Base64", e)
            null
        }
    }
}