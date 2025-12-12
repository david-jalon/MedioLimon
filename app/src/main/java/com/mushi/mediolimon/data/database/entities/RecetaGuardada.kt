package com.mushi.mediolimon.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mushi.mediolimon.buscar.model.ExtendedIngredient

/**
 * Representa una receta guardada en la base de datos local.
 *
 * @property id El ID único de la receta, que se corresponde con el de la API de Spoonacular.
 * @property title El título de la receta.
 * @property imageUrl La URL de la imagen de la receta.
 * @property instructions Las instrucciones de preparación de la receta.
 * @property extendedIngredients La lista de ingredientes de la receta.
 */
@Entity(tableName = "recetas_guardadas")
data class RecetaGuardada(
    @PrimaryKey
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val instructions: String?,
    val extendedIngredients: List<ExtendedIngredient>?
)
