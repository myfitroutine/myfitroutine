package com.bestteam.myfitroutine.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Model.TotalNumData
import com.bestteam.myfitroutine.databinding.MealTodayItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealDailyAdapter(val context : Context) : RecyclerView.Adapter<MealDailyAdapter.ViewHolder>(){

    var todayDataSet = ArrayList<TotalNumData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDailyAdapter.ViewHolder {
        val binding = MealTodayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealDailyAdapter.ViewHolder, position: Int) {

        val todayMeal = todayDataSet[position]

        holder.todayTitle.text = todayMeal.title
    }

    override fun getItemCount(): Int {
        return todayDataSet.size
    }

    inner class ViewHolder(binding : MealTodayItemBinding) : RecyclerView.ViewHolder(binding.root){

        var todayTitle = binding.rvTodayMeal

    }
}