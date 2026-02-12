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
     * @return Un objeto [RecipeResponse] si la llamada es exitosa, o null si la respuesta es vacía.
     * @throws Exception Si ocurre un error de red o de la API (ej: 401 Unauthorized).
     */
    suspend fun searchRecipesByIngredients(apiKey: String, ingredients: String?, offset: Int): RecipeResponse? {
        // Se elimina el bloque try-catch.
        // Ahora, si Retrofit lanza una excepción (como HttpException por un error 401 o IOException por un fallo de red),
        // esta no será capturada aquí, sino que se propagará hacia la capa que llamó a esta función (el ViewModel).
        // De esta forma, el ViewModel puede saber que ocurrió un error y actuar en consecuencia.
        return apiService.searchRecipes(
            apiKey = apiKey,
            ingredients = ingredients,
            offset = offset
        )
    }

    /**
     * Llama a la API para obtener una lista de recetas aleatorias.
     *
     * @param apiKey La clave de la API para la autenticación.
     * @param number El número de recetas aleatorias que se desean obtener.
     * @return Un objeto [RandomRecipesResponse] si la llamada es exitosa, o null si la respuesta es vacía.
     * @throws Exception Si ocurre un error de red o de la API.
     */
    suspend fun getRandomRecipes(apiKey: String, number: Int): RandomRecipesResponse? {
        // Al igual que en la función anterior, dejamos que la excepción se propague al ViewModel.
        return apiService.getRandomRecipes(apiKey = apiKey, number = number)
    }
}
