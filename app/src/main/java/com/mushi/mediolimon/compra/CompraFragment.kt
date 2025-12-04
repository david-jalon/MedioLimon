package com.mushi.mediolimon.compra

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mushi.mediolimon.R
import com.mushi.mediolimon.data.database.entities.Ingrediente

/**
 * Fragment que muestra la lista de la compra.
 *
 * Permite al usuario ver, añadir, marcar como comprados y eliminar ingredientes.
 * Este Fragment utiliza un [CompraViewModel] para interactuar con la base de datos y un
 * [IngredienteAdapter] para mostrar los datos en un [RecyclerView].
 */
class CompraFragment : Fragment() {

    private lateinit var compraViewModel: CompraViewModel
    private lateinit var etNuevoIngrediente: EditText
    private lateinit var btnAnadir: Button

    /**
     * Infla el layout del Fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragment
        return inflater.inflate(R.layout.fragment_compra, container, false)
    }

    /**
     * Se llama después de que la vista del Fragment haya sido creada.
     * Aquí es donde se configuran las vistas, el ViewModel, el RecyclerView y los listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa el ViewModel.
        compraViewModel = ViewModelProvider(this).get(CompraViewModel::class.java)

        // Obtiene las referencias a las vistas del layout.
        etNuevoIngrediente = view.findViewById(R.id.etNuevoIngrediente)
        btnAnadir = view.findViewById(R.id.btnAnadir)

        // Configura el adaptador para el RecyclerView.
        val adapter = IngredienteAdapter(
            // Lambda para manejar el clic en un ingrediente (marcar/desmarcar como comprado).
            onIngredienteClicked = { ingrediente ->
                val updatedIngrediente = ingrediente.copy(comprado = !ingrediente.comprado)
                compraViewModel.update(updatedIngrediente)
            },
            // Lambda para manejar el clic en el botón de eliminar.
            onEliminarClicked = { ingrediente ->
                compraViewModel.delete(ingrediente)
            }
        )

        // Configura el RecyclerView.
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvIngredientes)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observa los cambios en la lista de ingredientes del ViewModel.
        // Cuando los datos cambian, se actualiza el adaptador.
        compraViewModel.allIngredientes.observe(viewLifecycleOwner) { ingredientes ->
            ingredientes?.let { adapter.setIngredientes(it) }
        }

        // Configura el listener para el botón de añadir un nuevo ingrediente.
        btnAnadir.setOnClickListener {
            val nombreIngrediente = etNuevoIngrediente.text.toString()
            if (nombreIngrediente.isNotBlank()) {
                val ingrediente = Ingrediente(nombre = nombreIngrediente)
                compraViewModel.insert(ingrediente)
                etNuevoIngrediente.text.clear() // Limpia el campo de texto.
            }
        }
    }
}