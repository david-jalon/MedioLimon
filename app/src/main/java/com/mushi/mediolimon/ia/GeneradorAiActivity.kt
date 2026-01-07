package com.mushi.mediolimon.ia

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.databinding.ActivityGeneradorAiBinding
import kotlinx.coroutines.launch

/**
 * Activity dedicada a la generación de recetas mediante la IA de Gemini.
 * El usuario puede introducir hasta 4 ingredientes de forma dinámica.
 */
class GeneradorAiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneradorAiBinding

    private lateinit var ingredientLayouts: List<TextInputLayout>
    private lateinit var ingredientEditTexts: List<TextInputEditText>
    private var visibleIngredientCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneradorAiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeIngredientFields()
        setupActionBar()
        setupListeners()
    }

    private fun initializeIngredientFields() {
        ingredientLayouts = listOf(
            binding.tilIngredientsAi1,
            binding.tilIngredientsAi2,
            binding.tilIngredientsAi3,
            binding.tilIngredientsAi4
        )
        ingredientEditTexts = listOf(
            binding.etIngredientsAi1,
            binding.etIngredientsAi2,
            binding.etIngredientsAi3,
            binding.etIngredientsAi4
        )
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Generador de Recetas con IA"
    }

    private fun setupListeners() {
        binding.btnGenerateAi.setOnClickListener {
            val ingredients = getIngredientsFromFields()
            if (ingredients.isNotBlank()) {
                hideKeyboard()
                generateRecipe(ingredients)
            } else {
                Toast.makeText(this, "Por favor, introduce al menos un ingrediente", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddIngredient.setOnClickListener {
            if (visibleIngredientCount < 4) {
                ingredientLayouts[visibleIngredientCount].visibility = View.VISIBLE
                visibleIngredientCount++

                if (visibleIngredientCount == 4) {
                    binding.btnAddIngredient.visibility = View.GONE
                }
            }
        }
    }

    private fun getIngredientsFromFields(): String {
        return ingredientEditTexts
            .take(visibleIngredientCount)
            .map { it.text.toString().trim() }
            .filter { it.isNotBlank() }
            .joinToString(separator = ", ")
    }

    private fun generateRecipe(ingredients: String) {
        lifecycleScope.launch {
            setLoadingState(true)
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-3-flash-preview",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                // Prompt mejorado para ser más restrictivo con los ingredientes.
                val prompt = "Crea una receta de cocina que utilice únicamente los siguientes ingredientes: $ingredients. " +
                             "Puedes añadir únicamente ingredientes básicos y comunes como aceite, sal, pimienta, especias y agua si son estrictamente necesarios. " +
                             "La receta debe tener un título claro, una lista de ingredientes final y los pasos de preparación bien detallados."

                val response = generativeModel.generateContent(prompt)
                
                // Formatea la respuesta para eliminar caracteres de Markdown antes de mostrarla.
                binding.tvGeneratedRecipe.text = formatGeminiResponse(response.text)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@GeneradorAiActivity, "Error al generar la receta: ${e.message}", Toast.LENGTH_LONG).show()
                binding.tvGeneratedRecipe.text = "No se pudo generar la receta. Revisa la clave de API y la conexión."
            }
            setLoadingState(false)
        }
    }

    /**
     * Limpia el texto de respuesta de Gemini, eliminando los caracteres de formato Markdown.
     * @param rawText El texto en bruto devuelto por la API.
     * @return El texto formateado y limpio.
     */
    private fun formatGeminiResponse(rawText: String?): String {
        return rawText
            ?.replace("## ", "") // Elimina los marcadores de encabezado H2
            ?.replace("#", "")    // Elimina los marcadores de encabezado H1
            ?.replace("**", "")   // Elimina los marcadores de negrita
            ?.replace("* ", "• ") // Reemplaza los asteriscos de las listas por un punto
            ?.trim() ?: "No se pudo generar la receta. Inténtalo de nuevo."
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBarAi.isVisible = isLoading
        binding.btnGenerateAi.isEnabled = !isLoading
        binding.btnAddIngredient.isEnabled = !isLoading
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
