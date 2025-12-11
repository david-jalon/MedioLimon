package com.mushi.mediolimon.buscar.model

/**
 * Representa una receta individual en la lista de resultados de búsqueda.
 * Contiene la información básica que se muestra en la tarjeta.
 */
data class Recipe(
    val id: Int,         // Identificador único de la receta.
    val title: String,   // Título de la receta.
    val image: String,   // URL de la imagen de la receta.
    val imageType: String // Formato de la imagen (ej: "jpg").
)

/**
 * Representa la respuesta completa de la API cuando se busca una lista de recetas.
 * La librería GSON se encarga de mapear el JSON a esta estructura.
 */
data class RecipeResponse(
    // La lista de recetas encontradas.
    val results: List<Recipe>,
    // El número de recetas que se saltaron (para paginación).
    val offset: Int,
    // El número de recetas devueltas en esta respuesta.
    val number: Int,
    // El número total de recetas que coinciden con la búsqueda.
    val totalResults: Int
)

/**
 * Representa un ingrediente con su información detallada.
 * GSON mapeará los datos de "extendedIngredients" a objetos de esta clase.
 */
data class ExtendedIngredient(
    val id: Int,             // Identificador único del ingrediente.
    val name: String,        // Nombre limpio del ingrediente (ej: "flour").
    val original: String     // La descripción original completa (ej: "2 cups all-purpose flour").
)

/**
 * Representa los detalles de una receta específica obtenidos del endpoint de información.
 */
data class RecipeDetail(
    val id: Int,         // Identificador único de la receta.
    val title: String,   // Título de la receta.
    // Las instrucciones de preparación. Pueden contener formato HTML.
    val instructions: String,
    // La lista de ingredientes necesarios para la receta.
    val extendedIngredients: List<ExtendedIngredient> // Lista de ingredientes.
)
