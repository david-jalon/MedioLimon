package com.mushi.mediolimon.buscar.model

// --- Modelos de datos para la funcionalidad de Búsqueda y Detalle de Recetas ---
// Estas clases están diseñadas para mapear las respuestas JSON de varios endpoints de la API de Spoonacular.

/**
 * Representa una receta individual con su información básica.
 * Es el modelo principal que se usa en las listas de resultados.
 */
data class Recipe(
    /** El identificador único de la receta en la API de Spoonacular. */
    val id: Int,
    /** El título de la receta. */
    val title: String,
    /** La URL completa de la imagen de la receta. */
    val image: String,
    /** El tipo de formato de la imagen (ej: "jpg"). */
    val imageType: String
)

/**
 * Representa la respuesta específica del endpoint de recetas aleatorias (`/recipes/random`).
 * La API envuelve la lista de recetas dentro de un objeto JSON con una clave "recipes".
 */
data class RandomRecipesResponse(
    /** La lista de objetos [Recipe] aleatorios. */
    val recipes: List<Recipe>
)

/**
 * Representa la respuesta completa del endpoint de búsqueda compleja (`/recipes/complexSearch`).
 * Incluye tanto la lista de resultados como metadatos para la paginación.
 */
data class RecipeResponse(
    /** La lista de recetas encontradas que coinciden con los criterios de búsqueda. */
    val results: List<Recipe>,
    /** El número de resultados que se han saltado. Clave para la paginación. */
    val offset: Int,
    /** El número de resultados devueltos en esta petición. */
    val number: Int,
    /** El número total de recetas que existen para la consulta realizada. */
    val totalResults: Int
)

/**
 * Representa un ingrediente individual con su información detallada.
 * Se utiliza dentro de [RecipeDetail].
 */
data class ExtendedIngredient(
    /** El identificador único del ingrediente. */
    val id: Int,
    /** El nombre limpio y normalizado del ingrediente (ej: "flour"). */
    val name: String,
    /** La descripción original y completa del ingrediente tal como aparece en la receta (ej: "2 cups all-purpose flour"). */
    val original: String
)

/**
 * Representa los detalles completos de una receta específica.
 * Se obtiene del endpoint de información de receta (`/recipes/{id}/information`).
 */
data class RecipeDetail(
    /** El identificador único de la receta. */
    val id: Int,
    /** El título de la receta. */
    val title: String,
    /** Las instrucciones de preparación. Este campo puede contener etiquetas HTML. */
    val instructions: String,
    /** La lista completa de ingredientes necesarios para la receta. */
    val extendedIngredients: List<ExtendedIngredient>
)
