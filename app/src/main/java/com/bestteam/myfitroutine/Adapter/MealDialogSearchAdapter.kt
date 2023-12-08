package com.bestteam.myfitroutine.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.MealItem
import com.bestteam.myfitroutine.databinding.MealDialogSearchItemBinding

class MealDialogSearchAdapter(val context: Context) : RecyclerView.Adapter<MealDialogSearchAdapter.ViewHolder>() {

    var dataSet = ArrayList<MealItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDialogSearchAdapter.ViewHolder {

        val binding = MealDialogSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MealDialogSearchAdapter.ViewHolder, position: Int) {

        val meal = dataSet[position]

        holder.searchText.text = meal.DESC_KOR
        holder.searchCalorie.text = meal.NUTR_CONT1

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class ViewHolder(binding: MealDialogSearchItemBinding) : RecyclerView.ViewHolder(binding.root){

        var searchText = binding.mealDialogSearchText
        var searchCalorie = binding.mealDialogSearchCal

    }
}