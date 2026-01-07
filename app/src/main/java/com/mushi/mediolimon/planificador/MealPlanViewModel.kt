package com.mushi.mediolimon.planificador

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mushi.mediolimon.planificador.model.MealPlan
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla del Planificador de Comidas.
 * Su responsabilidad es obtener y mantener los datos del plan de comidas, 
 * y exponerlos a la vista ([PlanificadorFragment]) a través de LiveData.
 * Actúa como un intermediario entre el Repositorio y la UI.
 */
class MealPlanViewModel : ViewModel() {

    // Instancia del repositorio, que es la única fuente de datos para este ViewModel.
    private val repository = MealPlanRepository()

    // --- LiveData para el estado de la UI ---

    // Backing property para el plan de comidas. Es mutable para que el ViewModel pueda actualizar su valor.
    private val _mealPlan = MutableLiveData<MealPlan?>()
    // LiveData público e inmutable. El Fragment lo observará para reaccionar a los cambios en los datos.
    // Es nullable porque la petición a la API puede fallar.
    val mealPlan: LiveData<MealPlan?> = _mealPlan

    // Backing property para el estado de carga.
    private val _isLoading = MutableLiveData<Boolean>()
    // LiveData público para que la UI pueda mostrar u ocultar un ProgressBar.
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Inicia la generación de un nuevo plan de comidas.
     * Esta función es llamada por la vista (el Fragment) cuando el usuario quiere un nuevo plan.
     *
     * @param apiKey La clave de la API para la autenticación.
     * @param targetCalories El objetivo de calorías diarias (opcional).
     * @param diet La dieta a seguir (opcional).
     */
    fun generateMealPlan(apiKey: String, targetCalories: Int?, diet: String?) {
        // Se utiliza viewModelScope para lanzar una corrutina. Esta corrutina está ligada al ciclo de vida
        // del ViewModel, lo que significa que se cancelará automáticamente si el ViewModel es destruido.
        // Esto previene fugas de memoria y trabajo innecesario.
        viewModelScope.launch {
            // 1. Indicar que la carga ha comenzado. El Fragment observará este cambio y mostrará el ProgressBar.
            _isLoading.value = true 

            // 2. Llamar al repositorio (en un hilo secundario) para obtener los datos. 
            // La función del repositorio es suspend, por lo que la corrutina se pausará aquí hasta que la API responda.
            val plan = repository.generateMealPlan(apiKey, targetCalories, diet)
            
            // 3. Actualizar el LiveData con el resultado. Si la llamada falló, `plan` será null.
            _mealPlan.value = plan
            
            // 4. Indicar que la carga ha terminado. El Fragment observará este cambio y ocultará el ProgressBar.
            _isLoading.value = false 
        }
    }
}
