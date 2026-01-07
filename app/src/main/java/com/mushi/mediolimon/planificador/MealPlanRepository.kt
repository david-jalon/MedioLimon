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
     * @return Un objeto [MealPlan] si la llamada a la API es exitosa, o `null` si ocurre cualquier tipo de error.
     */
    suspend fun generateMealPlan(apiKey: String, targetCalories: Int?, diet: String?): MealPlan? {
        // Se utiliza un bloque try-catch para capturar cualquier excepción que pueda ocurrir durante la llamada de red.
        // Esto es crucial para la estabilidad de la app, ya que evita que se cierre si no hay conexión a internet
        // o si la API devuelve un error (ej: 401 Unauthorized).
        return try {
            apiService.generateMealPlan(
                apiKey = apiKey,
                timeFrame = "week", // Pide un plan semanal por defecto.
                targetCalories = targetCalories,
                diet = diet
            )
        } catch (e: Exception) {
            // En una aplicación de producción, sería ideal registrar este error en un servicio de monitorización.
            // Por ahora, simplemente se imprime en la consola y se devuelve null para que el ViewModel gestione el error.
            e.printStackTrace()
            null
        }
    }
}
