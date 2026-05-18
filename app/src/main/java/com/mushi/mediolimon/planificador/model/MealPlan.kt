package com.mushi.mediolimon.planificador.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo flexible para el Plan de Comidas.
 * Soporta tanto el formato semanal (un mapa de días) como el formato diario (comidas directas).
 */
data class MealPlan(
    /** Formato Semanal: Mapa de días (ej: "monday" -> DayPlan) */
    @SerializedName("week") val week: Map<String, DayPlan>? = null,
    
    /** Formato Diario: Lista de comidas directa */
    @SerializedName("meals") val meals: List<Meal>? = null,
    
    /** Formato Diario: Nutrientes directos */
    @SerializedName("nutrients") val nutrients: Nutrients? = null
)

/**
 * Representa el plan de comidas para un solo día (usado en el mapa semanal).
 */
data class DayPlan(
    @SerializedName("meals") val meals: List<Meal>,
    @SerializedName("nutrients") val nutrients: Nutrients
)

/**
 * Representa una única comida dentro del plan.
 */
data class Meal(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("imageType") val imageType: String,
    @SerializedName("readyInMinutes") val readyInMinutes: Int,
    @SerializedName("servings") val servings: Int,
    @SerializedName("sourceUrl") val sourceUrl: String
) {
    fun getImageUrl(): String {
        return "https://spoonacular.com/recipeImages/$id-556x370.$imageType"
    }
}

data class Nutrients(
    @SerializedName("calories") val calories: Double,
    @SerializedName("protein") val protein: Double,
    @SerializedName("fat") val fat: Double,
    @SerializedName("carbohydrates") val carbohydrates: Double
)
