package com.bestteam.myfitroutine.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.Repository.MealRepository
import com.bestteam.myfitroutine.retrofit.NetworkClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealViewModel() : ViewModel() {

    private val _searchData = MutableLiveData<List<Meal_Adapter_Data>>()
    val searchData: LiveData<List<Meal_Adapter_Data>> get() = _searchData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val networkInterface = NetworkClient.api

    fun fetchSearchResults(desc_kor: String) {
        networkInterface.getMeal(desc_kor, 1, 100, "", "", Contain.AUTH).enqueue(object :
            Callback<MealData> {
            override fun onResponse(call: Call<MealData>, response: Response<MealData>) {
                if (response.isSuccessful) {
                    val mealItems = response.body()?.mealBody?.mealItems
                    _searchData.value = mealItems?.map { item ->
                        Meal_Adapter_Data(item.DESC_KOR, item.NUTR_CONT1)
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
}