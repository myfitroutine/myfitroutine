package com.bestteam.myfitroutine.Dialog

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bestteam.myfitroutine.Adapter.MealDialogResultAdapter
import com.bestteam.myfitroutine.Adapter.MealDialogSearchAdapter
import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.Model.TotalNumData
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.View.MealFragment
import com.bestteam.myfitroutine.ViewModel.MealPlusViewModel
import com.bestteam.myfitroutine.databinding.MealDialogBinding
import com.bestteam.myfitroutine.retrofit.NetworkClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealPlusDialog() : DialogFragment(), MealDialogResultAdapter.ButtonClick {

    private lateinit var binding : MealDialogBinding
    private lateinit var viewModel : MealPlusViewModel
    private lateinit var searchAdapter : MealDialogSearchAdapter
    private lateinit var resultAdapter : MealDialogResultAdapter
    private lateinit var mealContext : Context
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager

    private var searchData : ArrayList<Meal_Adapter_Data> = ArrayList()
    private var resultData : ArrayList<Meal_Adapter_Data> = ArrayList()
    private var totalNumData = mutableListOf<TotalNumData>()

    private val db = Firebase.firestore
    private var auth : FirebaseAuth? = null

    private val networkInterface = NetworkClient.api

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mealContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = MealDialogBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MealPlusViewModel::class.java)


        // 검색 기능
        searchSetupView()
        setupListeners()

        // clip 클릭시 firestore 데이터 가져오기
        chipClick()

        totalCalories()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        binding.btnDialogCancel.setOnClickListener {
            dismiss()
        }

        binding.mealDialogPlus.setOnClickListener {

            btnPlus()
            dismiss()

            val mealFragment = MealFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentFrame,mealFragment)
            transaction.commit()
        }
    }

    override fun onResume() {
        super.onResume()

        //디바이스 크기 구하기
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params : ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x

        //디바이스 크기의 %로 크기 조정
        params?.width = (deviceWidth * 0.95).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        //다이얼로그 모서리 둥글게 하기
        dialog?.window?.setBackgroundDrawableResource(R.drawable.meal_dialog_shape)


//        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

    }

    private fun searchSetupView(){
        gridLayoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        binding.rvSearch.layoutManager = gridLayoutManager
        searchAdapter = MealDialogSearchAdapter(mealContext,searchData)
        binding.rvSearch.adapter = searchAdapter
        binding.rvSearch.itemAnimator = null

        searchAdapter.itemClick = object : MealDialogSearchAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {

                binding.rvSearch.isGone = true

                auth = Firebase.auth

                val searchData = searchData[position]

                val currentDate = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

                val title = searchData.title
                val calorie = searchData.calorie
                val carbohydrate = searchData.resultCarbohydrate
                val protein = searchData.resultProtein
                val fat = searchData.resultFat
                val uid = auth?.currentUser?.uid
                val resultCountNum = searchData.resultCountNum

                val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
                    "아침"
                } else if (binding.mealDialogTypeLunch.isChecked){
                    "점심"
                } else if (binding.mealDialogTypeDinner.isChecked){
                    "저녁"
                } else "그외"

                val mealResult = hashMapOf(
                    "title" to title,
                    "calorie" to calorie,
                    "resultCarbohydrate" to carbohydrate,
                    "resultProtein" to protein,
                    "resultFat" to fat,
                    "uid" to uid,
                    "resultCountNum" to resultCountNum,
                    "mealType" to mealTypeText,
                )

                db.collection("$uid").document(dateFormat)
                    .collection("$mealTypeText").document(title)
                    .set(mealResult)
                    .addOnFailureListener {
                        Log.d("mealResult", "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("mealResult", "Error writing document", e)
                    }
                searchAdapter.dataSet.clear()
                binding.mealEditText.setText("")
                resultSetupView()

                totalCalories()}
        }
    }

    private fun setupListeners(){
        binding.searchImage.setOnClickListener {
            val breakfast = binding.mealDialogTypeBreakfast
            val lunch = binding.mealDialogTypeLunch
            val dinner = binding.mealDialogTypeDinner
            val etc = binding.mealDialogTypeEtc

            val desc_kor = binding.mealEditText.text.toString()
            if (desc_kor.isNotEmpty()){

                if (!breakfast.isChecked && !lunch.isChecked && !dinner.isChecked && !etc.isChecked){
                    Toast.makeText(mealContext,"분류를 선택해 주세요.",Toast.LENGTH_SHORT).show()
                }
                else {
                    fetchSearchResults(desc_kor)
                    binding.rvSearch.visibility = View.VISIBLE
                }
            }
            else {
                Toast.makeText(mealContext, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                binding.rvSearch.visibility = View.GONE
            }
        }
    }

    private fun fetchSearchResults(desc_kor:String){

        viewModel.fetchSearchResults(desc_kor)
        viewModel.searchData.observe(viewLifecycleOwner) { searchData ->
            this.searchData.clear()
            this.searchData.addAll(searchData)
            searchAdapter.dataSet = this.searchData
            searchAdapter.notifyDataSetChanged()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("fetchSearchResults", errorMessage)
            Toast.makeText(mealContext, "원하는 검색어가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resultSetupView(){
        gridLayoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        binding.rvResult.layoutManager = gridLayoutManager
        resultAdapter = MealDialogResultAdapter(mealContext, this)
        binding.rvResult.adapter = resultAdapter
        binding.rvResult.itemAnimator = null

        resultData.clear()

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        db.collection("$uid").document("$dateFormat")
            .collection("$mealTypeText")
            .get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                for(document in result){
                    val value = gson.toJson(document.data)
                    val resulFormat = gson.fromJson(value, Meal_Adapter_Data::class.java)
                    resultData.add(resulFormat)
                    Log.d("resultSetup","resultData : $resultData")
                    Log.d("resultSetup","document ID : ${document.id}, Data: $value")
                    Log.d("resultSetup", "Size of resultData: ${resultData.size}")
                }
                resultAdapter.resultDataSet = resultData
                resultAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("resultSetupView","resultData : $resultData")
            }
    }

    fun chipClick(){

        val breakfast = binding.mealDialogTypeBreakfast
        val lunch = binding.mealDialogTypeLunch
        val dinner = binding.mealDialogTypeDinner
        val etc = binding.mealDialogTypeEtc

        breakfast.setOnClickListener {
            resultSetupView()
            totalCalories()
        }
        lunch.setOnClickListener {
            resultSetupView()
            totalCalories()
        }
        dinner.setOnClickListener {
            resultSetupView()
            totalCalories()
        }
        etc.setOnClickListener {
            resultSetupView()
            totalCalories()
        }
    }

    fun totalCalories(){

        val totalCal = binding.mealDialogTotalCalNum
        val totalCar = binding.mealDialogTotalCarbohydrateNum
        val totalPro = binding.mealDialogTotalProteinNum
        val totalFat = binding.mealDialogTotalFatNum

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        val query = db.collection("$uid").document(dateFormat).collection(mealTypeText)

        var calorieSum = 0
        var carbohydrateSum = 0
        var proteinSum = 0
        var fatSum = 0

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val calorie = document.getLong("calorie")
                    val carbohydrate = document.getLong("resultCarbohydrate")
                    val protein = document.getLong("resultProtein")
                    val fat = document.getLong("resultFat")

                    if(calorie != null && carbohydrate != null && protein != null && fat != null){

                        calorieSum += calorie.toInt()
                        carbohydrateSum += carbohydrate.toInt()
                        proteinSum += protein.toInt()
                        fatSum += fat.toInt()

                        val totalData = hashMapOf(
                            "calorieSum" to calorieSum,
                            "carbohydrateSum" to carbohydrateSum,
                            "proteinSum" to proteinSum,
                            "fatSum" to fatSum
                        )

                        db.collection("$uid").document(dateFormat).collection("totalNum").document("$mealTypeText totalNum")
                            .set(totalData)

                    }
                    Log.d("totalCalories","totalCalories : $calorieSum")
                }
            }
            .addOnFailureListener {
                Log.e("totalCalories", "totalCalories fail : $totalNumData")
            }

        val mealTypeTotalNum = db.collection("$uid").document(dateFormat).collection("totalNum")

        if (binding.mealDialogTypeBreakfast.isChecked){
            totalCal.text = "0"
            totalCar.text = "0"
            totalPro.text = "0"
            totalFat.text = "0"
            mealTypeTotalNum.document("아침 totalNum")
                .get()
                .addOnSuccessListener {
                    if (it.contains("calorieSum") && it.contains("carbohydrateSum") && it.contains("proteinSum") && it.contains("fatSum")){
                        val mealTypeCal = it.getLong("calorieSum")
                        val mealTypeCar = it.getLong("carbohydrateSum")
                        val mealTypePro = it.getLong("proteinSum")
                        val mealTypeFat = it.getLong("fatSum")

                        totalCal.text = mealTypeCal.toString()
                        totalCar.text = mealTypeCar.toString()
                        totalPro.text = mealTypePro.toString()
                        totalFat.text = mealTypeFat.toString()
                    }
                }
        }
        else if (binding.mealDialogTypeLunch.isChecked){
            totalCal.text = "0"
            totalCar.text = "0"
            totalPro.text = "0"
            totalFat.text = "0"
            mealTypeTotalNum.document("점심 totalNum")
                .get()
                .addOnSuccessListener {
                    if (it.contains("calorieSum") && it.contains("carbohydrateSum") && it.contains("proteinSum") && it.contains("fatSum")){
                        val mealTypeCal = it.getLong("calorieSum")
                        val mealTypeCar = it.getLong("carbohydrateSum")
                        val mealTypePro = it.getLong("proteinSum")
                        val mealTypeFat = it.getLong("fatSum")

                        totalCal.text = mealTypeCal.toString()
                        totalCar.text = mealTypeCar.toString()
                        totalPro.text = mealTypePro.toString()
                        totalFat.text = mealTypeFat.toString()
                    }
                }
        }
        else if (binding.mealDialogTypeDinner.isChecked){
            totalCal.text = "0"
            totalCar.text = "0"
            totalPro.text = "0"
            totalFat.text = "0"
            mealTypeTotalNum.document("저녁 totalNum")
                .get()
                .addOnSuccessListener {
                    if (it.contains("calorieSum") && it.contains("carbohydrateSum") && it.contains("proteinSum") && it.contains("fatSum")){
                        val mealTypeCal = it.getLong("calorieSum")
                        val mealTypeCar = it.getLong("carbohydrateSum")
                        val mealTypePro = it.getLong("proteinSum")
                        val mealTypeFat = it.getLong("fatSum")

                        totalCal.text = mealTypeCal.toString()
                        totalCar.text = mealTypeCar.toString()
                        totalPro.text = mealTypePro.toString()
                        totalFat.text = mealTypeFat.toString()
                    }
                }
        }
        else {
            totalCal.text = "0"
            totalCar.text = "0"
            totalPro.text = "0"
            totalFat.text = "0"
            mealTypeTotalNum.document("그외 totalNum")
                .get()
                .addOnSuccessListener {
                    if (it.contains("calorieSum") && it.contains("carbohydrateSum") && it.contains("proteinSum") && it.contains("fatSum")){
                        val mealTypeCal = it.getLong("calorieSum")
                        val mealTypeCar = it.getLong("carbohydrateSum")
                        val mealTypePro = it.getLong("proteinSum")
                        val mealTypeFat = it.getLong("fatSum")

                        totalCal.text = mealTypeCal.toString()
                        totalCar.text = mealTypeCar.toString()
                        totalPro.text = mealTypePro.toString()
                        totalFat.text = mealTypeFat.toString()
                    }
                }
        }


    }

    //삭제
    override fun resultMealDelete(position: Int) {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        val mealResult = resultData[position]
        val mealTitle = mealResult.title

        db.collection("$uid").document(dateFormat)
            .collection("$mealTypeText").document(mealTitle)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(mealContext, "삭제되었습니다.",Toast.LENGTH_SHORT).show()
                Log.d("mealDelete","mealTitle $mealTitle")
            }
            .addOnFailureListener {  e ->
                Log.w(
                    "mealDelete", "Error deleting document", e
                )
            }

        db.collection("$uid").document(dateFormat).collection("totalNum").document("$mealTypeText totalNum")
            .delete()
            .addOnSuccessListener {
                Log.d("mealDelete","mealTitle success")
            }
            .addOnFailureListener {  e ->
                Log.w(
                    "mealDelete", "Error deleting document", e
                )
            }
        totalCalories()
        resultData.removeAt(position)
        resultAdapter.notifyDataSetChanged()
    }

    //+버튼 클릭
    override fun resultMealEditPlus(position: Int) {
        resultAdapter = MealDialogResultAdapter(mealContext, this)

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        val mealResult = resultData[position]
        val mealTitle = mealResult.title


        networkInterface.getMeal(mealTitle,1, 1, "", "", Contain.AUTH).enqueue(object :Callback<MealData>{
            override fun onResponse(call: Call<MealData>, response: Response<MealData>) {
                if (response.isSuccessful){
                    response.body()?.mealBody?.mealItems?.forEach {

                        val calorie = it.NUTR_CONT1
                        val carbohydrate = it.NUTR_CONT2
                        val protein = it.NUTR_CONT3
                        val fat = it.NUTR_CONT4

                        db.collection("$uid").document(dateFormat)
                            .collection("$mealTypeText").document(mealTitle)
                            .update(
                                "calorie", FieldValue.increment(calorie.toLong()),
                                "resultCarbohydrate", FieldValue.increment(carbohydrate.toLong()),
                                "resultProtein", FieldValue.increment(protein.toLong()),
                                "resultFat", FieldValue.increment(fat.toLong()),
                                "resultCountNum",FieldValue.increment(1)
                            )
                    }
                    resultAdapter.resultDataSet = resultData
                    resultAdapter.notifyDataSetChanged()
                    totalCalories()
                }
            }

            override fun onFailure(call: Call<MealData>, t: Throwable) {
                Log.e("resultMealEditPlus","resultMealEditPlus error")
            }
        })
    }

    //-버튼 클릭
    override fun resultMealEditMinus(position: Int) {

        resultAdapter = MealDialogResultAdapter(mealContext, this)

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        val mealResult = resultData[position]
        val mealTitle = mealResult.title

        networkInterface.getMeal(mealTitle,1, 1, "", "", Contain.AUTH).enqueue(object :Callback<MealData>{
            override fun onResponse(call: Call<MealData>, response: Response<MealData>) {
                if (response.isSuccessful){
                    response.body()?.mealBody?.mealItems?.forEach {
                        val calorie = it.NUTR_CONT1
                        val carbohydrate = it.NUTR_CONT2
                        val protein = it.NUTR_CONT3
                        val fat = it.NUTR_CONT4

                        db.collection("$uid").document(dateFormat)
                            .collection("$mealTypeText").document(mealTitle)
                            .update(
                                "calorie", FieldValue.increment(-calorie.toLong()),
                                "resultCarbohydrate", FieldValue.increment(-carbohydrate.toLong()),
                                "resultProtein", FieldValue.increment(-protein.toLong()),
                                "resultFat", FieldValue.increment(-fat.toLong()),
                                "resultCountNum",FieldValue.increment(-1)
                            )
                        resultAdapter.resultDataSet = resultData
                        resultAdapter.notifyItemChanged(position)
                        totalCalories()
                    }
                }
            }

            override fun onFailure(call: Call<MealData>, t: Throwable) {
                Log.e("resultMealEditPlus","resultMealEditPlus error")
            }

        })
    }

    fun btnPlus(){
        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"


        val query = db.collection("$uid").document(dateFormat).collection("totalNum")
        val daily = db.collection("$uid").document(dateFormat).collection("dailyTotalNum")
//        val dailyTitle = db.collection("$uid").document(dateFormat).collection(mealTypeText)
//
//
//        dailyTitle.get()
//            .addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot){
//                    val title = document.getString("title")
//
//                    if(title != null){
//                        val dailyTitles = hashMapOf(
//                            "mealType" to title
//                        )
//                        daily.document("dailyNum")
//                            .set(dailyTitles)
//                    }
//                }
//            }


        var dailyCalorieSum = 0
        var dailyCarbohydrateSum = 0
        var dailyProteinSum = 0
        var dailyFatSum = 0

        query.get()
            .addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val calorieSum = document.getLong("calorieSum")
                val carbohydrateSum = document.getLong("carbohydrateSum")
                val proteinSum = document.getLong("proteinSum")
                val fatSum = document.getLong("fatSum")

                if(calorieSum != null && carbohydrateSum != null && proteinSum != null && fatSum != null){

                    dailyCalorieSum += calorieSum.toInt()
                    dailyCarbohydrateSum += carbohydrateSum.toInt()
                    dailyProteinSum += proteinSum.toInt()
                    dailyFatSum += fatSum.toInt()

                    val dailyTotalData = hashMapOf(
                        "dailyCalorieSum" to dailyCalorieSum,
                        "dailyCarbohydrateSum" to dailyCarbohydrateSum,
                        "dailyProteinSum" to dailyProteinSum,
                        "dailyFateSum" to dailyFatSum
                    )

                    query.document("dailyNum")
                        .set(dailyTotalData)

                    Toast.makeText(mealContext,"입력이 완료 되었습니다.", Toast.LENGTH_SHORT).show()

                }
                Log.d("dailyTotalData","totalCalories : $dailyCalorieSum")
            }
        }
            .addOnFailureListener {
                Log.e("dailyTotalData", "totalCalories fail : $dailyCalorieSum")
            }
    }
}