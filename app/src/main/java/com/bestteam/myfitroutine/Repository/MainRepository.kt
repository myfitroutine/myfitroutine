package com.bestteam.myfitroutine.Repository

import com.bestteam.myfitroutine.Model.WeightData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale


class MainRepository(private val db: FirebaseFirestore) {

    private val collection = db.collection("weight")

    suspend fun addWeight(weight: WeightData) {
        val date = weight.date.toDate()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val yearMonthDay = dateFormat.format(date)

        // Document ID를 현재 년, 월, 일로 설정하여 추가
        collection.document(yearMonthDay).set(weight).await()
    }

    suspend fun getAllWeight(): List<WeightData> {
        val querySnapshot = collection.get().await()
        return querySnapshot.documents.mapNotNull { it.toObject(WeightData::class.java) }
    }
    suspend fun getTodayWeight(id: Timestamp): WeightData? {
        val date = id.toDate()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val yearMonthDay = dateFormat.format(date)
        val document = collection.document(yearMonthDay).get().await()

        return document.toObject(WeightData::class.java)
    }
//    suspend fun getYesterdayWeight(id: Timestamp): WeightData?{
//        val document = collection.document(id.toString()-1).get().await()
//        return document.toObject((WeightData::class.java))
//    }
    suspend fun updateWeight(weight: WeightData) {
        collection.document((weight.date).toString()).set(weight).await()
    }
    suspend fun deleteWeight(id: Timestamp) {
        collection.document(id.toString()).delete().await()
    }
}