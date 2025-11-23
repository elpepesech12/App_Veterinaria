package com.example.veterinaria.camara

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object CamaraUtils {

    fun convertirDeBitMapABase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }


    fun base64ToByteArray(base64String: String?): ByteArray? {
        if (base64String.isNullOrEmpty()) return null
        return try {
            val cleanBase64 = base64String.replace("\n", "").replace(" ", "")
            Base64.decode(cleanBase64, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    // Mantenemos esta por si la usas en el DetalleActivity, pero trataremos de no usarla en la lista
    fun convertirDeBase64ABitmap(base64String: String?): Bitmap? {
        val bytes = base64ToByteArray(base64String) ?: return null
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}