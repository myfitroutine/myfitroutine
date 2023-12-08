package com.bestteam.myfitroutine.Repository

import com.bestteam.myfitroutine.retrofit.NetworkClient
import com.bestteam.myfitroutine.retrofit.NetworkInterface


class MealRepository {

    private val client = NetworkClient.getInstance().create(NetworkInterface::class.java)
    suspend fun getMealData() = client.getMeal()

}