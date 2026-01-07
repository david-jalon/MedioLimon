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
 * Este ViewModel es el "cerebro" de la pantalla y orquesta toda la lógica de negocio.
 * Se encarga de:
 *   - Obtener una lista inicial de recetas aleatorias.
 *   - Realizar búsquedas por ingrediente.
 *   - Gestionar la paginación (cargar más resultados).
 *   - Exponer el estado de la UI (listas de datos, estados de carga) a través de LiveData.
 */
class BuscarViewModel : ViewModel() {

    private val repository = RecipeRepository()

    // --- LiveData para el estado de la UI ---

    // Backing property para la lista de recetas. Es Mutable para poder modificarla desde el ViewModel.
    private val _recipes = MutableLiveData<List<Recipe>>(emptyList())
    // LiveData público e inmutable. El Fragment lo observará para pintar la lista.
    val recipes: LiveData<List<Recipe>> = _recipes

    // Backing property para el estado de carga principal (el que bloquea la pantalla).
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Backing property para el estado de carga de la paginación (el del botón "Cargar más").
    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    // Backing property para controlar la visibilidad del botón "Cargar más".
    private val _canLoadMore = MutableLiveData<Boolean>(false)
    val canLoadMore: LiveData<Boolean> = _canLoadMore

    // --- Estado interno del ViewModel ---

    private var currentOffset = 0 // Contador para la paginación. Guarda cuántos resultados se han cargado.
    private var currentIngredient: String? = null // El último ingrediente que se ha buscado.
    private var isSearchMode = false // Flag para saber si estamos en modo "búsqueda" o en modo "aleatorio".
    private val pageSize = 10 // Número de resultados que se piden en cada página de búsqueda.

    /**
     * El bloque init se ejecuta cuando se crea la instancia del ViewModel.
     * Es el lugar perfecto para cargar los datos iniciales.
     */
    init {
        fetchInitialRandomRecipes()
    }

    /**
     * Obtiene una lista inicial de recetas aleatorias. Se llama solo al principio.
     */
    fun fetchInitialRandomRecipes() {
        _isLoading.value = true // Activa el ProgressBar principal.
        isSearchMode = false      // Desactiva el modo búsqueda.
        _canLoadMore.value = false // En modo aleatorio no hay paginación.

        viewModelScope.launch {
            // Llama al repositorio para obtener 20 recetas aleatorias.
            val response = repository.getRandomRecipes(BuildConfig.SPOONACULAR_API_KEY, 20)
            // Actualiza el LiveData de recetas. Si la respuesta es nula, se envía una lista vacía.
            _recipes.value = response?.recipes ?: emptyList()
            _isLoading.value = false // Desactiva el ProgressBar principal.
        }
    }

    /**
     * Inicia una nueva búsqueda por ingrediente. Esto resetea cualquier estado anterior.
     */
    fun searchByIngredient(ingredient: String) {
        // Evita búsquedas vacías que gastarían cuota de la API innecesariamente.
        if (ingredient.isBlank()) return

        currentIngredient = ingredient // Guarda el nuevo ingrediente.
        currentOffset = 0              // Resetea el contador de paginación a cero.
        isSearchMode = true            // Activa el modo búsqueda.
        _isLoading.value = true        // Muestra el ProgressBar de pantalla completa.
        _recipes.value = emptyList()     // Limpia la lista de recetas anterior para mostrar los nuevos resultados.

        // Llama a la función interna que se encarga de hacer la petición a la API.
        fetchMoreRecipesInternal()
    }

    /**
     * Carga la siguiente página de resultados (solo funciona en modo búsqueda).
     */
    fun loadMoreRecipes() {
        // Si no estamos en modo búsqueda (ej: estamos viendo las recetas aleatorias), no hace nada.
        if (!isSearchMode) return

        _isLoadingMore.value = true // Activa el ProgressBar del botón "Cargar más".
        fetchMoreRecipesInternal()    // Llama a la misma función interna para obtener la siguiente página.
    }

    /**
     * Lógica interna y centralizada para llamar a la API en modo búsqueda.
     * Esta función es llamada tanto por searchByIngredient como por loadMoreRecipes.
     */
    private fun fetchMoreRecipesInternal() {
        viewModelScope.launch {
            val response = repository.searchRecipesByIngredients(
                apiKey = BuildConfig.SPOONACULAR_API_KEY,
                ingredients = currentIngredient,
                offset = currentOffset // Envía el offset actual para obtener la página correcta.
            )

            if (response != null) {
                // Si la respuesta es válida, combina la lista actual con los nuevos resultados.
                val currentList = _recipes.value ?: emptyList()
                _recipes.value = currentList + response.results

                // Actualiza el offset para la siguiente petición.
                currentOffset += response.results.size

                // La API nos permite cargar más si nos ha devuelto una página completa.
                _canLoadMore.value = response.results.size == pageSize
            } else {
                // Si hay un error, deshabilita la opción de cargar más.
                _canLoadMore.value = false
            }

            // Al finalizar, desactiva ambos indicadores de carga.
            _isLoading.value = false
            _isLoadingMore.value = false
        }
    }
}
