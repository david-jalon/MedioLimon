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

    private fun setupListeners() {
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

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

        binding.fabAi.setOnClickListener {
            val intent = Intent(requireContext(), GeneradorAiActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Configura un único observador para reaccionar al objeto de estado de la UI (UiState).
     * Este enfoque centralizado previene race conditions y asegura que la UI sea siempre
     * un reflejo consistente del estado actual de la aplicación.
     */
    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // 1. Gestionar estado de carga principal
            binding.progressBar.isVisible = state.isLoading

            // 2. Gestionar estado de carga de paginación
            binding.loadMoreProgress.isVisible = state.isLoadingMore
            binding.btnLoadMore.text = if (state.isLoadingMore) "" else "Cargar más recetas"
            binding.btnLoadMore.isEnabled = !state.isLoadingMore

            // 3. Gestionar visibilidad del botón "Cargar más"
            // Solo es visible si se puede cargar más Y no hay otra carga principal en curso.
            binding.btnLoadMore.isVisible = state.canLoadMore && !state.isLoading

            // 4. Gestionar la lista de recetas
            recipeAdapter.submitList(state.recipes)
            // La lista solo es visible si no estamos en carga principal y no hay un error que mostrar.
            binding.recipesRecyclerView.isVisible = !state.isLoading && state.error == null

            // 5. Gestionar el estado de error
            binding.tvError.text = state.error
            // El error solo es visible si existe y no estamos en una carga principal.
            binding.tvError.isVisible = state.error != null && !state.isLoading
        }
    }

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
