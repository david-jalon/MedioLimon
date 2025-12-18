package com.mushi.mediolimon.planificador

import com.mushi.mediolimon.api.RetrofitClient
import com.mushi.mediolimon.planificador.model.MealPlan

/**
 * El repositorio se encarga de obtener los datos del plan de comidas.
 * Abstrae la fuente de datos (en este caso, la API remota) del resto de la app.
 */
class MealPlanRepository {

    // Instancia del servicio de la API obtenida desde el cliente de Retrofit.
    private val apiService = RetrofitClient.apiService

    /**
     * Llama a la API para generar un plan de comidas semanal.
     *
     * @param apiKey La clave de la API para la autenticación.
     * @param targetCalories El objetivo de calorías diarias. Puede ser nulo.
     * @param diet La dieta a seguir. Puede ser nula.
     * @return Un objeto [MealPlan] si la llamada es exitosa, o null si falla.
     */
    suspend fun generateMealPlan(apiKey: String, targetCalories: Int?, diet: String?): MealPlan? {
        // El bloque try-catch maneja posibles errores de red o de la API (ej: clave inválida).
        return try {
            apiService.generateMealPlan(
                apiKey = apiKey,
                timeFrame = "week",
                targetCalories = targetCalories,
                diet = diet
            )
        } catch (e: Exception) {
            // Si ocurre un error, se imprime en la consola y se devuelve null.
            e.printStackTrace()
            null
        }
    }
}
