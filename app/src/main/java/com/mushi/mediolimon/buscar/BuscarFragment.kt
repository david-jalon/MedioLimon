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

/**
 * Fragment que muestra recetas aleatorias y permite buscar por ingrediente.
 * Sigue el patrón MVVM usando un ViewModel y ViewBinding.
 */
class BuscarFragment : Fragment() {

    private var _binding: FragmentBuscarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BuscarViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuscarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter { recipe -> onRecipeClicked(recipe) }
        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerView.adapter = recipeAdapter
    }

    /**
     * Configura los listeners para el botón de búsqueda y el campo de texto.
     */
    private fun setupListeners() {
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        // Permite buscar al pulsar "Intro" en el teclado.
        binding.etIngredient.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.btnLoadMore.setOnClickListener {
            viewModel.loadMoreRecipes()
        }
    }

    /**
     * Configura los observadores para los LiveData del ViewModel y actualiza la UI.
     */
    private fun setupObservers() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.submitList(recipes)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            // Oculta el RecyclerView durante la carga inicial para evitar mostrar datos antiguos.
            binding.recipesRecyclerView.isVisible = !isLoading
        }

        viewModel.isLoadingMore.observe(viewLifecycleOwner) { isLoadingMore ->
            binding.loadMoreProgress.isVisible = isLoadingMore
            binding.btnLoadMore.text = if (isLoadingMore) "" else "Cargar más recetas"
            binding.btnLoadMore.isEnabled = !isLoadingMore
        }

        viewModel.canLoadMore.observe(viewLifecycleOwner) { canLoadMore ->
            // El botón solo es visible si se puede cargar más y no hay una carga principal en curso.
            binding.btnLoadMore.isVisible = canLoadMore && viewModel.isLoading.value == false
        }
    }

    /**
     * Oculta el teclado y llama al ViewModel para iniciar la búsqueda.
     */
    private fun performSearch() {
        hideKeyboard()
        val ingredient = binding.etIngredient.text.toString().trim()
        viewModel.searchByIngredient(ingredient)
    }

    private fun onRecipeClicked(recipe: Recipe) {
        val intent = Intent(requireContext(), RecipeDetailActivity::class.java).apply {
            putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id)
            putExtra(RecipeDetailActivity.EXTRA_RECIPE_IMAGE_URL, recipe.image)
        }
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
