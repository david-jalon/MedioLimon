package com.mushi.mediolimon.buscar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.buscar.model.Recipe
import kotlinx.coroutines.launch

/**
 * Representa el estado completo de la UI para la pantalla de búsqueda.
 * Usar una única clase de estado previene inconsistencias y race conditions
 * al actualizar la interfaz de usuario desde múltiples LiveData.
 */
data class BuscarUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String? = null
)

class BuscarViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _uiState = MutableLiveData(BuscarUiState())
    val uiState: LiveData<BuscarUiState> = _uiState

    // --- Estado interno del ViewModel ---
    private var currentOffset = 0
    private var currentIngredient: String? = null
    private var isSearchMode = false
    private val pageSize = 10

    init {
        fetchInitialRandomRecipes()
    }

    fun fetchInitialRandomRecipes() {
        isSearchMode = false
        _uiState.value = BuscarUiState(isLoading = true) // Estado inicial de carga

        viewModelScope.launch {
            try {
                val response = repository.getRandomRecipes(BuildConfig.SPOONACULAR_API_KEY, 20)
                val recipes = response?.recipes ?: emptyList()
                if (recipes.isNotEmpty()) {
                    _uiState.value = BuscarUiState(recipes = recipes)
                } else {
                    _uiState.value = BuscarUiState(error = "No se encontraron recetas aleatorias.")
                }
            } catch (e: Exception) {
                _uiState.value = BuscarUiState(error = "The connection to the server has been lost. \nError: ${e.message}")
            } 
        }
    }

    fun searchByIngredient(ingredient: String) {
        if (ingredient.isBlank()) return

        currentIngredient = ingredient
        currentOffset = 0
        isSearchMode = true
        _uiState.value = uiState.value?.copy(isLoading = true, recipes = emptyList(), error = null)

        fetchMoreRecipesInternal()
    }

    fun loadMoreRecipes() {
        if (!isSearchMode) return

        _uiState.value = uiState.value?.copy(isLoadingMore = true)
        fetchMoreRecipesInternal()
    }

    private fun fetchMoreRecipesInternal() {
        viewModelScope.launch {
            try {
                val response = repository.searchRecipesByIngredients(
                    apiKey = BuildConfig.SPOONACULAR_API_KEY,
                    ingredients = currentIngredient,
                    offset = currentOffset
                )

                if (response != null && response.results.isNotEmpty()) {
                    val currentList = _uiState.value?.recipes ?: emptyList()
                    val newList = currentList + response.results
                    currentOffset += response.results.size
                    
                    _uiState.value = uiState.value?.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        recipes = newList,
                        canLoadMore = response.results.size == pageSize,
                        error = null
                    )
                } else {
                     _uiState.value = uiState.value?.copy(
                        isLoading = false, 
                        isLoadingMore = false,
                        canLoadMore = false, // No hay más resultados
                        // Mantiene las recetas actuales si las hay, si no, muestra error
                        error = if (_uiState.value?.recipes.isNullOrEmpty()) "No se encontraron recetas para '$currentIngredient'." else null 
                    )
                }
            } catch (e: Exception) {
                _uiState.value = uiState.value?.copy(
                    isLoading = false, 
                    isLoadingMore = false, 
                    error = "Error de red: ${e.message}"
                )
            }
        }
    }
}
