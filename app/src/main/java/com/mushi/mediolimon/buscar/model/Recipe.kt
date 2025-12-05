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
 * Representa los detalles de una receta específica obtenidos del endpoint de información.
 */
data class RecipeDetail(
    val id: Int,         // Identificador único de la receta.
    val title: String,   // Título de la receta.
    // Las instrucciones de preparación. Pueden contener formato HTML.
    val instructions: String
)
