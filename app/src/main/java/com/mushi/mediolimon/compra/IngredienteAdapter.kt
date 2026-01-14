package com.mushi.mediolimon.compra

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.mushi.mediolimon.R
import com.mushi.mediolimon.data.database.entities.Ingrediente

/**
 * Adaptador para el [RecyclerView] que muestra la lista de ingredientes.
 */
class IngredienteAdapter(
    private val onIngredienteClicked: (Ingrediente) -> Unit,
    private val onEliminarClicked: (Ingrediente) -> Unit
) : RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder>() {

    private var ingredientes = emptyList<Ingrediente>()
    private var isModoEdicion = false // Flag para controlar la visibilidad de los botones de eliminar.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_ingrediente, parent, false)
        return IngredienteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        val currentIngrediente = ingredientes[position]
        holder.cbIngrediente.text = currentIngrediente.nombre
        holder.cbIngrediente.isChecked = currentIngrediente.comprado

        // Muestra u oculta el botón de eliminar según el modo de edición.
        holder.btnEliminar.visibility = if (isModoEdicion) View.VISIBLE else View.GONE

        holder.cbIngrediente.setOnClickListener {
            onIngredienteClicked(currentIngrediente)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminarClicked(currentIngrediente)
        }
    }

    override fun getItemCount() = ingredientes.size

    /**
     * Actualiza la lista de ingredientes del adaptador.
     */
    internal fun setIngredientes(ingredientes: List<Ingrediente>) {
        this.ingredientes = ingredientes
        notifyDataSetChanged()
    }

    /**
     * Activa o desactiva el modo de edición, que muestra los botones de eliminar.
     */
    fun setModoEdicion(activado: Boolean) {
        isModoEdicion = activado
        notifyDataSetChanged() // Notifica para que el RecyclerView se redibuje y muestre/oculte los botones.
    }

    /**
     * ViewHolder para un elemento de la lista de ingredientes.
     */
    class IngredienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbIngrediente: CheckBox = itemView.findViewById(R.id.cbIngrediente)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
    }
}