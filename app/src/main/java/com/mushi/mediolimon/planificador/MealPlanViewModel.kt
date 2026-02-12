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
    val mealPlan: LiveData<MealPlan?> = _mealPlan

    // Backing property para el estado de carga.
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Inicia la generación de un nuevo plan de comidas.
     * Esta función es llamada por la vista (el Fragment) cuando el usuario quiere un nuevo plan.
     *
     * @param apiKey La clave de la API para la autenticación.
     * @param targetCalories El objetivo de calorías diarias (opcional).
     * @param diet La dieta a seguir (opcional).
     */
    fun generateMealPlan(apiKey: String, targetCalories: Int?, diet: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpia el error anterior al iniciar una nueva petición

            try {
                val plan = repository.generateMealPlan(apiKey, targetCalories, diet)
                // Comprueba si el plan recibido es válido
                if (plan != null && plan.week.values.any { it.meals.isNotEmpty() }) {
                    _mealPlan.value = plan
                } else {
                    // Si el plan es nulo o está vacío, se considera un error
                    _mealPlan.value = null // Limpia el plan antiguo
                    _error.value = "A meal plan could not be generated. Please try again."
                }
            } catch (e: Exception) {
                // Si ocurre una excepción (ej: sin red, error 401), se limpia el plan y se notifica el error
                _mealPlan.value = null
                _error.value = "The connection to the server has been lost. \n\nError: ${e.message}"
            } finally {
                // Se asegura de que el estado de carga siempre se desactive al final
                _isLoading.value = false
            }
        }
    }
}
