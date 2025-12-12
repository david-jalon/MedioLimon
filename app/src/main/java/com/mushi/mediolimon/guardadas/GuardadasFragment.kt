package com.mushi.mediolimon.guardadas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mushi.mediolimon.R

class GuardadasFragment : Fragment() {

    private val viewModel: GuardadasViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_guardadas, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_guardadas)

        // Inicializamos el adapter con los dos ClickListeners.
        val adapter = RecetaGuardadaAdapter(
            onItemClicked = { receta ->
                // Abrir la pantalla de detalle al pulsar en la receta.
                val intent = Intent(requireContext(), RecetaGuardadaDetailActivity::class.java).apply {
                    putExtra(RecetaGuardadaDetailActivity.EXTRA_RECIPE_ID, receta.id)
                }
                startActivity(intent)
            },
            onDeleteClicked = { receta ->
                // Pedir al ViewModel que elimine la receta.
                viewModel.delete(receta)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observamos los cambios en la base de datos y actualizamos la lista.
        viewModel.recetasGuardadas.observe(viewLifecycleOwner) { recetas ->
            adapter.submitList(recetas)
        }

        return view
    }
}
