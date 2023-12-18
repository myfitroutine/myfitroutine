package com.bestteam.myfitroutine.Model

import com.google.gson.annotations.SerializedName

data class MealData(
    @SerializedName("body")
    val mealBody: MealBody,
    @SerializedName("header")
    val mealHeader: MealHeader
)

data class MealBody(
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
    @SerializedName("items")
    val mealItems: MutableList<mealItem>
)

data class MealHeader(
    val resultCode: String,
    val resultMsg: String
)

data class mealItem(
    val DESC_KOR: String,
    val SERVING_WT: String,
    val NUTR_CONT1: Double,
    val NUTR_CONT2: Double,
    val NUTR_CONT3: Double,
    val NUTR_CONT4: Double,
    val NUTR_CONT5: String,
    val NUTR_CONT6: String,
    val NUTR_CONT7: String,
    val NUTR_CONT8: String,
    val NUTR_CONT9: String,
    val BGN_YEAR: String,
    val ANIMAL_PLANT: String
)