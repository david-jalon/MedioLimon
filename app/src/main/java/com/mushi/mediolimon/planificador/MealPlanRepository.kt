package com.mushi.mediolimon.planificador

import com.mushi.mediolimon.api.RetrofitClient
import com.mushi.mediolimon.planificador.model.MealPlan

/**
 * Repositorio para la sección del Planificador de Comidas.
 * Su función es la de ser un intermediario entre el [MealPlanViewModel] y la fuente de datos (la API).
 * Esta capa de abstracción permite que la lógica de obtención de datos esté centralizada y sea fácil de mantener o sustituir.
 */
class MealPlanRepository {

    // Obtiene una instancia del servicio de la API a través del singleton RetrofitClient.
    private val apiService = RetrofitClient.apiService

    /**
     * Llama a la API para generar un plan de comidas semanal, con la posibilidad de filtrar por calorías y dieta.
     *
     * @param apiKey La clave de la API para la autenticación.
     * @param targetCalories El objetivo de calorías diarias. Puede ser nulo.
     * @param diet La dieta a seguir (ej: "vegetarian"). Puede ser nula.
     * @return Un objeto [MealPlan] si la llamada a la API es exitosa.
     * @throws Exception Si ocurre un error de red o de la API.
     */
    suspend fun generateMealPlan(apiKey: String, targetCalories: Int?, diet: String?): MealPlan? {
        // Se elimina el bloque try-catch para permitir que las excepciones (ej. HttpException, IOException)
        // se propaguen hasta el ViewModel. El ViewModel será el encargado de capturar estas excepciones
        // y mostrar un mensaje de error adecuado al usuario.
        return apiService.generateMealPlan(
            apiKey = apiKey,
            timeFrame = "week", // Pide un plan semanal por defecto.
            targetCalories = targetCalories,
            diet = diet
        )
    }
}
