package com.bestteam.myfitroutine.retrofit

import com.bestteam.myfitroutine.Contain
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object NetworkClient {


    val api : NetworkInterface
        get() = instanse.create(NetworkInterface::class.java)

    private val instanse : Retrofit
        get(){
            val gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
                .baseUrl(Contain.MEAL_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

}