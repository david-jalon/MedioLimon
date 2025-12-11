package com.mushi.mediolimon.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mushi.mediolimon.data.database.entities.Ingrediente

/**
 * Data Access Object (DAO) para la entidad [Ingrediente].
 *
 * Esta interfaz define los métodos para interactuar con la tabla `ingredientes` en la base de datos.
 * Room generará la implementación de esta interfaz en tiempo de compilación.
 */
@Dao
interface IngredienteDao {

    /**
     * Obtiene todos los ingredientes de la base de datos, ordenados alfabéticamente por nombre.
     *
     * @return Un [LiveData] que contiene la lista de todos los [Ingrediente].
     *         LiveData notificará a los observadores cuando los datos cambien.
     */
    @Query("SELECT * FROM ingredientes ORDER BY nombre ASC")
    fun getAllIngredientes(): LiveData<List<Ingrediente>>

    /**
     * Inserta un nuevo ingrediente en la base de datos.
     * Si el ingrediente ya existe, la operación se ignora gracias a [OnConflictStrategy.IGNORE].
     *
     * @param ingrediente El [Ingrediente] a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ingrediente: Ingrediente)

    /**
     * Inserta una lista de ingredientes en la base de datos.
     * Si alguno de los ingredientes ya existe, se ignora.
     *
     * @param ingredientes La lista de [Ingrediente] a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(ingredientes: List<Ingrediente>)

    /**
     * Actualiza un ingrediente existente en la base de datos.
     *
     * @param ingrediente El [Ingrediente] a actualizar.
     */
    @Update
    suspend fun update(ingrediente: Ingrediente)

    /**
     * Elimina un ingrediente de la base de datos.
     *
     * @param ingrediente El [Ingrediente] a eliminar.
     */
    @Delete
    suspend fun delete(ingrediente: Ingrediente)
}