package com.mushi.mediolimon.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que gestiona la configuración y creación del cliente de Retrofit.
 * Al ser un `object`, Kotlin asegura que solo existirá una única instancia de RetrofitClient en toda la aplicación (patrón Singleton).
 * Esto es ideal para centralizar la configuración de red y reutilizar la misma instancia de Retrofit, 
 * lo cual es muy eficiente en términos de rendimiento y memoria.
 */
object RetrofitClient {
    // La URL base de la API. Todas las peticiones definidas en SpoonacularApiService serán relativas a esta URL.
    private const val BASE_URL = "https://api.spoonacular.com/"

    /**
     * Creación de la instancia de Retrofit usando inicialización perezosa (`lazy`).
     * La delegación `by lazy` significa que el bloque de código para crear el objeto Retrofit
     * solo se ejecutará la primera vez que se acceda a la propiedad `retrofit`.
     * En las siguientes llamadas, se devolverá la instancia ya creada. Esto optimiza el arranque de la app.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 1. Se establece la URL base para todas las peticiones.
            // 2. Se añade un convertidor. Retrofit necesita saber cómo procesar el JSON que recibe de la API.
            //    `GsonConverterFactory` utiliza la librería GSON de Google para mapear automáticamente
            //    el JSON a nuestras clases de datos Kotlin (ej: Recipe, MealPlan, etc.).
            .addConverterFactory(GsonConverterFactory.create())
            // 3. Se construye el objeto Retrofit.
            .build()
    }

    /**
     * Proporciona la implementación concreta de la interfaz [SpoonacularApiService].
     * Retrofit toma nuestra interfaz y genera dinámicamente una clase que implementa todos los métodos (ej: searchRecipes).
     * También usa inicialización perezosa por las mismas razones de eficiencia.
     * Los repositorios obtendrán el servicio a través de esta propiedad.
     * 
     * Uso: `RetrofitClient.apiService.searchRecipes(...)`
     */
    val apiService: SpoonacularApiService by lazy {
        retrofit.create(SpoonacularApiService::class.java)
    }
}