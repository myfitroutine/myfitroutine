package com.bestteam.myfitroutine.Model

import java.sql.Timestamp

data class WeightData(
    val uid:String,
    val weight:Int,
    val date:Timestamp
){
    constructor() : this(
        "", 0, Timestamp.valueOf(null)
    )
}
