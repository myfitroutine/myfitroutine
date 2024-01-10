package com.bestteam.myfitroutine.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Dialog.MealPlusDialog
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.databinding.MealDialogResultItemBinding
import okhttp3.internal.notifyAll

class MealDialogResultAdapter(val context: Context, var listener: MealPlusDialog) : RecyclerView.Adapter<MealDialogResultAdapter.ViewHolder>(){


    private val differCallback = object :DiffUtil.ItemCallback<Meal_Adapter_Data>(){
        override fun areItemsTheSame(oldItem: Meal_Adapter_Data, newItem: Meal_Adapter_Data): Boolean {
            return oldItem.title == newItem.title
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Meal_Adapter_Data, newItem: Meal_Adapter_Data): Boolean {
            return oldItem == newItem
        }

    }

    var resultDataSet = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealDialogResultAdapter.ViewHolder {

        val binding = MealDialogResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MealDialogResultAdapter.ViewHolder, position: Int) {

        val mealResult = resultDataSet.currentList[position]

        holder.resultTitle.text = mealResult.title
        holder.resultCarbohydrate.text = mealResult.resultCarbohydrate.toInt().toString()
        holder.resultProtein.text = mealResult.resultProtein.toInt().toString()
        holder.resultFat.text = mealResult.resultFat.toInt().toString()
        holder.resultCountNum.text = mealResult.resultCountNum.toString()

    }

    override fun getItemCount(): Int {
        return resultDataSet.currentList.size
    }

    inner class ViewHolder(binding: MealDialogResultItemBinding) : RecyclerView.ViewHolder(binding.root){

        var resultTitle = binding.rvDialogMenu
        var resultCarbohydrate = binding.rvDialogCarbohydrateNum
        var resultProtein = binding.rvDialogProteinNum
        var resultFat = binding.rvDialogFatNum

        private var resultDelete = binding.rvDialogResultDelete
        private var btnPlus = binding.rvDialogPlus
        private var btnMinus = binding.rvDialogMinus
        var resultCountNum = binding.rvDialogCountNum


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