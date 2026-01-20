package com.mushi.mediolimon.planificador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mushi.mediolimon.R
import com.mushi.mediolimon.planificador.model.DayPlan
import java.util.Locale

/**
 * Adaptador para el RecyclerView que muestra el plan de comidas semanal.
 * Cada elemento de la lista es un día del plan.
 */
class PlanificadorAdapter : RecyclerView.Adapter<PlanificadorAdapter.DiaPlanViewHolder>() {

    // Lista que contiene los pares de (día, plan) para mostrar.
    private var dailyPlans = emptyList<Pair<String, DayPlan>>()

    /**
     * ViewHolder que contiene las referencias a las vistas de una sola tarjeta de día (item_dia_plan.xml).
     */
    class DiaPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayName: TextView = itemView.findViewById(R.id.tv_day_name)
        val meals: TextView = itemView.findViewById(R.id.tv_meals)
        val nutrients: TextView = itemView.findViewById(R.id.tv_nutrients)
    }

    /**
     * Crea un nuevo ViewHolder inflando el layout de la tarjeta del día.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaPlanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dia_plan, parent, false)
        return DiaPlanViewHolder(itemView)
    }

    /**
     * Vincula los datos de un día específico con las vistas del ViewHolder.
     */
    override fun onBindViewHolder(holder: DiaPlanViewHolder, position: Int) {
        val (day, dayPlan) = dailyPlans[position]

        // Asigna el nombre del día, capitalizando la primera letra.
        holder.dayName.text = day.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        // Construye y asigna la lista de comidas.
        val mealsText = dayPlan.meals.joinToString(separator = "\n") { meal ->
            "• ${meal.title} (${meal.readyInMinutes} min)"
        }
        holder.meals.text = mealsText

        // Asigna el resumen de nutrientes.
        holder.nutrients.text = "Total: ${dayPlan.nutrients.calories.toInt()} kcal"
    }

    /**
     * Devuelve el número total de días en el plan.
     */
    override fun getItemCount() = dailyPlans.size

    /**
     * Actualiza la lista de planes diarios y notifica al RecyclerView para que se redibuje.
     * @param newPlan El mapa que viene de la API con los datos del plan semanal.
     */
    fun submitList(newPlan: Map<String, DayPlan>) {
        // Convierte el mapa en una lista ordenada para una presentación consistente.
        val daysOrder = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        dailyPlans = daysOrder.mapNotNull { day ->
            newPlan[day]?.let { dayPlan -> Pair(day, dayPlan) }
        }
        notifyDataSetChanged()
    }
}