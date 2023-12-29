package com.bestteam.myfitroutine.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.databinding.MealDialogSearchItemBinding
class MealDialogSearchAdapter(val context: Context, val dataset : ArrayList<Meal_Adapter_Data> ) : RecyclerView.Adapter<MealDialogSearchAdapter.ViewHolder>() {

    var dataSet = ArrayList<Meal_Adapter_Data>()

    interface ItemClick{
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDialogSearchAdapter.ViewHolder {

        val binding = MealDialogSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MealDialogSearchAdapter.ViewHolder, position: Int) {

        val meal = dataSet[position]

        holder.searchText.text = meal.title
        holder.searchCalorie.text = meal.calorie.toInt().toString()
        holder.searchLayout.setOnClickListener {
            itemClick?.onClick(it,position)
        }

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class ViewHolder(binding: MealDialogSearchItemBinding) : RecyclerView.ViewHolder(binding.root) {

        var searchText = binding.mealDialogSearchText
        var searchCalorie = binding.mealDialogSearchCal
        var searchLayout = binding.searchItemLayout
    }
}