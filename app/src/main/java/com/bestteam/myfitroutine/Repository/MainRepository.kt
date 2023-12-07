package com.bestteam.myfitroutine.Repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bestteam.myfitroutine.Model.WeightData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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
    suspend fun getWeightGap(): WeightData?
    suspend fun setGoalWeight(callback: (Boolean) -> Unit)
    suspend fun getWeightDataForLastDays(days: Int): List<WeightData>
    suspend fun getGoalWeight(): Int?
    suspend fun getGoalWeightGap(): Int?

}
class MainRepositoryImpl (db: FirebaseFirestore): MainRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userUid = auth.currentUser?.uid.toString()
    private val collection = db.collection(userUid)


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
    override suspend fun getYesterdayWeight(): WeightData? {
        val yesterday = getYesterdayDate()
        val document = collection.document(yesterday).get().await()
        Log.d("nyh", "getYesterdayWeight repo: $yesterday")
        return document.toObject((WeightData::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getWeightGap(): WeightData? {
        val yesterdayWeight = getYesterdayWeight()?.weight
        val todayWeight = getTodayWeight()?.weight

        if (yesterdayWeight == null || todayWeight == null) {
            return null
        }
        val weightGapValue = todayWeight - yesterdayWeight
        val currentDate = getCurrentDate()

        return WeightData("", weightGapValue, currentDate)

        Log.d("nyh", "getWeightGap: $weightGapValue")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getWeightDataForLastDays(days: Int): List<WeightData> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(days.toLong())
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

        val querySnapshot = collection
            .whereGreaterThanOrEqualTo("date", startDate.format(dateFormat))
            .whereLessThanOrEqualTo("date", endDate.format(dateFormat))
            .get().await()

        return querySnapshot.documents.mapNotNull { it.toObject(WeightData::class.java) }
    }

    override suspend fun setGoalWeight(callback: (Boolean) -> Unit) {
        val fireStore = FirebaseFirestore.getInstance()
        val UserData = fireStore.collection("UserData")
        val document = UserData.document(userUid)

        try {
            val snapshot = document.get().await()

            if (snapshot.exists() && !snapshot.contains("goalWeight")) {
                Log.d("nyh", "setGoalWeight repo  user Uid = $userUid ")
                callback.invoke(true)
            } else {
                callback.invoke(false)
            }
        } catch (e: Exception) {
            Log.e("nyh", "repo error setGoalWeight", e)
            callback.invoke(false)
        }
    }

    override suspend fun getGoalWeight(): Int? {
        val fireStore = FirebaseFirestore.getInstance()
        val userDataCollection = fireStore.collection("UserData")
        val document = userDataCollection.document(userUid)

        try {
            val snapshot = document.get().await()

            if (snapshot.exists() && snapshot.contains("goalWeight")) {
                val goalWeight = snapshot.getString("goalWeight")
                Log.d("nyh", "getGoalWeight: $goalWeight")
                return goalWeight ?.toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getGoalWeightGap(): Int? {
        val todayWeight = getTodayWeight()?.weight
        val goalWeight = getGoalWeight()

        if (todayWeight == null) {
            return null
        }
        return goalWeight?.let { todayWeight?.minus(it) }
        Log.d("nyh", "getGoalWeightGap repo goalWeightGap : ${getGoalWeight()} ")
    }
}
