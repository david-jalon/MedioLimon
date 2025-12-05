package com.mushi.mediolimon.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que gestiona la configuración y creación del cliente de Retrofit.
 * Proporciona una única instancia del servicio de la API para toda la aplicación.
 */
object RetrofitClient {
    // URL base de la API de Spoonacular.
    private const val BASE_URL = "https://api.spoonacular.com/"

    /**
     * Creación de la instancia de Retrofit usando inicialización perezosa (lazy).
     * Esto significa que el objeto Retrofit solo se creará la primera vez que se acceda a él.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Se establece la URL base para todas las peticiones.
            // Se añade un convertidor para que Retrofit pueda transformar el JSON de la API
            // en objetos de datos de Kotlin (usando la librería GSON).
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Proporciona la implementación concreta de la interfaz [SpoonacularApiService].
     * También usa inicialización perezosa para crear el servicio solo cuando se necesita por primera vez.
     */
    val apiService: SpoonacularApiService by lazy {
        retrofit.create(SpoonacularApiService::class.java)
    }
}