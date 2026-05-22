package com.mushi.mediolimon.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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