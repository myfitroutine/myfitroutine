package com.bestteam.myfitroutine.Model


data class WeightData(
    val uid:String,
    val weight: Int,
    val date: String
){
    constructor() : this(
        "", 0, ""
    )
}
