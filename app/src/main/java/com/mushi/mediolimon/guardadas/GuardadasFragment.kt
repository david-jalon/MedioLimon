package com.mushi.mediolimon.guardadas

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mushi.mediolimon.R
import com.mushi.mediolimon.databinding.FragmentGuardadasBinding

class GuardadasFragment : Fragment() {

    private var _binding: FragmentGuardadasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GuardadasViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuardadasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupTitle()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupTitle() {
        val fullText = "Your saved recipes"
        val wordToStyle = "recipes"
        val spannable = SpannableString(fullText)
        
        val startIndex = fullText.indexOf(wordToStyle)
        if (startIndex != -1) {
            val endIndex = fullText.length
            
            // Color Verde (Mismo que en BuscarFragment)
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary)),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            // Cursiva (Italic)
            spannable.setSpan(
                StyleSpan(Typeface.ITALIC),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        binding.tvTitleGuardadas.text = spannable
    }

    private fun setupRecyclerView() {
        val adapter = RecetaGuardadaAdapter(
            onItemClicked = { receta ->
                val intent = Intent(requireContext(), RecetaGuardadaDetailActivity::class.java).apply {
                    putExtra(RecetaGuardadaDetailActivity.EXTRA_RECIPE_ID, receta.id)
                }
                startActivity(intent)
            },
            onDeleteClicked = { receta ->
                viewModel.delete(receta)
            }
        )

        binding.recyclerViewGuardadas.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.recetasGuardadas.observe(viewLifecycleOwner) { recetas ->
            (binding.recyclerViewGuardadas.adapter as? RecetaGuardadaAdapter)?.submitList(recetas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}