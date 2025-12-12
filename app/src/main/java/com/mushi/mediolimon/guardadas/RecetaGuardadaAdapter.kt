package com.mushi.mediolimon.guardadas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mushi.mediolimon.R
import com.mushi.mediolimon.data.database.entities.RecetaGuardada

/**
 * Adapter para el RecyclerView que muestra la lista de recetas guardadas.
 *
 * @param onItemClicked Lambda que se ejecuta cuando se pulsa sobre una receta.
 * @param onDeleteClicked Lambda que se ejecuta cuando se pulsa el botón de eliminar.
 */
class RecetaGuardadaAdapter(
    private val onItemClicked: (RecetaGuardada) -> Unit,
    private val onDeleteClicked: (RecetaGuardada) -> Unit
) : ListAdapter<RecetaGuardada, RecetaGuardadaAdapter.RecetaGuardadaViewHolder>(RecetaGuardadaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaGuardadaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receta_guardada, parent, false)
        return RecetaGuardadaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaGuardadaViewHolder, position: Int) {
        val receta = getItem(position)
        holder.bind(receta)
        holder.itemView.setOnClickListener {
            onItemClicked(receta)
        }
        holder.deleteButton.setOnClickListener {
            onDeleteClicked(receta)
        }
    }

    class RecetaGuardadaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.item_receta_title)
        private val imageView: ImageView = itemView.findViewById(R.id.item_receta_image)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(receta: RecetaGuardada) {
            titleTextView.text = receta.title
            receta.imageUrl?.let {
                Glide.with(itemView.context).load(it).into(imageView)
            } ?: imageView.setImageResource(R.drawable.ic_placeholder)
        }
    }
}

class RecetaGuardadaDiffCallback : DiffUtil.ItemCallback<RecetaGuardada>() {
    override fun areItemsTheSame(oldItem: RecetaGuardada, newItem: RecetaGuardada): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecetaGuardada, newItem: RecetaGuardada): Boolean {
        return oldItem == newItem
    }
}
