package com.mushi.mediolimon.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.mushi.mediolimon.data.database.entities.Ingrediente

/**
 * Clase de acceso a datos para la entidad Ingrediente.
 *
 *  Esta interfaz define los métodos para interactuar con la tabla `ingredientes` en la base de datos.
 *  Todas las funciones se marcan como `suspend` para asegurar que se ejecuten en un hilo secundario
 */

@Dao
interface IngredienteDao {

    @Insert
    suspend fun insertIngrediente(ingrediente: Ingrediente)

    @Delete
    suspend fun deleteIngrediente(ingrediente: Ingrediente)

}