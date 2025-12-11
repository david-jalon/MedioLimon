package com.mushi.mediolimon.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa un ingrediente en la base de datos de la lista de la compra.
 *
 * @property id El identificador único del ingrediente, generado automáticamente.
 * @property nombre El nombre del ingrediente (por ejemplo, "Leche", "Huevos").
 * @property comprado Indica si el ingrediente ha sido marcado como comprado.
 */
@Entity(
    tableName = "ingredientes",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class Ingrediente (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val comprado: Boolean = false
)