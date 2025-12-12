package com.mushi.mediolimon.buscar

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.R
import com.mushi.mediolimon.api.RetrofitClient
import com.mushi.mediolimon.buscar.model.ExtendedIngredient
import com.mushi.mediolimon.data.database.AppDatabase
import com.mushi.mediolimon.data.database.dao.IngredienteDao
import com.mushi.mediolimon.data.database.dao.RecetaGuardadaDao
import com.mushi.mediolimon.data.database.entities.Ingrediente
import com.mushi.mediolimon.data.database.entities.RecetaGuardada
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var ingredienteDao: IngredienteDao
    private lateinit var recetaGuardadaDao: RecetaGuardadaDao
    private var extendedIngredients: List<ExtendedIngredient> = emptyList()
    private var recipeTitle: String = ""
    private var recipeInstructions: String? = ""

    companion object {
        const val EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID"
        const val EXTRA_RECIPE_IMAGE_URL = "EXTRA_RECIPE_IMAGE_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        ingredienteDao = AppDatabase.getDatabase(this).IngredienteDao()
        recetaGuardadaDao = AppDatabase.getDatabase(this).recetaGuardadaDao()

        val recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1)
        val imageUrl = intent.getStringExtra(EXTRA_RECIPE_IMAGE_URL)

        if (recipeId == -1) {
            Toast.makeText(this, "Error: No se encontró la receta", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val imageView = findViewById<ImageView>(R.id.recipe_image_detail)
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_error).into(imageView)
        }

        fetchRecipeDetails(recipeId)

        val fab = findViewById<FloatingActionButton>(R.id.add_to_shopping_list_fab)
        fab.setOnClickListener {
            if (extendedIngredients.isNotEmpty()) {
                lifecycleScope.launch {
                    val ingredientes = extendedIngredients.map { Ingrediente(nombre = it.original) }
                    withContext(Dispatchers.IO) {
                        ingredienteDao.insertAll(ingredientes)
                    }
                    Toast.makeText(this@RecipeDetailActivity, "Ingredientes añadidos a la lista", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@RecipeDetailActivity, "No hay ingredientes para añadir", Toast.LENGTH_SHORT).show()
            }
        }

        val fabFav = findViewById<FloatingActionButton>(R.id.add_to_favorites)
        fabFav.setOnClickListener {
            lifecycleScope.launch {
                val receta = RecetaGuardada(
                    id = recipeId,
                    title = recipeTitle,
                    imageUrl = imageUrl,
                    instructions = recipeInstructions,
                    extendedIngredients = extendedIngredients
                )
                withContext(Dispatchers.IO) {
                    recetaGuardadaDao.insert(receta)
                }
                Toast.makeText(this@RecipeDetailActivity, "Receta añadida a favoritos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchRecipeDetails(recipeId: Int) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getRecipeInformation(id = recipeId, apiKey = BuildConfig.SPOONACULAR_API_KEY)
                }

                extendedIngredients = response.extendedIngredients
                recipeTitle = response.title
                recipeInstructions = response.instructions

                val titleTextView = findViewById<TextView>(R.id.recipe_title_detail)
                val ingredientsTextView = findViewById<TextView>(R.id.recipe_ingredients)
                val instructionsTextView = findViewById<TextView>(R.id.recipe_instructions)

                titleTextView.text = response.title
                val ingredientsText = response.extendedIngredients.joinToString("\n") { "- ${it.original}" }
                ingredientsTextView.text = ingredientsText

                if (response.instructions != null) {
                    instructionsTextView.text = Html.fromHtml(response.instructions, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    instructionsTextView.text = "No hay instrucciones disponibles."
                }

            } catch (e: Exception) {
                Log.e("API_CALL_DETAIL", "Error al obtener detalles: ${e.message}", e)
                Toast.makeText(this@RecipeDetailActivity, "Error al cargar instrucciones: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
