package com.mushi.mediolimon.planificador

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mushi.mediolimon.planificador.model.MealPlan
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla del planificador de comidas.
 * Se encarga de la lógica de negocio y de la comunicación con el repositorio.
 */
class MealPlanViewModel : ViewModel() {

    // Repositorio para obtener los datos del plan de comidas.
    private val repository = MealPlanRepository()

    // LiveData privado y mutable para el plan de comidas.
    // Solo el ViewModel puede modificar su valor.
    private val _mealPlan = MutableLiveData<MealPlan?>()

    // LiveData público e inmutable expuesto a la UI.
    // La UI puede observar este LiveData para reaccionar a los cambios en los datos.
    val mealPlan: LiveData<MealPlan?> = _mealPlan

    // LiveData para gestionar el estado de carga (ej: mostrar un ProgressBar).
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Inicia la generación del plan de comidas.
     * Utiliza el viewModelScope para lanzar una corrutina de forma segura.
     *
     * @param apiKey La clave de la API para la autenticación.
     * @param targetCalories El objetivo de calorías diarias (opcional).
     * @param diet La dieta a seguir (opcional).
     */
    fun generateMealPlan(apiKey: String, targetCalories: Int?, diet: String?) {
        // Inicia una corrutina en el scope del ViewModel.
        // Esta corrutina se cancelará automáticamente si el ViewModel se destruye.
        viewModelScope.launch {
            _isLoading.value = true // Indica que la carga ha comenzado.
            // Llama al repositorio para obtener los datos.
            val plan = repository.generateMealPlan(apiKey, targetCalories, diet)
            _mealPlan.value = plan // Actualiza el LiveData con el nuevo plan.
            _isLoading.value = false // Indica que la carga ha terminado.
        }
    }
}
