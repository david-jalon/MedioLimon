package com.mushi.mediolimon.planificador.model

import com.google.gson.annotations.SerializedName

/**
 * Representa el plan de comidas para una semana completa.
 * Contiene un mapa donde cada clave es un día de la semana ("monday", "tuesday", etc.)
 * y el valor es el plan de comidas para ese día.
 */
data class MealPlan(
    @SerializedName("week") val week: Map<String, DayPlan>
)

/**
 * Representa el plan de comidas para un solo día.
 * Incluye una lista de comidas y un resumen de los nutrientes totales para ese día.
 */
data class DayPlan(
    @SerializedName("meals") val meals: List<Meal>,
    @SerializedName("nutrients") val nutrients: Nutrients
)

/**
 * Representa una única comida dentro del plan diario.
 * Contiene detalles como el ID, título, tiempo de preparación y URL de la imagen.
 */
data class Meal(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("imageType") val imageType: String,
    @SerializedName("readyInMinutes") val readyInMinutes: Int,
    @SerializedName("servings") val servings: Int,
    @SerializedName("sourceUrl") val sourceUrl: String
) {
    /**
     * Construye la URL completa de la imagen de la receta.
     * Spoonacular proporciona el ID y el tipo de imagen, que se combinan para formar la URL.
     */
    fun getImageUrl(): String {
        return "https://spoonacular.com/recipeImages/$id-556x370.$imageType"
    }
}

/**
 * Representa la información nutricional resumida para un día o una comida.
 */
data class Nutrients(
    @SerializedName("calories") val calories: Double,
    @SerializedName("protein") val protein: Double,
    @SerializedName("fat") val fat: Double,
    @SerializedName("carbohydrates") val carbohydrates: Double
)
