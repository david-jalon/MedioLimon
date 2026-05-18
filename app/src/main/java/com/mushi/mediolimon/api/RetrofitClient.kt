package com.mushi.mediolimon.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Objeto singleton que gestiona la configuración y creación del cliente de Retrofit.
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.spoonacular.com/"

    /**
     * Configuramos un cliente OkHttp personalizado para aumentar los tiempos de espera.
     * Esto evita los errores de "timeout" cuando la API tarda en procesar peticiones pesadas
     * como el planificador de comidas semanal.
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Aumentamos a 60 por seguridad
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true) // Reintentar si falla la conexión
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Asignamos el cliente con los nuevos timeouts
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: SpoonacularApiService by lazy {
        retrofit.create(SpoonacularApiService::class.java)
    }
}