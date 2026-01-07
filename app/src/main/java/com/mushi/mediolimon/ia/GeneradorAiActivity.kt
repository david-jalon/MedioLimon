package com.mushi.mediolimon.ia

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.databinding.ActivityGeneradorAiBinding
import kotlinx.coroutines.launch

/**
 * Activity dedicada a la generación de recetas mediante la IA de Gemini.
 */
class GeneradorAiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneradorAiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla el layout usando ViewBinding y lo establece como la vista de la actividad.
        binding = ActivityGeneradorAiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupListeners()
    }

    /**
     * Configura la barra de acción (ActionBar) con un título y un botón de "Atrás".
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Generador de Recetas con IA"
    }

    /**
     * Configura el listener para el botón de generar receta.
     */
    private fun setupListeners() {
        binding.btnGenerateAi.setOnClickListener {
            val ingredients = binding.etIngredientsAi.text.toString()
            if (ingredients.isNotBlank()) {
                hideKeyboard()
                generateRecipe(ingredients)
            } else {
                Toast.makeText(this, "Por favor, introduce al menos un ingrediente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Inicia la corrutina para llamar a la API de Gemini y generar la receta.
     * @param ingredients Los ingredientes introducidos por el usuario.
     */
    private fun generateRecipe(ingredients: String) {
        // Se utiliza lifecycleScope para lanzar una corrutina segura ligada al ciclo de vida de la Activity.
        lifecycleScope.launch {
            // Muestra el progreso y deshabilita el botón.
            setLoadingState(true)

            try {
                // 1. Inicializa el modelo generativo de Gemini.
                val generativeModel = GenerativeModel(
                    // Este es el modelo estándar y gratuito. Si esto falla, el problema está en la configuración de la API Key.
                    modelName = "gemini-3-flash-preview",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                // 2. Crea el prompt que se enviará a la IA.
                val prompt = "Crea una receta de cocina que utilice los siguientes ingredientes: $ingredients. " +
                             "La receta debe tener un título claro, una lista de ingredientes (incluyendo los proporcionados y otros que estimes necesarios) " +
                             "y los pasos de preparación bien detallados."

                // 3. Llama a la API para generar el contenido.
                val response = generativeModel.generateContent(prompt)

                // 4. Muestra la respuesta en el TextView.
                binding.tvGeneratedRecipe.text = response.text

            } catch (e: Exception) {
                // Si algo va mal (ej: clave de API incorrecta, sin conexión, API no habilitada), muestra un error.
                e.printStackTrace()
                Toast.makeText(this@GeneradorAiActivity, "Error al generar la receta: ${e.message}", Toast.LENGTH_LONG).show()
                binding.tvGeneratedRecipe.text = "No se pudo generar la receta. Revisa la clave de API y la conexión."
            }

            // Oculta el progreso y habilita el botón de nuevo.
            setLoadingState(false)
        }
    }

    /**
     * Gestiona la visibilidad de la barra de progreso y el estado del botón.
     */
    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBarAi.isVisible = isLoading
        binding.btnGenerateAi.isEnabled = !isLoading
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
