package com.mushi.mediolimon.api

import com.mushi.mediolimon.buscar.model.RandomRecipesResponse
import com.mushi.mediolimon.buscar.model.RecipeDetail
import com.mushi.mediolimon.buscar.model.RecipeResponse
import com.mushi.mediolimon.planificador.model.MealPlan
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz que define los endpoints de la API de Spoonacular usando Retrofit.
 */
interface SpoonacularApiService {

    /**
     * Busca recetas que incluyan un ingrediente específico. Soporta paginación.
     *
     * @param apiKey La clave de la API.
     * @param ingredients Los ingredientes a incluir (ej: "tomato,cheese").
     * @param number El número de resultados a devolver.
     * @param offset El número de resultados a saltar (paginación).
     * @return Un objeto [RecipeResponse] con la lista de recetas.
     */
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("includeIngredients") ingredients: String?,
        @Query("number") number: Int = 10,
        @Query("offset") offset: Int = 0
    ): RecipeResponse

    /**
     * Obtiene un número determinado de recetas aleatorias.
     *
     * @param apiKey La clave de la API.
     * @param number El número de recetas aleatorias a obtener.
     * @return Un objeto [RandomRecipesResponse] con la lista de recetas.
     */
    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 10
    ): RandomRecipesResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): RecipeDetail

    @GET("mealplanner/generate")
    suspend fun generateMealPlan(
        @Query("apiKey") apiKey: String,
        @Query("timeFrame") timeFrame: String = "week",
        @Query("targetCalories") targetCalories: Int?,
        @Query("diet") diet: String?
    ): MealPlan
}
