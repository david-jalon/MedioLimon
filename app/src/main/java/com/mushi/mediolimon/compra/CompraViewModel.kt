package com.mushi.mediolimon.compra

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mushi.mediolimon.data.database.AppDatabase
import com.mushi.mediolimon.data.database.dao.IngredienteDao
import com.mushi.mediolimon.data.database.entities.Ingrediente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de la lista de la compra (`CompraFragment`).
 *
 * Este ViewModel se encarga de la lógica de negocio y la comunicación con la capa de datos (la base de datos Room).
 * Proporciona métodos para obtener, insertar, actualizar y eliminar ingredientes, y expone los datos
 * a la UI a través de [LiveData], asegurando que la UI siempre refleje el estado actual de los datos.
 *
 * @param application La instancia de la aplicación, necesaria para obtener el contexto de la base de datos.
 */
class CompraViewModel(application: Application) : AndroidViewModel(application) {

    private val ingredienteDao: IngredienteDao

    /**
     * Lista de todos los ingredientes en la base de datos.
     * Es un [LiveData], por lo que la UI puede observar los cambios y actualizarse automáticamente.
     */
    val allIngredientes: LiveData<List<Ingrediente>>

    init {
        val database = AppDatabase.getDatabase(application)
        ingredienteDao = database.IngredienteDao()
        allIngredientes = ingredienteDao.getAllIngredientes()
    }

    /**
     * Inserta un nuevo ingrediente en la base de datos.
     * La operación se ejecuta en un hilo de fondo ([Dispatchers.IO]) para no bloquear la UI.
     *
     * @param ingrediente El [Ingrediente] a insertar.
     */
    fun insert(ingrediente: Ingrediente) = viewModelScope.launch(Dispatchers.IO) {
        ingredienteDao.insert(ingrediente)
    }

    /**
     * Actualiza un ingrediente existente en la base de datos.
     * La operación se ejecuta en un hilo de fondo ([Dispatchers.IO]).
     *
     * @param ingrediente El [Ingrediente] a actualizar.
     */
    fun update(ingrediente: Ingrediente) = viewModelScope.launch(Dispatchers.IO) {
        ingredienteDao.update(ingrediente)
    }

    /**
     * Elimina un ingrediente de la base de datos.
     * La operación se ejecuta en un hilo de fondo ([Dispatchers.IO]).
     *
     * @param ingrediente El [Ingrediente] a eliminar.
     */
    fun delete(ingrediente: Ingrediente) = viewModelScope.launch(Dispatchers.IO) {
        ingredienteDao.delete(ingrediente)
    }
}