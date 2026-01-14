package com.mushi.mediolimon.compra

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mushi.mediolimon.data.database.entities.Ingrediente
import com.mushi.mediolimon.databinding.FragmentCompraBinding

/**
 * Fragment que muestra la lista de la compra.
 * Utiliza ViewBinding y un ViewModel para una arquitectura moderna y robusta.
 */
class CompraFragment : Fragment() {

    // Usando la delegación de KTX `viewModels` para obtener el ViewModel.
    private val compraViewModel: CompraViewModel by viewModels()
    
    // ViewBinding para acceder a las vistas de forma segura y concisa.
    private var _binding: FragmentCompraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura el adaptador para el RecyclerView.
        val adapter = IngredienteAdapter(
            onIngredienteClicked = { ingrediente ->
                val updatedIngrediente = ingrediente.copy(comprado = !ingrediente.comprado)
                compraViewModel.update(updatedIngrediente)
            },
            onEliminarClicked = { ingrediente ->
                compraViewModel.delete(ingrediente)
            }
        )

        // Configura el RecyclerView usando el ViewBinding.
        binding.rvIngredientes.adapter = adapter
        binding.rvIngredientes.layoutManager = LinearLayoutManager(requireContext())

        // Observa los cambios en la lista de ingredientes.
        compraViewModel.allIngredientes.observe(viewLifecycleOwner) { ingredientes ->
            ingredientes?.let { 
                adapter.setIngredientes(it)
                // ¡SOLUCIÓN! Activa el modo edición para mostrar los botones de eliminar.
                adapter.setModoEdicion(true)
            }
        }

        // Configura el listener del botón de añadir.
        binding.btnAnadir.setOnClickListener {
            val nombreIngrediente = binding.etNuevoIngrediente.text.toString()
            if (nombreIngrediente.isNotBlank()) {
                val ingrediente = Ingrediente(nombre = nombreIngrediente)
                compraViewModel.insert(ingrediente)
                binding.etNuevoIngrediente.text.clear()
            }
        }
    }

    /**
     * Limpia la referencia al binding para evitar fugas de memoria.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
