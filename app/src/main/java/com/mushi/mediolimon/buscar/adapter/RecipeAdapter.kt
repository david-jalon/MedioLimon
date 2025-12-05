package com.mushi.mediolimon.buscar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mushi.mediolimon.R
import com.mushi.mediolimon.buscar.model.Recipe

/**
 * Adaptador para el RecyclerView que muestra la lista de recetas.
 * Hereda de ListAdapter para gestionar eficientemente las actualizaciones de la lista.
 *
 * @param onItemClicked Una función lambda que se invoca cuando el usuario hace clic en una receta.
 */
class RecipeAdapter(private val onItemClicked: (Recipe) -> Unit) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    /**
     * ViewHolder que representa una única fila (un solo ítem) en la lista de recetas.
     */
    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.recipe_title)
        private val imageView: ImageView = view.findViewById(R.id.recipe_image)

        /**
         * Vincula los datos de una receta específica a las vistas del ViewHolder.
         * @param recipe El objeto Recipe que contiene los datos a mostrar.
         */
        fun bind(recipe: Recipe) {
            titleTextView.text = recipe.title

            Glide.with(itemView.context)
                .load(recipe.image)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)
        holder.itemView.setOnClickListener { onItemClicked(recipe) }
    }

    /**
     * Callback para que DiffUtil calcule las diferencias entre dos listas de recetas.
     */
    class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}