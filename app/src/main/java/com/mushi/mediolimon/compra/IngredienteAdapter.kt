package com.mushi.mediolimon.compra

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mushi.mediolimon.R
import com.mushi.mediolimon.data.database.entities.Ingrediente

class IngredienteAdapter(
    private val onIngredienteClicked: (Ingrediente) -> Unit,
    private val onEliminarClicked: (Ingrediente) -> Unit
) : RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder>() {

    private var ingredientes = emptyList<Ingrediente>()
    private var isModoEdicion = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_ingrediente, parent, false)
        return IngredienteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        val currentIngrediente = ingredientes[position]
        holder.tvIngredienteNombre.text = currentIngrediente.nombre
        holder.cbIngrediente.isChecked = currentIngrediente.comprado

        // Aplicar tachado y opacidad al TEXTO si está comprado
        if (currentIngrediente.comprado) {
            holder.tvIngredienteNombre.paintFlags = holder.tvIngredienteNombre.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvIngredienteNombre.alpha = 0.5f
        } else {
            holder.tvIngredienteNombre.paintFlags = holder.tvIngredienteNombre.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvIngredienteNombre.alpha = 1.0f
        }

        holder.btnEliminar.visibility = if (isModoEdicion) View.VISIBLE else View.GONE

        // Acción de toggle (cambiar estado)
        val toggleAction = View.OnClickListener {
            onIngredienteClicked(currentIngrediente)
        }

        // Permitir clic en el checkbox, el texto o toda la tarjeta
        holder.cbIngrediente.setOnClickListener(toggleAction)
        holder.tvIngredienteNombre.setOnClickListener(toggleAction)
        holder.itemView.setOnClickListener(toggleAction)

        holder.btnEliminar.setOnClickListener {
            onEliminarClicked(currentIngrediente)
        }
    }

    override fun getItemCount() = ingredientes.size

    internal fun setIngredientes(ingredientes: List<Ingrediente>) {
        this.ingredientes = ingredientes
        notifyDataSetChanged()
    }

    fun setModoEdicion(activado: Boolean) {
        isModoEdicion = activado
        notifyDataSetChanged()
    }

    class IngredienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbIngrediente: CheckBox = itemView.findViewById(R.id.cbIngrediente)
        val tvIngredienteNombre: TextView = itemView.findViewById(R.id.tvIngredienteNombre)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
    }
}