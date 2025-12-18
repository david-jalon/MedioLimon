package com.mushi.mediolimon.buscar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.buscar.model.Recipe
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de búsqueda de recetas.
 * Gestiona la lógica para obtener recetas aleatorias y buscar por ingrediente, incluyendo paginación.
 */
class BuscarViewModel : ViewModel() {

    private val repository = RecipeRepository()

    // LiveData para la lista de recetas.
    private val _recipes = MutableLiveData<List<Recipe>>(emptyList())
    val recipes: LiveData<List<Recipe>> = _recipes

    // LiveData para el estado de carga (pantalla completa).
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para el estado de carga de paginación ("Cargar más").
    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    // LiveData para controlar si se puede cargar más.
    private val _canLoadMore = MutableLiveData<Boolean>(false)
    val canLoadMore: LiveData<Boolean> = _canLoadMore

    // Estado interno para gestionar el modo de búsqueda y paginación.
    private var currentOffset = 0
    private var currentIngredient: String? = null
    private var isSearchMode = false
    private val pageSize = 10

    init {
        // Carga inicial de recetas aleatorias.
        fetchInitialRandomRecipes()
    }

    /**
     * Obtiene una lista inicial de recetas aleatorias.
     */
    fun fetchInitialRandomRecipes() {
        _isLoading.value = true
        isSearchMode = false // Modo aleatorio, no búsqueda.
        _canLoadMore.value = false // No hay paginación para recetas aleatorias.

        viewModelScope.launch {
            val response = repository.getRandomRecipes(BuildConfig.SPOONACULAR_API_KEY, 20)
            _recipes.value = response?.recipes ?: emptyList()
            _isLoading.value = false
        }
    }

    /**
     * Inicia una nueva búsqueda por ingrediente, reseteando la paginación.
     */
    fun searchByIngredient(ingredient: String) {
        // Si el ingrediente está vacío, no hace nada.
        if (ingredient.isBlank()) return

        currentIngredient = ingredient
        currentOffset = 0
        isSearchMode = true // Cambia a modo búsqueda.
        _isLoading.value = true // Activa el loading de pantalla completa.
        _recipes.value = emptyList() // Limpia la lista anterior.

        fetchMoreRecipesInternal()
    }

    /**
     * Carga la siguiente página de resultados (solo en modo búsqueda).
     */
    fun loadMoreRecipes() {
        if (!isSearchMode) return // No hacer nada si no estamos en modo búsqueda.

        _isLoadingMore.value = true
        fetchMoreRecipesInternal()
    }

    /**
     * Lógica interna para llamar al repositorio y actualizar el estado de la búsqueda.
     */
    private fun fetchMoreRecipesInternal() {
        viewModelScope.launch {
            val response = repository.searchRecipesByIngredients(
                apiKey = BuildConfig.SPOONACULAR_API_KEY,
                ingredients = currentIngredient,
                offset = currentOffset
            )

            if (response != null) {
                val currentList = _recipes.value ?: emptyList()
                _recipes.value = currentList + response.results

                currentOffset += response.results.size
                // El botón "Cargar más" se mostrará si la API devuelve una página completa.
                _canLoadMore.value = response.results.size == pageSize
            } else {
                _canLoadMore.value = false // Error, no se puede cargar más.
            }

            // Desactiva ambos indicadores de carga.
            _isLoading.value = false
            _isLoadingMore.value = false
        }
    }
}
