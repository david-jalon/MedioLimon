package com.mushi.mediolimon.buscar

import com.mushi.mediolimon.api.RetrofitClient
import com.mushi.mediolimon.buscar.model.RandomRecipesResponse
import com.mushi.mediolimon.buscar.model.RecipeResponse

/**
 * Repositorio para la sección de búsqueda de recetas.
 * Su principal responsabilidad es actuar como una capa de abstracción sobre la fuente de datos (la API de Spoonacular).
 * El ViewModel nunca hablará directamente con Retrofit; en su lugar, hablará con este repositorio.
 * Esto facilita las pruebas y el mantenimiento, ya que la lógica de la API está aislada en un solo lugar.
 */
class RecipeRepository {

    // Obtiene una instancia del servicio de la API a través del singleton RetrofitClient.
    private val apiService = RetrofitClient.apiService

    /**
     * Llama a la API para buscar recetas que contengan ciertos ingredientes. Soporta paginación.
     *
     * @param apiKey La clave de la API para la autenticación.
     * @param ingredients La cadena de texto con los ingredientes a buscar (ej: "tomato,cheese").
     * @param offset El punto a partir del cual se devuelven los resultados (para la paginación).
     * @return Un objeto [RecipeResponse] si la llamada es exitosa, o null si ocurre una excepción.
     */
    suspend fun searchRecipesByIngredients(apiKey: String, ingredients: String?, offset: Int): RecipeResponse? {
        // El bloque try-catch es fundamental para manejar errores de red (ej: sin conexión)
        // o errores de la API (ej: clave inválida, cuota agotada) sin que la app se cierre.
        return try {
            apiService.searchRecipes(
                apiKey = apiKey,
                ingredients = ingredients,
                offset = offset
            )
        } catch (e: Exception) {
            // En una app más compleja, aquí se podría registrar el error en un sistema de logs
            // o devolver un objeto de error más específico en lugar de simplemente null.
            e.printStackTrace()
            null
        }
    }

    /**
     * Llama a la API para obtener una lista de recetas aleatorias.
     *
     * @param apiKey La clave de la API para la autenticación.
     * @param number El número de recetas aleatorias que se desean obtener.
     * @return Un objeto [RandomRecipesResponse] si la llamada es exitosa, o null si ocurre una excepción.
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
