package com.mushi.mediolimon.planificador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // Si no hay un plan de comidas, se genera uno nuevo
        if (viewModel.mealPlan.value == null) {
            generateNewMealPlan()
        }
    }

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
     * Configura los observadores para que la UI reaccione a los cambios de datos del ViewModel.
     * La lógica se ha simplificado para evitar conflictos y tener un único punto de verdad para cada estado.
     */
    private fun setupObservers() {
        // 1. Observa el estado de carga
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.fabRefresh.isEnabled = !isLoading
        }

        // 2. Observa los datos del plan de comidas
        viewModel.mealPlan.observe(viewLifecycleOwner) { mealPlan ->
            // Si hay un plan, se actualiza la lista y se muestra el RecyclerView
            val hasData = mealPlan != null
            if(hasData) {
                planificadorAdapter.submitList(mealPlan!!.week)
            }
            binding.rvPlanificador.isVisible = hasData
        }

        // 3. Observa los errores
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            // Si hay un error, se muestra el mensaje y se oculta el RecyclerView
            val hasError = errorMessage != null
            binding.tvError.isVisible = hasError
            if(hasError) {
                binding.tvError.text = errorMessage
                binding.rvPlanificador.isVisible = false // Asegura que la lista no se muestre si hay un error
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
