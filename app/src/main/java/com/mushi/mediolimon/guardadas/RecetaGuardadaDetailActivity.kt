package com.mushi.mediolimon.guardadas

import android.app.Application
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mushi.mediolimon.R
import com.mushi.mediolimon.data.database.AppDatabase
import com.mushi.mediolimon.data.database.entities.Ingrediente
import com.mushi.mediolimon.data.database.entities.RecetaGuardada
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecetaGuardadaDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID"
    }

    private val viewModel: RecetaGuardadaDetailViewModel by viewModels {
        val recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1)
        RecetaGuardadaDetailViewModelFactory(application, recipeId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receta_guardada_detail)

        viewModel.receta.observe(this) { receta ->
            receta?.let { setupUI(it) }
        }
    }

    private fun setupUI(receta: RecetaGuardada) {
        val titleTextView = findViewById<TextView>(R.id.recipe_title_detail)
        val ingredientsTextView = findViewById<TextView>(R.id.recipe_ingredients)
        val instructionsTextView = findViewById<TextView>(R.id.recipe_instructions)
        val imageView = findViewById<ImageView>(R.id.recipe_image_detail)

        titleTextView.text = receta.title
        receta.imageUrl?.let {
            Glide.with(this).load(it).into(imageView)
        }

        val ingredientsText = receta.extendedIngredients?.joinToString("\n") { "- ${it.original}" }
        ingredientsTextView.text = ingredientsText

        receta.instructions?.let {
            instructionsTextView.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
        } ?: run {
            instructionsTextView.text = "No hay instrucciones disponibles."
        }

        val fab = findViewById<FloatingActionButton>(R.id.add_to_shopping_list_fab)
        fab.setOnClickListener {
            receta.extendedIngredients?.let { ingredients ->
                if (ingredients.isNotEmpty()) {
                    lifecycleScope.launch {
                        val ingredientesDb = ingredients.map { Ingrediente(nombre = it.original) }
                        withContext(Dispatchers.IO) {
                            AppDatabase.getDatabase(applicationContext).IngredienteDao().insertAll(ingredientesDb)
                        }
                        Toast.makeText(this@RecetaGuardadaDetailActivity, "Ingredientes añadidos a la lista", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RecetaGuardadaDetailActivity, "No hay ingredientes para añadir", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

class RecetaGuardadaDetailViewModel(application: Application, recipeId: Int) : AndroidViewModel(application) {
    val receta = AppDatabase.getDatabase(getApplication()).recetaGuardadaDao().getRecetaById(recipeId)
}

class RecetaGuardadaDetailViewModelFactory(private val application: Application, private val recipeId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecetaGuardadaDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecetaGuardadaDetailViewModel(application, recipeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
