package com.mushi.mediolimon.planificador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.databinding.FragmentPlanificadorBinding
import com.mushi.mediolimon.planificador.model.MealPlan
import java.util.Locale

/**
 * Fragment que muestra un plan de comidas semanal y permite generar uno nuevo.
 */
class PlanificadorFragment : Fragment() {

    private var _binding: FragmentPlanificadorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MealPlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanificadorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura el listener del botón flotante para generar un nuevo plan.
        binding.fabRefresh.setOnClickListener {
            generateNewMealPlan()
        }

        // Configura los observadores de los LiveData.
        setupObservers()

        // Si no hay un plan de comidas cargado, genera uno inicial.
        if (viewModel.mealPlan.value == null) {
            generateNewMealPlan()
        }
    }

    /**
     * Configura los observadores para la UI (estado de carga y datos del plan).
     */
    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            // Desactiva el botón mientras se está cargando para evitar clics múltiples.
            binding.fabRefresh.isEnabled = !isLoading
        }

        viewModel.mealPlan.observe(viewLifecycleOwner) { mealPlan ->
            if (mealPlan != null) {
                // Formatea y muestra el plan de comidas.
                binding.tvMealPlan.text = formatMealPlan(mealPlan)
            } else {
                // Muestra un error si el plan es nulo y no se está cargando.
                viewModel.isLoading.value?.let { isLoading ->
                    if (!isLoading) {
                        Toast.makeText(context, "Error al generar el plan de comidas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Llama al ViewModel para solicitar un nuevo plan de comidas a la API.
     */
    private fun generateNewMealPlan() {
        viewModel.generateMealPlan(
            apiKey = BuildConfig.SPOONACULAR_API_KEY,
            targetCalories = 2000,
            diet = "vegetarian"
        )
    }

    /**
     * Convierte el objeto MealPlan en un String formateado y legible.
     */
    private fun formatMealPlan(mealPlan: MealPlan): String {
        val formattedPlan = StringBuilder()
        // Define el orden de los días para una presentación consistente.
        val daysOfWeek = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")

        daysOfWeek.forEach { day ->
            mealPlan.week[day]?.let { dayPlan ->
                formattedPlan.append(day.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }).append("\n")
                formattedPlan.append("---------------------\n")

                dayPlan.meals.forEach { meal ->
                    formattedPlan.append("  • ${meal.title} (${meal.readyInMinutes} min)\n")
                }

                val nutrients = dayPlan.nutrients
                formattedPlan.append("  Total: ${nutrients.calories.toInt()} kcal\n\n")
            }
        }
        return formattedPlan.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
