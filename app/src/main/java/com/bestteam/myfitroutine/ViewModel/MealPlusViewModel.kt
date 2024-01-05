package com.bestteam.myfitroutine.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestteam.myfitroutine.Adapter.MealDialogResultAdapter
import com.bestteam.myfitroutine.Adapter.MealDialogSearchAdapter
import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.Dialog.MealPlusDialog
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.retrofit.NetworkClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealPlusViewModel() : ViewModel() {

    private val db = Firebase.firestore
    private var auth : FirebaseAuth? = null

    private val _searchData = MutableLiveData<List<Meal_Adapter_Data>>()
    val searchData: LiveData<List<Meal_Adapter_Data>> get() = _searchData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val networkInterface = NetworkClient.api

    private val _mealDialogType = MutableLiveData<String>()
    val mealDialogType : MutableLiveData<String> get() = _mealDialogType

    private val _resultData = MutableLiveData<List<Meal_Adapter_Data>>()
    val resultData: LiveData<List<Meal_Adapter_Data>> get() = _resultData


    private val _searchPosition = MutableLiveData<Int>()
    val searchPosition : MutableLiveData<Int> get() = _searchPosition

    private val _resultPosition = MutableLiveData<Int>()
    val resultPosition : MutableLiveData<Int> get() = _resultPosition

    fun fetchSearchResults(desc_kor: String) = viewModelScope.launch  {
        networkInterface.getMeal(desc_kor, 1, 100, "", "", Contain.AUTH).enqueue(object :
            Callback<MealData> {
            override fun onResponse(call: Call<MealData>, response: Response<MealData>) {
                if (response.isSuccessful) {
                    val mealItems = response.body()?.mealBody?.mealItems
                    _searchData.value = mealItems?.map { item ->
                        Meal_Adapter_Data(item.DESC_KOR, item.NUTR_CONT1, item.NUTR_CONT2, item.NUTR_CONT3, item.NUTR_CONT4,1)
                    } ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.code()} - ${response.message()}"
                }
            }

            override fun onFailure(call: Call<MealData>, t: Throwable) {
                _errorMessage.value = "Failure: ${t.message}"
            }
        })
    }
    fun mealSearchItemClick() = viewModelScope.launch {

        auth = Firebase.auth

        val searchData = _searchPosition.value?.let { _searchData.value?.get(it) }

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)
        val dateFormat2 = SimpleDateFormat("yyyy-MM-dd , HH:mm:ss", Locale.KOREA).format(currentDate)

        val title = searchData?.title
        val calorie = searchData?.calorie?.toInt()
        val carbohydrate = searchData?.resultCarbohydrate?.toInt()
        val protein = searchData?.resultProtein?.toInt()
        val fat = searchData?.resultFat?.toInt()
        val uid = auth?.currentUser?.uid
        val resultCountNum = searchData?.resultCountNum
        val mealTypeText = _mealDialogType.value

        val mealResult = hashMapOf(
            "date" to dateFormat2,
            "title" to title,
            "calorie" to calorie,
            "resultCarbohydrate" to carbohydrate,
            "resultProtein" to protein,
            "resultFat" to fat,
            "uid" to uid,
            "resultCountNum" to resultCountNum,
        )

        if (title != null) {
            db.collection("$uid").document(dateFormat)
                .collection("$mealTypeText").document(title)
                .set(mealResult)
                .addOnFailureListener {
                    Log.d("mealResult", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w("mealResult", "Error writing document", e)
                }

            //처음 칼로리 저장
            db.collection("$uid").document(dateFormat)
                .collection("originalCal").document(title)
                .set(mealResult)
                .addOnFailureListener {
                    Log.d("mealResult", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w("mealResult", "Error writing document", e)
                }
        }



    }
    fun mealResultSetting() = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)
        val mealTypeText = _mealDialogType.value

        if (mealTypeText != null) {
            db.collection("$uid").document(dateFormat)
                .collection(mealTypeText)
                .orderBy("date",Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val gson = GsonBuilder().create()
                    val resultList = mutableListOf<Meal_Adapter_Data>()

                    for(document in result){
                        val value = gson.toJson(document.data)
                        val resultFormat = gson.fromJson(value, Meal_Adapter_Data::class.java)
                        resultList.add(resultFormat)
                        Log.d("resultSetup","resultData : ${_resultData.value}")
                        Log.d("resultSetup","document ID : ${document.id}, Data: $value")
                    }
                    _resultData.value = resultList
                }
                .addOnFailureListener {
                    Log.d("resultSetupView","resultData : $_resultData")
                }
        }

    }
    fun breakfastResultSetting() = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        db.collection("$uid").document(dateFormat)
            .collection("아침")
            .orderBy("date",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                val resultList = mutableListOf<Meal_Adapter_Data>()

                for(document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, Meal_Adapter_Data::class.java)
                    resultList.add(resultFormat)
                    Log.d("resultSetup","resultData : ${_resultData.value}")
                    Log.d("resultSetup","document ID : ${document.id}, Data: $value")
                }
                _resultData.value = resultList
            }
            .addOnFailureListener {
                Log.d("resultSetupView","resultData : $_resultData")
            }
    }
    fun lunchResultSetting() = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        db.collection("$uid").document(dateFormat)
            .collection("점심")
            .orderBy("date",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                val resultList = mutableListOf<Meal_Adapter_Data>()

                for(document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, Meal_Adapter_Data::class.java)
                    resultList.add(resultFormat)
                    Log.d("resultSetup","resultData : ${_resultData.value}")
                    Log.d("resultSetup","document ID : ${document.id}, Data: $value")
                }
                _resultData.value = resultList
            }
            .addOnFailureListener {
                Log.d("resultSetupView","resultData : $_resultData")
            }
    }
    fun dinnerResultSetting() = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        db.collection("$uid").document(dateFormat)
            .collection("저녁")
            .orderBy("date",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                val resultList = mutableListOf<Meal_Adapter_Data>()

                for(document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, Meal_Adapter_Data::class.java)
                    resultList.add(resultFormat)
                    Log.d("resultSetup","resultData : ${_resultData.value}")
                    Log.d("resultSetup","document ID : ${document.id}, Data: $value")
                }
                _resultData.value = resultList
            }
            .addOnFailureListener {
                Log.d("resultSetupView","resultData : $_resultData")
            }
    }
    fun etcResultSetting() = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        db.collection("$uid").document(dateFormat)
            .collection("그외")
            .orderBy("date",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                val resultList = mutableListOf<Meal_Adapter_Data>()

                for(document in result){
                    val value = gson.toJson(document.data)
                    val resultFormat = gson.fromJson(value, Meal_Adapter_Data::class.java)
                    resultList.add(resultFormat)
                    Log.d("resultSetup","resultData : ${_resultData.value}")
                    Log.d("resultSetup","document ID : ${document.id}, Data: $value")
                }
                _resultData.value = resultList
            }
            .addOnFailureListener {
                Log.d("resultSetupView","resultData : $_resultData")
            }
    }

    fun mealTotalCalorie() = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)
        val mealTypeText = _mealDialogType.value

        val query = mealTypeText?.let { db.collection("$uid").document(dateFormat).collection(it) }

        var calorieSum = 0
        var carbohydrateSum = 0
        var proteinSum = 0
        var fatSum = 0

        query?.get()
            ?.addOnSuccessListener { querySnapshot ->
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
            ?.addOnFailureListener {
                Log.e("totalCalories", "totalCalories fail")
            }
    }

    fun getBreakFastTotalNum(){


    }

    fun mealResultDelete(context: Context) = viewModelScope.launch {
        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealResult =_resultPosition.value?.let { _resultData.value?.get(it) }
        Log.e("_resultPosition", "_resultPosition value: ${_resultPosition.value}")
        val mealTitle = mealResult?.title

        val mealTypeText = _mealDialogType.value

        if (mealTitle != null) {
            db.collection("$uid").document(dateFormat)
                .collection("$mealTypeText").document(mealTitle)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("mealDelete","mealTitle $mealTitle")
                }
                .addOnFailureListener {  e ->
                    Log.w(
                        "mealDelete", "Error deleting document", e
                    )
                }
        }

        db.collection("$uid").document(dateFormat).collection("totalNum").document("$mealTypeText totalNum")
            .delete()
            .addOnSuccessListener {
                Log.d("totalNumDelete","totalNumDelete success")
            }
            .addOnFailureListener {  e ->
                Log.w(
                    "totalNumDelete", "Error deleting document", e
                )
            }

        db.collection("$uid").document(dateFormat).collection("totalNum").document("dailyNum")
            .delete()
            .addOnSuccessListener {
                Log.d("dailyNumDelete","dailyNumDelete success")
            }
            .addOnFailureListener {  e ->
                Log.w(
                    "dailyNumDelete", "Error deleting document", e
                )
            }
    }

    fun mealResultPlus() = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealResult = _resultPosition.value?.let { _resultData.value?.get(it) }
        val mealTitle = mealResult?.title

        val mealTypeText = _mealDialogType.value

        val query = db.collection("$uid").document(dateFormat).collection("originalCal")

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot){

                    val originalCal = document.getLong("calorie")
                    val originalCar = document.getLong("resultCarbohydrate")
                    val originalPro = document.getLong("resultProtein")
                    val originalFat = document.getLong("resultFat")

                    if (mealTitle != null) {
                        db.collection("$uid").document(dateFormat)
                            .collection("$mealTypeText").document(mealTitle)
                            .update(
                                "calorie", originalCal?.let { FieldValue.increment(it) },
                                "resultCarbohydrate", originalCar?.let { FieldValue.increment(it) },
                                "resultProtein", originalPro?.let { FieldValue.increment(it) },
                                "resultFat", originalFat?.let { FieldValue.increment(it) },
                                "resultCountNum",FieldValue.increment(1)
                            )
                    }

                }
            }
    }

    fun mealResultMinus() = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealResult = _resultPosition.value?.let { _resultData.value?.get(it) }
        val mealTitle = mealResult?.title

        val mealTypeText = _mealDialogType.value

        val query = db.collection("$uid").document(dateFormat).collection("originalCal")

        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot){

                    val originalCal = document.getLong("calorie")
                    val originalCar = document.getLong("resultCarbohydrate")
                    val originalPro = document.getLong("resultProtein")
                    val originalFat = document.getLong("resultFat")

                    if (mealTitle != null) {
                        db.collection("$uid").document(dateFormat)
                            .collection("$mealTypeText").document(mealTitle)
                            .update(
                                "calorie", originalCal?.let { FieldValue.increment(-it) },
                                "resultCarbohydrate", originalCar?.let { FieldValue.increment(-it) },
                                "resultProtein", originalPro?.let { FieldValue.increment(-it) },
                                "resultFat", originalFat?.let { FieldValue.increment(-it) },
                                "resultCountNum",FieldValue.increment(-1)
                            )
                    }

                }
            }
    }

    fun btnComplete(context: Context) = viewModelScope.launch {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)


        val query = db.collection("$uid").document(dateFormat).collection("totalNum")

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

                    if (calorieSum != null && carbohydrateSum != null && proteinSum != null && fatSum != null) {

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

                        Toast.makeText(context, "입력이 완료 되었습니다.", Toast.LENGTH_SHORT).show()

                    }
                    Log.d("dailyTotalData", "totalCalories : $dailyCalorieSum")
                }
            }
            .addOnFailureListener {
                Log.e("dailyTotalData", "totalCalories fail : $dailyCalorieSum")
            }
    }


}