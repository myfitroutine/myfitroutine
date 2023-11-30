package com.bestteam.myfitroutine.Repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bestteam.myfitroutine.Model.WeightData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

interface MainRepository {
    suspend fun addWeight(weight: WeightData)
    suspend fun getAllWeight(): List<WeightData>
    suspend fun getTodayWeight(): WeightData?
    suspend fun getYesterdayWeight(): WeightData?
    suspend fun getCurrentDate(): String
    suspend fun getYesterdayDate(): String
}
class MainRepositoryImpl (db: FirebaseFirestore): MainRepository {

    private val collection = db.collection("weight")

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        return currentDate.format(dateFormat)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getYesterdayDate(): String {
        val yesterday = LocalDate.now().minusDays(1)
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        return yesterday.format(dateFormat)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addWeight(weight: WeightData) {
        val today = getCurrentDate()

        // Document ID를 현재 년, 월, 일로 설정하여 추가
        collection.document(today).set(weight).await()
    }

    override suspend fun getAllWeight(): List<WeightData> {
        val querySnapshot = collection.get().await()
        return querySnapshot.documents.mapNotNull { it.toObject(WeightData::class.java) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getTodayWeight(): WeightData? {
        val today = getCurrentDate()
        val document = collection.document(today).get().await()
        Log.d("nyh", "getTodayWeight: repo $today")

        return document.toObject(WeightData::class.java)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getYesterdayWeight(): WeightData?{
        val yesterday = getYesterdayDate()
        val document = collection.document(yesterday).get().await()
        Log.d("nyh", "getYesterdayWeight repo: $yesterday")
        return document.toObject((WeightData::class.java))
    }
}