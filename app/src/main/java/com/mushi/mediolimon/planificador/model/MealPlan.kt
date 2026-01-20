package com.mushi.mediolimon.planificador.model

import com.google.gson.annotations.SerializedName

// --- Modelos de datos para la funcionalidad del Planificador de Comidas ---
// Estas clases están diseñadas para mapear la respuesta JSON del endpoint 'generate' de la API de Spoonacular.

/**
 * Representa la respuesta completa del plan de comidas semanal de la API.
 * La anotación `@SerializedName` indica a la librería GSON cómo mapear la clave del JSON a la propiedad de la clase.
 */
data class MealPlan(
    /**
     * Un mapa que contiene el plan para cada día de la semana.
     * La clave del mapa (ej: "monday") es el nombre del día en inglés, tal como lo devuelve la API.
     * El valor es un objeto [DayPlan] que contiene las comidas y nutrientes para ese día.
     */
    @SerializedName("week") val week: Map<String, DayPlan>
)

/**
 * Representa el plan de comidas para un solo día.
 */
data class DayPlan(
    /** Lista de las comidas programadas para este día. */
    @SerializedName("meals") val meals: List<Meal>,
    /** Resumen de los nutrientes totales para este día (suma de todas las comidas). */
    @SerializedName("nutrients") val nutrients: Nutrients
)

/**
 * Representa una única comida (receta) dentro del plan diario.
 */
data class Meal(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("imageType") val imageType: String, // Ej: "jpg"
    @SerializedName("readyInMinutes") val readyInMinutes: Int, // Tiempo de preparación en minutos.
    @SerializedName("servings") val servings: Int, // Número de raciones.
    @SerializedName("sourceUrl") val sourceUrl: String // Enlace a la receta original.
) {
    /**
     * Función de utilidad para construir la URL completa de la imagen de la receta.
     * La API de Spoonacular a menudo proporciona solo el ID y el formato de la imagen por separado.
     * Este metodo los combina para formar una URL válida y con un tamaño específico (556x370).
     * @return La URL completa de la imagen.
     */
    fun getImageUrl(): String {
        return "https://spoonacular.com/recipeImages/$id-556x370.$imageType"
    }
}

/**
 * Representa la información nutricional resumida para un día o una comida.
 * Los valores son de tipo Double, ya que la API los devuelve con decimales.
 */
data class Nutrients(
    @SerializedName("calories") val calories: Double,
    @SerializedName("protein") val protein: Double,
    @SerializedName("fat") val fat: Double,
    @SerializedName("carbohydrates") val carbohydrates: Double
)
