package com.bestteam.myfitroutine.retrofit

import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.Model.MealData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface NetworkInterface {
    @GET("getFoodNtrItdntList1")
    fun getMeal(
        @Query("desc_kor") desc_kor : String?,
        @Query("pageNo") pageNo : Int?,
        @Query("numOfRows") numOfRows : Int?,
        @Query("bgn_year") bgn_year : String?,
        @Query("animal_plant") animal_plant : String?,
        @Query("ServiceKey") ServiceKey : String?,
        @Query("type") type : String? = "json"
    ) : Call<MealData>

}