package com.bestteam.myfitroutine.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.retrofit.NetworkClient
import com.bestteam.myfitroutine.retrofit.NetworkInterface
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MealRepository() {

//    private val networkInterface = NetworkClient.api
//
//    suspend fun fetchSearchResults(desc_kor: String): List<Meal_Adapter_Data>? {
//        return try {
//            val response = networkInterface.getMeal(desc_kor, 1, 100, "", "", Contain.AUTH).execute()
//            if (response.isSuccessful) {
//                response.body()?.mealBody?.mealItems?.map { item ->
//                    Meal_Adapter_Data(item.DESC_KOR, item.NUTR_CONT1)
//                } ?: emptyList()
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }

}