package com.bestteam.myfitroutine.View

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bestteam.myfitroutine.Adapter.MealDailyAdapter
import com.bestteam.myfitroutine.Dialog.MealPlusDialog
import com.bestteam.myfitroutine.Model.TotalNumData
import com.bestteam.myfitroutine.databinding.FragmentMealBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealFragment : Fragment() {

    private lateinit var binding : FragmentMealBinding
    private lateinit var mealContext : Context
    private lateinit var dailyAdapter : MealDailyAdapter
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager

    private var todayMealData : ArrayList<TotalNumData> = ArrayList()
    private var documentId = mutableListOf<String>()

    private val db = Firebase.firestore
    private var auth : FirebaseAuth? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mealContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentMealBinding.inflate(inflater, container, false)

        binding.plusMeal.setOnClickListener {
            val dialog = MealPlusDialog()
            dialog.show(childFragmentManager,"MealPlusDialog")
        }

        getTodayMeal()

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }
    private fun getTodayMeal(){
        todayMealData.clear()

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val totalCalQuery = db.collection("$uid").document(dateFormat).collection("totalNum")
        val mealTypeQuery = db.collection("$uid").document(dateFormat)

        //아침
        mealTypeQuery.collection("아침").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var a = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} , "
                    todayMealData.add(resultFormat)
                    a += docId
                    binding.rvTodayBreakfast.text = a
                }
            }

        totalCalQuery.document("아침 totalNum").get()
            .addOnSuccessListener {
                val cal = it.getLong("calorieSum")
                binding.rvTodayBreakfastCalorieNum.text = cal.toString()
                if(cal != null) {
                    binding.constraintDailyBreakfast.visibility = View.VISIBLE
                }
                else binding.constraintDailyBreakfast.visibility = View.GONE
            }


        //점심
        mealTypeQuery.collection("점심").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var b = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} , "
                    todayMealData.add(resultFormat)
                    b += docId
                    binding.rvTodayLunch.text = b
                }
            }

        totalCalQuery.document("점심 totalNum").get()
            .addOnSuccessListener {
                val cal = it.getLong("calorieSum")
                binding.rvTodayLunchCalorieNum.text = cal.toString()
                if(cal != null) {
                    binding.constraintDailyLunch.visibility = View.VISIBLE
                }
                else binding.constraintDailyLunch.visibility = View.GONE
            }

        //저녁
        mealTypeQuery.collection("저녁").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var c = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} , "
                    todayMealData.add(resultFormat)
                    c += docId
                    binding.rvTodayDinner.text = c
                }
            }

        totalCalQuery.document("저녁 totalNum").get()
            .addOnSuccessListener {
                val cal = it.getLong("calorieSum")
                binding.rvTodayDinnerCalorieNum.text = cal.toString()
                if(cal != null) {
                    binding.constraintDailyDinner.visibility = View.VISIBLE
                }
                else binding.constraintDailyDinner.visibility = View.GONE
            }

        //그외
        mealTypeQuery.collection("그외").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var d = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} , "
                    todayMealData.add(resultFormat)
                    d += docId
                    binding.rvTodayEtc.text = d
                }
            }

        totalCalQuery.document("그외 totalNum").get()
            .addOnSuccessListener {
                val cal = it.getLong("calorieSum")
                binding.rvTodayEtcCalorieNum.text = cal.toString()
                if(cal != null) {
                    binding.constraintDailyEtc.visibility = View.VISIBLE
                }
                else binding.constraintDailyEtc.visibility = View.GONE
            }

        totalCalQuery.document("dailyNum").get()
            .addOnSuccessListener {
                val totalCal = it.getLong("dailyCalorieSum")
                val totalCar = it.getLong("dailyCarbohydrateSum")
                val totalPro = it.getLong("dailyProteinSum")
                val totalFat = it.getLong("dailyFateSum")

                binding.totalCalorieNum.text = totalCal.toString()
                binding.mealGraph1.text = totalCar.toString()
                binding.mealGraph2.text = totalPro.toString()
                binding.mealGraph3.text = totalFat.toString()
            }
    }


}