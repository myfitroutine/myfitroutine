package com.bestteam.myfitroutine.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Dialog.MealPlusDialog
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.databinding.MealDialogResultItemBinding

class MealDialogResultAdapter(val context : Context, var listener:ButtonClick) : RecyclerView.Adapter<MealDialogResultAdapter.ViewHolder>(){

    var resultDataSet = ArrayList<Meal_Adapter_Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDialogResultAdapter.ViewHolder {

        val binding = MealDialogResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MealDialogResultAdapter.ViewHolder, position: Int) {

        val mealResult = resultDataSet[position]

        holder.resultTitle.text = mealResult.title
        holder.resultCarbohydrate.text = "${mealResult.resultCarbohydrate}"
        holder.resultProtein.text = mealResult.resultProtein.toString()
        holder.resultFat.text = mealResult.resultFat.toString()
        holder.resultCountNum.text = mealResult.resultCountNum.toString()

    }

    override fun getItemCount(): Int {
        return resultDataSet.size
    }

    inner class ViewHolder(bindig: MealDialogResultItemBinding) : RecyclerView.ViewHolder(bindig.root){

        var resultTitle = bindig.rvDialogMenu
        var resultCarbohydrate = bindig.rvDialogCarbohydrateNum
        var resultProtein = bindig.rvDialogProteinNum
        var resultFat = bindig.rvDialogFatNum

        var resultDelete = bindig.rvDialogResultDelete
        var btnPlus = bindig.rvDialogPlus
        var btnMinus = bindig.rvDialogMinus
        var resultCountNum = bindig.rvDialogCountNum


        init {
            resultDelete.setOnClickListener {
                listener.resultMealDelete(position)
            }
            btnPlus.setOnClickListener {
                listener.resultMealEditPlus(position)
            }
            btnMinus.setOnClickListener {
                listener.resultMealEditMinus(position)
            }
        }

    }

    interface ButtonClick{

        fun resultMealDelete(position: Int)

        fun resultMealEditPlus(position: Int)

        fun resultMealEditMinus(position: Int)

    }


}