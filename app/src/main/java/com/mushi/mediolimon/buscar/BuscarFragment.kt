package com.mushi.mediolimon.buscar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mushi.mediolimon.buscar.adapter.RecipeAdapter
import com.mushi.mediolimon.buscar.model.Recipe
import com.mushi.mediolimon.databinding.FragmentBuscarBinding
import com.mushi.mediolimon.ia.GeneradorAiActivity

/**
 * Fragment que actúa como la vista para la funcionalidad de búsqueda de recetas.
 * Su responsabilidad es únicamente mostrar los datos y delegar todas las acciones del usuario al [BuscarViewModel].
 * Sigue el patrón de diseño MVVM y utiliza ViewBinding para interactuar con el layout.
 */
class BuscarFragment : Fragment() {

    // Backing property para el ViewBinding. Es nullable para poder limpiarlo en onDestroyView.
    private var _binding: FragmentBuscarBinding? = null
    // Propiedad no nullable para acceder al binding de forma segura después de onCreateView.
    // El `!!` asegura que si se accede al binding cuando no está disponible, la app fallará (lo cual es bueno para detectar errores).
    private val binding get() = _binding!!

    // Inicializa el ViewModel usando la delegación de KTX `viewModels`. 
    // Esto asegura que el ViewModel sobrevive a cambios de configuración (como rotar la pantalla).
    private val viewModel: BuscarViewModel by viewModels()
    
    private lateinit var recipeAdapter: RecipeAdapter

    /**
     * Se llama para crear la vista del fragment. Aquí se infla el layout usando ViewBinding.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Infla el layout y asigna la instancia a _binding.
        _binding = FragmentBuscarBinding.inflate(inflater, container, false)
        // Devuelve la vista raíz del layout inflado.
        return binding.root
    }

    /**
     * Se llama justo después de que la vista del fragment ha sido creada.
     * Es el lugar ideal para configurar la UI (RecyclerView, Listeners, Observers).
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Se centraliza la configuración en métodos separados para mayor claridad.
        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    /**
     * Inicializa el RecyclerView, su LayoutManager y el adaptador.
     */
    private fun setupRecyclerView() {
        // Se crea el adaptador y se le pasa una lambda para manejar los clics en los elementos.
        recipeAdapter = RecipeAdapter { recipe -> onRecipeClicked(recipe) }
        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerView.adapter = recipeAdapter
    }

    /**
     * Configura los listeners para los elementos interactivos de la UI (botones, campo de texto).
     */
    private fun setupListeners() {
        // Cuando se pulsa el botón "Buscar", se llama a la función de búsqueda.
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        // Añade funcionalidad al teclado: permite buscar al pulsar la tecla "Intro" o "Buscar".
        binding.etIngredient.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true // Indica que el evento ha sido consumido.
            } else {
                false // Deja que el sistema maneje otros eventos.
            }
        }

        // Cuando se pulsa "Cargar más", se notifica al ViewModel.
        binding.btnLoadMore.setOnClickListener {
            viewModel.loadMoreRecipes()
        }

        // Cuando se pulsa el botón de IA, se abre la nueva actividad.
        binding.fabAi.setOnClickListener {
            val intent = Intent(requireContext(), GeneradorAiActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Configura los observadores para los LiveData del ViewModel.
     * Esta es la clave de la arquitectura reactiva: la UI reacciona a los cambios de datos, no los modifica directamente.
     */
    private fun setupObservers() {
        // Observa la lista de recetas. Cada vez que cambia, se actualiza el adaptador del RecyclerView.
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.submitList(recipes)
        }

        // Observa el estado de carga principal. Muestra u oculta la barra de progreso central.
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            // Es importante ocultar la lista durante la carga inicial para no mostrar datos antiguos.
            binding.recipesRecyclerView.isVisible = !isLoading
        }

        // Observa el estado de carga de la paginación.
        viewModel.isLoadingMore.observe(viewLifecycleOwner) { isLoadingMore ->
            binding.loadMoreProgress.isVisible = isLoadingMore
            // Truco visual: oculta el texto del botón para que solo se vea la barra de progreso interior.
            binding.btnLoadMore.text = if (isLoadingMore) "" else "Cargar más recetas"
            binding.btnLoadMore.isEnabled = !isLoadingMore // Desactiva el botón mientras carga.
        }

        // Observa si la paginación está disponible.
        viewModel.canLoadMore.observe(viewLifecycleOwner) { canLoadMore ->
            // El botón "Cargar más" solo es visible si hay más resultados y no hay una carga principal en curso.
            binding.btnLoadMore.isVisible = canLoadMore && viewModel.isLoading.value == false
        }
    }

    /**
     * Helper que recoge el texto, oculta el teclado y notifica al ViewModel para que inicie la búsqueda.
     */
    private fun performSearch() {
        hideKeyboard()
        val ingredient = binding.etIngredient.text.toString().trim()
        viewModel.searchByIngredient(ingredient)
    }

    /**
     * Navega a la pantalla de detalle cuando se hace clic en una receta.
     */
    private fun onRecipeClicked(recipe: Recipe) {
        val intent = Intent(requireContext(), RecipeDetailActivity::class.java).apply {
            putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id)
            putExtra(RecipeDetailActivity.EXTRA_RECIPE_IMAGE_URL, recipe.image)
        }
        startActivity(intent)
    }

    /**
     * Oculta el teclado virtual. Mejora la experiencia de usuario después de una búsqueda.
     */
    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    /**
     * Se llama cuando la vista del fragment va a ser destruida.
     * Es CRÍTICO limpiar la referencia al binding aquí para evitar fugas de memoria (memory leaks).
     * Si no se hiciera, el binding mantendría una referencia a la vista destruida, impidiendo que sea liberada por el Garbage Collector.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
