package com.mushi.mediolimon.ia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.mushi.mediolimon.BuildConfig
import kotlinx.coroutines.launch

/**
 * Representa el estado de la UI para la pantalla de generación de recetas con IA.
 */
data class GeneradorAiUiState(
    val isLoading: Boolean = false,
    val generatedRecipe: String? = null,
    val error: String? = null
)

class GeneradorAiViewModel : ViewModel() {

    private val _uiState = MutableLiveData(GeneradorAiUiState())
    val uiState: LiveData<GeneradorAiUiState> = _uiState

    fun generateRecipe(ingredients: String) {
        // Inicia el estado de carga
        _uiState.value = GeneradorAiUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-3-flash-preview",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                val prompt = "Create a recipe that uses only the following ingredients: $ingredients. " +
                        "You can only add basic and common ingredients like oil, salt, pepper, spices, and water if they are strictly necessary. " +
                        "The recipe must have a clear title, a final list of ingredients, and well-detailed preparation steps."

                val response = generativeModel.generateContent(prompt)
                
                if (response.text.isNullOrBlank()) {
                     _uiState.postValue(GeneradorAiUiState(error = "La API no devolvió una receta. Inténtalo de nuevo."))
                } else {
                     val formattedText = formatGeminiResponse(response.text)
                     // Actualiza el estado con la receta generada
                    _uiState.postValue(GeneradorAiUiState(generatedRecipe = formattedText))
                }

            } catch (e: Exception) {
                // En caso de error, se actualiza el estado con un mensaje más detallado.
                val errorMessage = e.message ?: "Unknow error."
                _uiState.postValue(GeneradorAiUiState(error = "Error generating the recipe: $errorMessage"))
            }
        }
    }

    /**
     * Limpia el texto de respuesta de Gemini, eliminando caracteres de formato Markdown.
     */
    private fun formatGeminiResponse(rawText: String?): String {
        return rawText
            ?.replace("## ", "")
            ?.replace("#", "")
            ?.replace("**", "")
            ?.replace("* ", "• ")
            ?.trim() ?: ""
    }
}
