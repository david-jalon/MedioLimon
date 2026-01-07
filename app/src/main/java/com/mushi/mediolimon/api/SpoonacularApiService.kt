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
 * Cada función corresponde a una petición a la API y está anotada con el método HTTP (GET)
 * y la ruta relativa del endpoint.
 */
interface SpoonacularApiService {

    /**
     * Busca recetas que incluyan un ingrediente específico. Soporta paginación.
     * Utiliza el endpoint 'complexSearch' de la API.
     *
     * @param apiKey La clave de la API para autenticar la solicitud.
     * @param ingredients Una cadena de texto con los ingredientes a incluir, separados por comas (ej: "tomato,cheese").
     * @param number El número máximo de resultados a devolver por página (por defecto, 10).
     * @param offset El número de resultados a saltar, útil para implementar la paginación.
     * @return Un objeto [RecipeResponse] que contiene la lista de recetas encontradas y datos de paginación.
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
     * Perfecto para mostrar contenido inicial o descubrir nuevas recetas.
     *
     * @param apiKey La clave de la API para autenticar la solicitud.
     * @param number El número de recetas aleatorias a obtener (por defecto, 10).
     * @return Un objeto [RandomRecipesResponse] que envuelve la lista de recetas aleatorias.
     */
    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 10
    ): RandomRecipesResponse

    /**
     * Obtiene la información detallada de una única receta a partir de su ID.
     * Esto incluye el título, las instrucciones de preparación y la lista de ingredientes.
     *
     * @param id El identificador único de la receta, que se inserta en la ruta de la URL.
     * @param apiKey La clave de la API para autenticar la solicitud.
     * @return Un objeto [RecipeDetail] con todos los detalles de la receta.
     */
    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): RecipeDetail

    /**
     * Genera un plan de comidas para un período de tiempo determinado (día o semana).
     * Puede ser personalizado con un objetivo de calorías y una dieta específica.
     *
     * @param apiKey La clave de la API para autenticar la solicitud.
     * @param timeFrame El período de tiempo, que puede ser "day" (día) o "week" (semana, por defecto).
     * @param targetCalories El objetivo de calorías diarias (opcional).
     * @param diet La dieta a seguir (ej: "vegetarian", "vegan", etc.) (opcional).
     * @return Un objeto [MealPlan] con el plan de comidas generado para el período especificado.
     */
    @GET("mealplanner/generate")
    suspend fun generateMealPlan(
        @Query("apiKey") apiKey: String,
        @Query("timeFrame") timeFrame: String = "week",
        @Query("targetCalories") targetCalories: Int?,
        @Query("diet") diet: String?
    ): MealPlan
}
