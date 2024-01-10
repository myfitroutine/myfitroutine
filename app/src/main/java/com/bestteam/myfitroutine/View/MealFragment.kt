package com.bestteam.myfitroutine.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bestteam.myfitroutine.Adapter.MealDailyAdapter
import com.bestteam.myfitroutine.Dialog.MealPlusDialog
import com.bestteam.myfitroutine.Model.TotalNumData
import com.bestteam.myfitroutine.databinding.FragmentMealBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.lang.StringBuilder
import java.text.DecimalFormat
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

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }

        getTodayMeal()
        getBeforeOneDayMeal()
        getBeforeTwoDayMeal()

        setCycleChart()

        return binding.root
    }

    override fun onResume() {
        super.onResume()


    }
    @SuppressLint("SetTextI18n")
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
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    a += docId
                    binding.rvTodayBreakfast.text = a.dropLast(3)
                }
            }

        totalCalQuery.document("아침 totalNum").get()
            .addOnSuccessListener {
                val cal = it.getLong("calorieSum")
                binding.rvTodayBreakfastCalorieNum.text = cal.toString()
                if(cal == null || cal == 0.toLong()) {
                    binding.constraintDailyBreakfast.visibility = View.GONE
                }
                else binding.constraintDailyBreakfast.visibility = View.VISIBLE
            }


        //점심
        mealTypeQuery.collection("점심").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var b = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    b += docId
                    binding.rvTodayLunch.text = b.dropLast(3)
                }
            }

        totalCalQuery.document("점심 totalNum").get()
            .addOnSuccessListener {
                val cal = it.getLong("calorieSum")
                binding.rvTodayLunchCalorieNum.text = cal.toString()
                if(cal == null || cal == 0.toLong()) {
                    binding.constraintDailyLunch.visibility = View.GONE
                }
                else binding.constraintDailyLunch.visibility = View.VISIBLE
            }

        //저녁
        mealTypeQuery.collection("저녁").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var c = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    c += docId
                    binding.rvTodayDinner.text = c.dropLast(3)
                }
            }

        totalCalQuery.document("저녁 totalNum").get()
            .addOnSuccessListener {
                val cal = it.getLong("calorieSum")
                binding.rvTodayDinnerCalorieNum.text = cal.toString()
                if(cal == null || cal == 0.toLong()) {
                    binding.constraintDailyDinner.visibility = View.GONE
                }
                else binding.constraintDailyDinner.visibility = View.VISIBLE
            }

        //그외
        mealTypeQuery.collection("그외").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var d = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    d += docId

                    binding.rvTodayEtc.text = d.dropLast(3)
                }
            }

        totalCalQuery.document("그외 totalNum").get()
            .addOnSuccessListener {
                val cal = it.getLong("calorieSum")
                binding.rvTodayEtcCalorieNum.text = cal.toString()
                if(cal == null || cal == 0.toLong()) {
                    binding.constraintDailyEtc.visibility = View.GONE
                }
                else binding.constraintDailyEtc.visibility = View.VISIBLE
            }

        totalCalQuery.document("dailyNum").get()
            .addOnSuccessListener {
                val decimalFormat = DecimalFormat("#,###")

                val totalCal = it.getLong("dailyCalorieSum")
                val totalCar = it.getLong("dailyCarbohydrateSum")
                val totalPro = it.getLong("dailyProteinSum")
                val totalFat = it.getLong("dailyFateSum")

                if (totalCal != null){
                    binding.totalCalorieNum.text = decimalFormat.format(totalCal)
                } else binding.totalCalorieNum.text = "0 Kcal"
                if (totalCar != null){
                    binding.mealGraph1.text = decimalFormat.format(totalCar)+" Kcal"
                } else binding.mealGraph1.text = "0 Kcal"
                if (totalPro != null){
                    binding.mealGraph2.text = decimalFormat.format(totalPro)+" Kcal"
                } else binding.mealGraph2.text = "0 Kcal"
                if (totalFat != null){
                    binding.mealGraph3.text = decimalFormat.format(totalFat)+" Kcal"
                } else binding.mealGraph3.text = "0 Kcal"

            }
    }

    private fun getBeforeOneDayMeal(){
        todayMealData.clear()

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val beforeOneDay = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(beforeOneDay)

        val mealTypeQuery = db.collection("$uid").document(dateFormat)

        binding.date.text = dateFormat

        //아침
        mealTypeQuery.collection("아침").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var a = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    a += docId
                    binding.rvBreakfast.text = a.dropLast(3)
                }
            }


        //점심
        mealTypeQuery.collection("점심").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var b = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    b += docId
                    binding.rvLunch.text = b.dropLast(3)
                }
            }

        //저녁
        mealTypeQuery.collection("저녁").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var c = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    c += docId
                    binding.rvDinner.text = c.dropLast(3)
                }
            }
    }

    private fun getBeforeTwoDayMeal(){
        todayMealData.clear()

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -2)
        val beforeTwoDay = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(beforeTwoDay)

        val mealTypeQuery = db.collection("$uid").document(dateFormat)

        binding.date2.text = dateFormat

        //아침
        mealTypeQuery.collection("아침").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var a = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    a += docId
                    binding.rvBreakfast.text = a.dropLast(3)
                }
            }


        //점심
        mealTypeQuery.collection("점심").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var b = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    b += docId
                    binding.rvLunch.text = b.dropLast(3)
                }
            }

        //저녁
        mealTypeQuery.collection("저녁").get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                var c = ""
                for (document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, TotalNumData::class.java)
                    val docId = "${document.id} & "
                    todayMealData.add(resultFormat)
                    c += docId
                    binding.rvDinner.text = c.dropLast(3)
                }
            }
    }



    private fun setCycleChart(){
        binding.mealChart.setUsePercentValues(true)

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val query = db.collection("$uid").document(dateFormat).collection("totalNum").document("dailyNum")

        query.get()
            .addOnSuccessListener {

                if (it.exists()){

                    val dataSet = it.data

                    val cal = dataSet?.get("dailyCalorieSum") as Long
                    val car = dataSet?.get("dailyCarbohydrateSum") as Long
                    val pro = dataSet?.get("dailyProteinSum") as Long
                    val fat = dataSet?.get("dailyFateSum") as Long

                    if (cal.toInt() != 0){

                        binding.mealChart.visibility = View.VISIBLE

                        val entries = ArrayList<PieEntry>()
                        if (car.toFloat() != 0f) {
                            entries.add(PieEntry(car.toFloat(), "탄수화물"))
                        }
                        if(pro.toFloat() != 0f) {
                            entries.add(PieEntry(pro.toFloat(), "단백질"))
                        }
                        if (fat.toFloat() != 0f) {
                            entries.add(PieEntry(fat.toFloat(), "지방"))
                        }


                        val colorItem = ArrayList<Int>()
                        for (c in ColorTemplate.VORDIPLOM_COLORS) colorItem.add(c)
                        for (c in ColorTemplate.JOYFUL_COLORS) colorItem.add(c)
                        for (c in ColorTemplate.COLORFUL_COLORS) colorItem.add(c)
                        colorItem.add(ColorTemplate.getHoloBlue())

                        val pieDataSet = PieDataSet(entries,"")
                        pieDataSet.apply {
                            colors = colorItem
                            valueTextColor = Color.BLACK
                            valueTextSize = 18f
                            valueFormatter = PercentFormatter(binding.mealChart) // 숫자뒤에 % 넣기
                        }

                        val pieData = PieData(pieDataSet)
                        binding.mealChart.apply {
                            data = pieData
                            description.isEnabled = false
                            isRotationEnabled = false
                            centerText = "칼로리"
                            setEntryLabelColor(Color.BLACK)
                            setCenterTextSize(20f)
                            animateY(1400, Easing.EaseInOutQuad)
                            animate()
                        }

                        binding.mealChart.legend.apply {
                            verticalAlignment = Legend.LegendVerticalAlignment.TOP
                            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                            orientation = Legend.LegendOrientation.VERTICAL
                            setDrawInside(false)
                            xEntrySpace = 10f
                            yEntrySpace = 10f
                            textSize = 12f
                        }

                    }
                }
            }
    }


}