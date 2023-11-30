package com.bestteam.myfitroutine.Model

import com.google.firebase.Timestamp


data class WeightData(
    val uid:String,
    val weight: Int,
    val date: String
){
    constructor() : this(
        "", 0, ""
    )
}
