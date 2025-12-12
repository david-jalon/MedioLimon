package com.mushi.mediolimon.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mushi.mediolimon.buscar.model.ExtendedIngredient

/**
 * Conversores de tipos para la base de datos Room.
 *
 * Room utiliza esta clase para convertir tipos de datos complejos que no puede
 * manejar de forma nativa (como listas de objetos) a tipos de datos primitivos
 * que sí puede almacenar en la base de datos (como un String).
 */
class Converters {

    private val gson = Gson()

    /**
     * Convierte una lista de [ExtendedIngredient] a su representación en formato JSON (String).
     * Es null-safe: si la lista es nula, devuelve un string nulo.
     */
    @TypeConverter
    fun fromIngredientList(value: List<ExtendedIngredient>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Convierte un [String] en formato JSON a una lista de [ExtendedIngredient].
     * Es null-safe: si el string es nulo, devuelve una lista nula.
     */
    @TypeConverter
    fun toIngredientList(value: String?): List<ExtendedIngredient>? {
        return value?.let {
            val type = object : TypeToken<List<ExtendedIngredient>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
