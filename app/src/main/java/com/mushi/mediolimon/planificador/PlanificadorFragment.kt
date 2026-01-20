package com.mushi.mediolimon.planificador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.databinding.FragmentPlanificadorBinding

/**
 * Fragment que muestra un plan de comidas semanal en un RecyclerView.
 */
class PlanificadorFragment : Fragment() {

    private var _binding: FragmentPlanificadorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MealPlanViewModel by viewModels()
    private lateinit var planificadorAdapter: PlanificadorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanificadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        setupObservers()

        if (viewModel.mealPlan.value == null) {
            generateNewMealPlan()
        }
    }

    /**
     * Configura el RecyclerView con su adaptador y layout manager.
     */
    private fun setupRecyclerView() {
        planificadorAdapter = PlanificadorAdapter()
        binding.rvPlanificador.apply {
            adapter = planificadorAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.fabRefresh.setOnClickListener {
            generateNewMealPlan()
        }
    }

    /**
     * Configura los observadores para que la UI reaccione a los cambios de datos.
     */
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.fabRefresh.isEnabled = !isLoading
        }

        viewModel.mealPlan.observe(viewLifecycleOwner) { mealPlan ->
            if (mealPlan != null) {
                // Envía los datos del plan al adaptador para que los muestre.
                planificadorAdapter.submitList(mealPlan.week)
            } else {
                viewModel.isLoading.value?.let {
                    if (!it) {
                        Toast.makeText(context, "Error al generar el plan de comidas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun generateNewMealPlan() {
        viewModel.generateMealPlan(
            apiKey = BuildConfig.SPOONACULAR_API_KEY,
            targetCalories = 2000,
            diet = "vegetarian"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
