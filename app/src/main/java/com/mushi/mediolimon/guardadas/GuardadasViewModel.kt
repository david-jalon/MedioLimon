package com.mushi.mediolimon.guardadas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mushi.mediolimon.data.database.AppDatabase
import com.mushi.mediolimon.data.database.dao.RecetaGuardadaDao
import com.mushi.mediolimon.data.database.entities.RecetaGuardada
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel para el fragmento de recetas guardadas.
 *
 * Se encarga de obtener y mantener la lista de recetas guardadas desde la base de datos
 * y de gestionar las operaciones de eliminación.
 */
class GuardadasViewModel(application: Application) : AndroidViewModel(application) {

    val recetasGuardadas: LiveData<List<RecetaGuardada>>
    private val recetaGuardadaDao: RecetaGuardadaDao

    init {
        recetaGuardadaDao = AppDatabase.getDatabase(application).recetaGuardadaDao()
        recetasGuardadas = recetaGuardadaDao.getAllRecetasGuardadas()
    }

    /**
     * Elimina una receta de la base de datos en un hilo secundario.
     *
     * @param receta La [RecetaGuardada] a eliminar.
     */
    fun delete(receta: RecetaGuardada) {
        viewModelScope.launch(Dispatchers.IO) {
            recetaGuardadaDao.delete(receta)
        }
    }
}
