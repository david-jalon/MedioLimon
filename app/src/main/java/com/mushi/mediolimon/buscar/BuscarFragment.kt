package com.mushi.mediolimon.buscar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.R
import com.mushi.mediolimon.api.RetrofitClient
import com.mushi.mediolimon.buscar.adapter.RecipeAdapter
import com.mushi.mediolimon.buscar.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragment que muestra una lista de recetas y permite filtrarlas por categoría de dieta.
 */
class BuscarFragment : Fragment() {

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var categorySpinner: Spinner

    // Mapa que asocia el nombre de la categoría con el parámetro de la API.
    private val categories = mapOf(
        "Todas las Dietas" to null,
        "Vegana" to "vegan",
        "Vegetariana" to "vegetarian",
        "Cetogénica (Keto)" to "ketogenic"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout correcto para este fragment.
        return inflater.inflate(R.layout.fragment_buscar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización de las vistas usando la vista del fragment.
        recipesRecyclerView = view.findViewById(R.id.recipes_recycler_view)
        categorySpinner = view.findViewById(R.id.category_spinner)

        // Configuración inicial del RecyclerView y el Spinner.
        setupRecyclerView()
        setupSpinner()

        // Carga inicial de las recetas sin filtro.
        fetchRecipes(diet = null, query = "")
    }

    /**
     * Configura el RecyclerView con su LayoutManager y el adaptador,
     * incluyendo el listener para manejar los clics en cada receta.
     */
    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter { recipe -> onRecipeClicked(recipe) }
        recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipesRecyclerView.adapter = recipeAdapter
    }

    /**
     * Se invoca cuando el usuario hace clic en una receta de la lista.
     * Abre la [RecipeDetailActivity] pasando los datos necesarios.
     * @param recipe La receta en la que se hizo clic.
     */
    private fun onRecipeClicked(recipe: Recipe) {
        val intent = Intent(requireContext(), RecipeDetailActivity::class.java).apply {
            putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id)
            putExtra(RecipeDetailActivity.EXTRA_RECIPE_IMAGE_URL, recipe.image)
        }
        startActivity(intent)
    }

    /**
     * Configura el Spinner con las categorías y define su comportamiento.
     */
    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(), // Contexto correcto.
            android.R.layout.simple_spinner_item,
            categories.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategoryName = parent?.getItemAtPosition(position).toString()
                val dietParameter = categories[selectedCategoryName]
                fetchRecipes(diet = dietParameter, query = "")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Realiza la llamada a la API para buscar recetas.
     * @param diet El filtro de dieta a aplicar (ej: "vegan").
     * @param query El texto a buscar (actualmente no se usa).
     */
    private fun fetchRecipes(diet: String?, query: String?) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.searchRecipes(
                        apiKey = BuildConfig.SPOONACULAR_API_KEY,
                        query = query,
                        diet = diet,
                        type = null
                    )
                }
                recipeAdapter.submitList(response.results)

            } catch (e: Exception) {
                Log.e("API_CALL", "Error al obtener recetas: ${e.message}", e)
                Toast.makeText(requireContext(), "Error de red o cuota API agotada.", Toast.LENGTH_LONG).show()
            }
        }
    }
}