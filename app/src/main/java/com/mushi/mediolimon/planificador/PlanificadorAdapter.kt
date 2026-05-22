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

class PlanificadorAdapter : RecyclerView.Adapter<PlanificadorAdapter.DiaPlanViewHolder>() {

    private var dailyPlans = mutableListOf<Pair<String, DayPlan>>()

    class DiaPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayName: TextView = itemView.findViewById(R.id.tv_day_name)
        val breakfastName: TextView = itemView.findViewById(R.id.tv_breakfast_name)
        val lunchName: TextView = itemView.findViewById(R.id.tv_lunch_name)
        val dinnerName: TextView = itemView.findViewById(R.id.tv_dinner_name)
        val nutrients: TextView = itemView.findViewById(R.id.tv_nutrients)
        
        // Contenedores para ocultar si no hay comida
        val breakfastLayout: View = itemView.findViewById(R.id.ll_breakfast)
        val lunchLayout: View = itemView.findViewById(R.id.ll_lunch)
        val dinnerLayout: View = itemView.findViewById(R.id.ll_dinner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaPlanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dia_plan, parent, false)
        return DiaPlanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiaPlanViewHolder, position: Int) {
        val (day, dayPlan) = dailyPlans[position]

        holder.dayName.text = day.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        
        // Mapeo inteligente de comidas (Spoonacular suele devolver 3 comidas: Breakfast, Lunch, Dinner)
        val meals = dayPlan.meals
        
        if (meals.size >= 1) {
            holder.breakfastLayout.visibility = View.VISIBLE
            holder.breakfastName.text = meals[0].title
        } else {
            holder.breakfastLayout.visibility = View.GONE
        }

        if (meals.size >= 2) {
            holder.lunchLayout.visibility = View.VISIBLE
            holder.lunchName.text = meals[1].title
        } else {
            holder.lunchLayout.visibility = View.GONE
        }

        if (meals.size >= 3) {
            holder.dinnerLayout.visibility = View.VISIBLE
            holder.dinnerName.text = meals[2].title
        } else {
            holder.dinnerLayout.visibility = View.GONE
        }

        holder.nutrients.text = "Total: ${dayPlan.nutrients.calories.toInt()} kcal"
    }

    override fun getItemCount() = dailyPlans.size

    fun submitMealPlan(mealPlan: MealPlan) {
        dailyPlans.clear()
        if (mealPlan.week != null) {
            val daysOrder = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
            daysOrder.forEach { day ->
                mealPlan.week[day]?.let { dayPlan ->
                    dailyPlans.add(Pair(day, dayPlan))
                }
            }
        } else if (mealPlan.meals != null && mealPlan.nutrients != null) {
            val todayPlan = DayPlan(mealPlan.meals, mealPlan.nutrients)
            dailyPlans.add(Pair("Today's Plan", todayPlan))
        }
        notifyDataSetChanged()
    }
}