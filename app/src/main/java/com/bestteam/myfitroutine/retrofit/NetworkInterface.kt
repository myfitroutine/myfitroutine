package com.bestteam.myfitroutine.retrofit

import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.MealItem
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface NetworkInterface {
    @GET("getFoodNtrItdntList1")
    suspend fun getMeal() : List<MealItem>
}