package com.bestteam.myfitroutine.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.Repository.MainRepository
import com.bestteam.myfitroutine.Repository.MainRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GraphViewModel (private val repository : MainRepository):ViewModel(){

    constructor() : this(MainRepositoryImpl(FirebaseFirestore.getInstance()))

    private val _weights = MutableStateFlow<List<WeightData>>(emptyList())
    val weights: StateFlow<List<WeightData>> = _weights

    private val _goalWeight = MutableStateFlow<Int?>(null)
    val goalWeight : StateFlow<Int?> get() = _goalWeight

    private val _goalWeightGap = MutableStateFlow<Int?>(null)
    val goaweightGap : StateFlow<Int?> get() = _goalWeightGap


    fun getAllWeight() {
        viewModelScope.launch {
            val weights = repository.getAllWeight()
            _weights.value = weights
            Log.d("nyh", "view model getAllWeight: $weights")

        }
    }

    fun filterDataByPeriod(days: Int) {
        // 선택한 기간에 따라 데이터 필터링
        viewModelScope.launch {
            val filteredData = repository.getWeightDataForLastDays(days)
            _weights.value = filteredData
            Log.d("nyh", "view model filterDataByPeriod: $filteredData")
            Log.d("nyh", "view model filterDataByPeriod: $days")
        }
    }

    fun getGoalWeight() {
        viewModelScope.launch {
            val goalWeights = repository.getGoalWeight()
            _goalWeight.value = goalWeights
        }
    }
    fun getGoalWeightGap() {
        viewModelScope.launch {
            val goalWeightsGap = repository.getGoalWeightGap()
            _goalWeightGap.value = goalWeightsGap
        }
    }

}