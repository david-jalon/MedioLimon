package com.mushi.mediolimon.buscar

import com.mushi.mediolimon.api.RetrofitClient
import com.mushi.mediolimon.buscar.model.RandomRecipesResponse
import com.mushi.mediolimon.buscar.model.RecipeResponse

/**
 * Repositorio que maneja la obtención de datos de recetas desde la API.
 */
class RecipeRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Busca recetas en la API por ingredientes, con soporte para paginación.
     *
     * @param apiKey La clave de la API.
     * @param ingredients Los ingredientes a buscar.
     * @param offset El número de resultados a saltar (paginación).
     * @return Un objeto [RecipeResponse] o null si hay un error.
     */
    suspend fun searchRecipesByIngredients(apiKey: String, ingredients: String?, offset: Int): RecipeResponse? {
        return try {
            apiService.searchRecipes(
                apiKey = apiKey,
                ingredients = ingredients,
                offset = offset
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene una lista de recetas aleatorias desde la API.
     *
     * @param apiKey La clave de la API.
     * @param number El número de recetas a obtener.
     * @return Un objeto [RandomRecipesResponse] o null si hay un error.
     */
    suspend fun getRandomRecipes(apiKey: String, number: Int): RandomRecipesResponse? {
        return try {
            apiService.getRandomRecipes(apiKey = apiKey, number = number)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
