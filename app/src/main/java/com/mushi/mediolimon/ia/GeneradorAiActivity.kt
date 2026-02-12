package com.mushi.mediolimon.ia

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mushi.mediolimon.databinding.ActivityGeneradorAiBinding

class GeneradorAiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneradorAiBinding
    private val viewModel: GeneradorAiViewModel by viewModels()

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
        setupObservers()
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
        supportActionBar?.title = "AI Recipe Generator"
    }

    private fun setupListeners() {
        binding.btnGenerateAi.setOnClickListener {
            val ingredients = getIngredientsFromFields()
            if (ingredients.isNotBlank()) {
                hideKeyboard()
                viewModel.generateRecipe(ingredients)
            } else {
                Toast.makeText(this, "Please enter at least one ingredient", Toast.LENGTH_SHORT).show()
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

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            // Gestionar estado de carga
            binding.progressBarAi.isVisible = state.isLoading
            binding.btnGenerateAi.isEnabled = !state.isLoading
            binding.btnAddIngredient.isEnabled = !state.isLoading

            // Mostrar receta generada o error
            val recipeText = state.generatedRecipe ?: state.error
            binding.tvGeneratedRecipe.text = recipeText

            // La vista de texto solo es visible si tiene contenido (receta o error)
            binding.tvGeneratedRecipe.isVisible = recipeText != null
        }
    }

    private fun getIngredientsFromFields(): String {
        return ingredientEditTexts
            .take(visibleIngredientCount)
            .map { it.text.toString().trim() }
            .filter { it.isNotBlank() }
            .joinToString(separator = ", ")
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
