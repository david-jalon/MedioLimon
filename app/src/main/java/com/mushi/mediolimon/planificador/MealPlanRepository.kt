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
            // Intentamos pedir la semana de nuevo, si falla saltará al catch con los datos de prueba
            apiService.generateMealPlan(
                apiKey = apiKey,
                timeFrame = "week", 
                targetCalories = targetCalories,
                diet = diet
            )
        } catch (e: Exception) {
            getMockWeeklyPlan()
        }
    }

    private fun getMockWeeklyPlan(): MealPlan {
        val days = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        val weekMap = days.associateWith { day ->
            DayPlan(
                meals = listOf(
                    Meal(1, "Roasted Plum Oatmeal ($day)", "jpg", 45, 2, ""),
                    Meal(2, "Fig and Goat Cheese Pizza", "jpg", 15, 1, ""),
                    Meal(3, "The Best Of England Salad", "jpg", 45, 4, "")
                ),
                nutrients = Nutrients(2000.0, 80.0, 60.0, 250.0)
            )
        }
        return MealPlan(week = weekMap)
    }
}