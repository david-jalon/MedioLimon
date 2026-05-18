package com.mushi.mediolimon.planificador

import com.mushi.mediolimon.api.RetrofitClient
import com.mushi.mediolimon.planificador.model.DayPlan
import com.mushi.mediolimon.planificador.model.Meal
import com.mushi.mediolimon.planificador.model.MealPlan
import com.mushi.mediolimon.planificador.model.Nutrients

class MealPlanRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun generateMealPlan(apiKey: String, targetCalories: Int?, diet: String?): MealPlan? {
        return try {
            apiService.generateMealPlan(
                apiKey = apiKey,
                timeFrame = "day",
                targetCalories = targetCalories,
                diet = diet
            )
        } catch (e: Exception) {
            // Si hay un error 502 o cualquier fallo, devolvemos un plan de prueba
            // para que el usuario pueda ver la interfaz diseñada.
            getMockMealPlan()
        }
    }

    private fun getMockMealPlan(): MealPlan {
        val mockMeals = listOf(
            Meal(1, "Vegetarian Omelette", "jpg", 15, 2, ""),
            Meal(2, "Quinoa Salad with Avocado", "jpg", 20, 1, ""),
            Meal(3, "Lentil Soup", "jpg", 40, 4, "")
        )
        val mockNutrients = Nutrients(1850.0, 75.0, 50.0, 210.0)
        
        return MealPlan(
            meals = mockMeals,
            nutrients = mockNutrients
        )
    }
}