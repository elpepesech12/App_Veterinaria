package com.example.veterinaria.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object SupabaseClient {

    // !! TUS CLAVES YA ESTÁN AQUÍ !!
    private const val BASE_URL = "https://lgaddicyligidaqshmdp.supabase.co/rest/v1/"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxnYWRkaWN5bGlnaWRhcXNobWRwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjIzNzE4NTUsImV4cCI6MjA3Nzk0Nzg1NX0.YZvtVTv2j9mPBPOPXdaGQjABTPW81x7YTSUTUUsUtDs"

    // Interceptor de Logging (como el del profe)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor paraAñadir las Headers de Supabase (API Key)
    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("apikey", API_KEY)
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()
        chain.proceed(request)
    }

    // Cliente OkHttp (como el del profe, pero con nuestro interceptor)
    private val http = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(headerInterceptor)
        .build()

    // Moshi (como el del profe)
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Servicio Retrofit (como el del profe)
    val service: SupabaseService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(http)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseService::class.java)
    }
}