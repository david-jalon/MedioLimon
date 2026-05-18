package com.mushi.mediolimon.planificador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mushi.mediolimon.R
import com.mushi.mediolimon.planificador.model.DayPlan
import com.mushi.mediolimon.planificador.model.MealPlan
import java.util.Locale

/**
 * Adaptador actualizado para manejar planes de comida semanales o diarios.
 */
class PlanificadorAdapter : RecyclerView.Adapter<PlanificadorAdapter.DiaPlanViewHolder>() {

    private var dailyPlans = mutableListOf<Pair<String, DayPlan>>()

    class DiaPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayName: TextView = itemView.findViewById(R.id.tv_day_name)
        val meals: TextView = itemView.findViewById(R.id.tv_meals)
        val nutrients: TextView = itemView.findViewById(R.id.tv_nutrients)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaPlanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dia_plan, parent, false)
        return DiaPlanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiaPlanViewHolder, position: Int) {
        val (day, dayPlan) = dailyPlans[position]

        holder.dayName.text = day.replaceFirstChar { it.titlecase(Locale.getDefault()) }

        val mealsText = dayPlan.meals.joinToString(separator = "\n") { meal ->
            "• ${meal.title} (${meal.readyInMinutes} min)"
        }
        holder.meals.text = mealsText
        holder.nutrients.text = "Total: ${dayPlan.nutrients.calories.toInt()} kcal"
    }

    override fun getItemCount() = dailyPlans.size

    /**
     * Procesa el objeto MealPlan para mostrarlo en el RecyclerView.
     */
    fun submitMealPlan(mealPlan: MealPlan) {
        dailyPlans.clear()

        // 1. Caso Formato Semanal
        if (mealPlan.week != null) {
            val daysOrder = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
            daysOrder.forEach { day ->
                mealPlan.week[day]?.let { dayPlan ->
                    dailyPlans.add(Pair(day, dayPlan))
                }
            }
        } 
        // 2. Caso Formato Diario (Si 'week' es nulo pero tenemos 'meals' y 'nutrients' directos)
        else if (mealPlan.meals != null && mealPlan.nutrients != null) {
            val todayPlan = DayPlan(mealPlan.meals, mealPlan.nutrients)
            dailyPlans.add(Pair("Today's Plan", todayPlan))
        }

        notifyDataSetChanged()
    }
}