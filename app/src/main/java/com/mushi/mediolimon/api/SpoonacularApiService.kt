package com.mushi.mediolimon.api

import com.mushi.mediolimon.buscar.model.RecipeDetail
import com.mushi.mediolimon.buscar.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz que define los endpoints de la API de Spoonacular usando Retrofit.
 */
interface SpoonacularApiService {

    /**
     * Busca una lista de recetas basadas en varios filtros.
     * @param apiKey La clave de la API para autenticar la solicitud.
     * @param query El texto de búsqueda (ej: "pasta").
     * @param diet El filtro de dieta (ej: "vegan", "vegetarian").
     * @param type El tipo de plato (ej: "main course", "dessert").
     * @param number El número máximo de resultados a devolver.
     * @return Un objeto [RecipeResponse] que contiene la lista de recetas encontradas.
     */
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String?,
        @Query("diet") diet: String?,
        @Query("type") type: String?,
        @Query("number") number: Int = 1 // VALOR TEMPORAL A 1 PARA NO GASTAR EL TOKEN DE LA API
    ): RecipeResponse

    /**
     * Obtiene la información detallada de una única receta a partir de su ID.
     * @param id El identificador único de la receta.
     * @param apiKey La clave de la API para autenticar la solicitud.
     * @return Un objeto [RecipeDetail] con los detalles de la receta, incluyendo las instrucciones.
     */
    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): RecipeDetail
}