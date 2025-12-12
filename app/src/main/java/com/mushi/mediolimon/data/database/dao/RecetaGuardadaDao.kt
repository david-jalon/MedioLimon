package com.mushi.mediolimon.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mushi.mediolimon.data.database.entities.RecetaGuardada

/**
 * Data Access Object (DAO) para la entidad [RecetaGuardada].
 *
 * Define los métodos para interactuar con la tabla `recetas_guardadas` en la base de datos.
 */
@Dao
interface RecetaGuardadaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(receta: RecetaGuardada)

    @Query("SELECT * FROM recetas_guardadas ORDER BY title ASC")
    fun getAllRecetasGuardadas(): LiveData<List<RecetaGuardada>>

    @Query("SELECT * FROM recetas_guardadas WHERE id = :id")
    fun getRecetaById(id: Int): LiveData<RecetaGuardada>

    /**
     * Elimina una receta de la base de datos.
     *
     * @param receta La [RecetaGuardada] a eliminar.
     */
    @Delete
    suspend fun delete(receta: RecetaGuardada)
}
