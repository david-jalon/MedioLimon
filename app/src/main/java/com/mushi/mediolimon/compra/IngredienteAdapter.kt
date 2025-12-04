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
 *
 * Se encarga de vincular los datos de la lista de [Ingrediente] con las vistas de cada elemento
 * en el RecyclerView (`item_ingrediente.xml`). También maneja las interacciones del usuario,
 * como marcar un ingrediente como comprado o eliminarlo.
 *
 * @param onIngredienteClicked Una función lambda que se invoca cuando se hace clic en el CheckBox de un ingrediente.
 * @param onEliminarClicked Una función lambda que se invoca cuando se hace clic en el botón de eliminar de un ingrediente.
 */
class IngredienteAdapter(
    private val onIngredienteClicked: (Ingrediente) -> Unit,
    private val onEliminarClicked: (Ingrediente) -> Unit
) : RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder>() {

    private var ingredientes = emptyList<Ingrediente>()

    /**
     * Crea y devuelve un [IngredienteViewHolder] inflando el layout del elemento.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_ingrediente, parent, false)
        return IngredienteViewHolder(itemView)
    }

    /**
     * Vincula los datos de un [Ingrediente] específico con un [IngredienteViewHolder].
     */
    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        val currentIngrediente = ingredientes[position]
        holder.cbIngrediente.text = currentIngrediente.nombre
        holder.cbIngrediente.isChecked = currentIngrediente.comprado

        holder.cbIngrediente.setOnClickListener {
            onIngredienteClicked(currentIngrediente)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminarClicked(currentIngrediente)
        }
    }

    /**
     * Devuelve el número total de ingredientes en la lista.
     */
    override fun getItemCount() = ingredientes.size

    /**
     * Actualiza la lista de ingredientes del adaptador y notifica al RecyclerView para que se redibuje.
     *
     * @param ingredientes La nueva lista de [Ingrediente] a mostrar.
     */
    internal fun setIngredientes(ingredientes: List<Ingrediente>) {
        this.ingredientes = ingredientes
        notifyDataSetChanged()
    }

    /**
     * ViewHolder para un elemento de la lista de ingredientes. Mantiene las referencias a las vistas
     * de la UI para un solo elemento (un CheckBox y un botón de eliminar).
     */
    class IngredienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbIngrediente: CheckBox = itemView.findViewById(R.id.cbIngrediente)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
    }
}