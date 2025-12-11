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
import com.mushi.mediolimon.BuildConfig
import com.mushi.mediolimon.R
import com.mushi.mediolimon.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity que muestra los detalles de una receta específica, incluyendo
 * su imagen, título e instrucciones de preparación.
 */
class RecipeDetailActivity : AppCompatActivity() {

    // Objeto compañero para definir constantes públicas.
    companion object {
        // Clave para pasar el ID de la receta a través de un Intent.
        const val EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID"
        // Clave para pasar la URL de la imagen de la receta.
        const val EXTRA_RECIPE_IMAGE_URL = "EXTRA_RECIPE_IMAGE_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        // 1. Recuperar los datos pasados desde el Intent.
        val recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1)
        val imageUrl = intent.getStringExtra(EXTRA_RECIPE_IMAGE_URL)

        // 2. Comprobar si se recibió un ID válido. Si no, cerrar la actividad.
        if (recipeId == -1) {
            Toast.makeText(this, "Error: No se encontró la receta", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad para evitar un crash.
            return   // Detiene la ejecución del método.
        }

        // 3. Cargar la imagen superior usando la librería Glide.
        val imageView = findViewById<ImageView>(R.id.recipe_image_detail)
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl) // URL de la imagen.
                .placeholder(R.drawable.ic_placeholder) // Imagen de carga.
                .error(R.drawable.ic_error) // Imagen en caso de error.
                .into(imageView) // ImageView de destino.
        }

        // 4. Iniciar la obtención de los detalles de la receta desde la API.
        fetchRecipeDetails(recipeId)
    }

    /**
     * Realiza una llamada a la API de Spoonacular para obtener las instrucciones
     * y el título de una receta específica usando su ID.
     * @param recipeId El ID de la receta a buscar.
     */
    private fun fetchRecipeDetails(recipeId: Int) {
        // Usamos una corrutina para realizar la llamada de red en un hilo secundario.
        lifecycleScope.launch {
            try {
                // Cambiamos al hilo de I/O (Input/Output) para la llamada de red.
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getRecipeInformation(
                        id = recipeId,
                        apiKey = BuildConfig.SPOONACULAR_API_KEY
                    )
                }

                // Una vez obtenidos los datos, actualizamos la UI en el hilo principal.
                val titleTextView = findViewById<TextView>(R.id.recipe_title_detail)
                val ingredientsTextView = findViewById<TextView>(R.id.recipe_ingredients)
                val instructionsTextView = findViewById<TextView>(R.id.recipe_instructions)

                titleTextView.text = response.title

                // Concatenamos los ingredientes en un solo texto.
                val ingredientsText = response.extendedIngredients.joinToString("\n") { "- ${it.original}" }
                ingredientsTextView.text = ingredientsText

                if (response.instructions != null) {
                    instructionsTextView.text = Html.fromHtml(response.instructions, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    instructionsTextView.text = "No hay instrucciones disponibles."
                }

                // El texto de las instrucciones puede contener etiquetas HTML.
                // Usamos Html.fromHtml para formatearlo correctamente.
                instructionsTextView.text = Html.fromHtml(response.instructions, Html.FROM_HTML_MODE_COMPACT)

            } catch (e: Exception) {
                // Si algo sale mal (problema de red, API key, etc.), lo capturamos aquí.
                Log.e("API_CALL_DETAIL", "Error al obtener detalles: ${e.message}", e)
                Toast.makeText(this@RecipeDetailActivity, "Error al cargar instrucciones: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
