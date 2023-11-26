package com.bestteam.myfitroutine.Repository

import com.bestteam.myfitroutine.Model.WeightData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp

class MainRepository(private val db: FirebaseFirestore) {

    private val collection = db.collection("weight")

    suspend fun addWeight(weight: WeightData) {
        collection.add(weight).await()
    }

    suspend fun getAllWeight(): List<WeightData> {
        val querySnapshot = collection.get().await()
        return querySnapshot.documents.mapNotNull { it.toObject(WeightData::class.java) }
    }
    suspend fun getTodayWeight(id: Timestamp): WeightData? {
        val document = collection.document(id.toString()).get().await()
        return document.toObject(WeightData::class.java)
    }
//    suspend fun getYesterdayWeight(id: Timestamp): WeightData?{
//        val document = collection.document(id.toString()-1).get().await()
//        return document.toObject((WeightData::class.java))
//    }
    suspend fun updateWeight(weight: WeightData) {
        collection.document((weight.date ?:"").toString()).set(weight).await()
    }
    suspend fun deleteWeight(id: Timestamp) {
        collection.document(id.toString()).delete().await()
    }

}