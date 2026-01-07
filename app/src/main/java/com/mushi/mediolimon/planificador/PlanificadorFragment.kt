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
 * Fragment que actúa como la vista para la funcionalidad del Planificador de Comidas.
 * Su responsabilidad es mostrar el plan de comidas y delegar las acciones del usuario (como refrescar el plan)
 * al [MealPlanViewModel].
 */
class PlanificadorFragment : Fragment() {

    // Backing property para el ViewBinding. Se usa para evitar fugas de memoria.
    private var _binding: FragmentPlanificadorBinding? = null
    // Propiedad de solo lectura para acceder al binding de forma segura.
    private val binding get() = _binding!!

    // Inicializa el ViewModel usando la delegación de KTX `viewModels`.
    // Esto asegura que el ViewModel sobrevive a cambios de configuración.
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

        setupListeners()
        setupObservers()

        // Si no hay un plan de comidas cargado (ej: la primera vez que se abre la app),
        // se solicita uno nuevo para que la pantalla no aparezca vacía.
        if (viewModel.mealPlan.value == null) {
            generateNewMealPlan()
        }
    }

    /**
     * Configura los listeners para los elementos interactivos de la UI.
     */
    private fun setupListeners() {
        // Asigna la acción de generar un nuevo plan al botón flotante.
        binding.fabRefresh.setOnClickListener {
            generateNewMealPlan()
        }
    }

    /**
     * Configura los observadores para los LiveData del ViewModel.
     * La UI reacciona a los cambios en los datos, pero no los modifica directamente.
     */
    private fun setupObservers() {
        // Observa el estado de carga. Muestra u oculta la barra de progreso central.
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            // Para una mejor experiencia, se desactiva el botón de refrescar mientras ya se está cargando.
            binding.fabRefresh.isEnabled = !isLoading
        }

        // Observa los datos del plan de comidas.
        viewModel.mealPlan.observe(viewLifecycleOwner) { mealPlan ->
            if (mealPlan != null) {
                // Si el plan no es nulo, se formatea y se muestra en el TextView.
                binding.tvMealPlan.text = formatMealPlan(mealPlan)
            } else {
                // Si el plan es nulo, puede ser porque la carga inicial aún no ha terminado
                // o porque ha ocurrido un error. Se muestra un Toast solo si no se está cargando.
                viewModel.isLoading.value?.let { isLoading ->
                    if (!isLoading) {
                        Toast.makeText(context, "Error al generar el plan de comidas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Función de ayuda que centraliza la llamada al ViewModel para generar un nuevo plan.
     */
    private fun generateNewMealPlan() {
        viewModel.generateMealPlan(
            apiKey = BuildConfig.SPOONACULAR_API_KEY,
            targetCalories = 2000, // Se puede cambiar o hacer configurable por el usuario.
            diet = "vegetarian"      // Se puede cambiar o hacer configurable por el usuario.
        )
    }

    /**
     * Convierte el objeto MealPlan en un String formateado y legible para ser mostrado en un TextView.
     * En una app más compleja, esto se podría reemplazar por un RecyclerView con diferentes vistas.
     * @param mealPlan El objeto de datos que contiene el plan semanal.
     * @return Un String con todo el plan formateado.
     */
    private fun formatMealPlan(mealPlan: MealPlan): String {
        val formattedPlan = StringBuilder()
        // Se define una lista explícita con el orden de los días para asegurar una presentación consistente.
        val daysOfWeek = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")

        daysOfWeek.forEach { day ->
            // Busca el plan para el día actual en el mapa de la API.
            mealPlan.week[day]?.let { dayPlan ->
                // Capitaliza la primera letra del día para una mejor presentación.
                formattedPlan.append(day.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }).append("\n")
                formattedPlan.append("---------------------\n")

                // Itera sobre cada comida del día.
                dayPlan.meals.forEach { meal ->
                    formattedPlan.append("  • ${meal.title} (${meal.readyInMinutes} min)\n")
                }

                // Añade el total de calorías del día.
                val nutrients = dayPlan.nutrients
                formattedPlan.append("  Total: ${nutrients.calories.toInt()} kcal\n\n")
            }
        }
        return formattedPlan.toString()
    }

    /**
     * Se llama cuando la vista del fragment va a ser destruida.
     * Es fundamental limpiar la referencia al binding para evitar fugas de memoria (memory leaks).
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
