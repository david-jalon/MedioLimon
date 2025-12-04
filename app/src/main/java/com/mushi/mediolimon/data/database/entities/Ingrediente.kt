package com.mushi.mediolimon.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Clase que representa un ingrediente en la lista de la compra.
 *
 * @property id El identificador único de la nota (generado automáticamente).
 * @property text El texto del ingrediente.
 */

@Entity(tableName = "ingredientes")
data class Ingrediente (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String
)