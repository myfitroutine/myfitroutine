package com.bestteam.myfitroutine.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.Repository.MainRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainViewModel(private val repository : MainRepository) : ViewModel() {

    constructor() : this(MainRepository(FirebaseFirestore.getInstance())) {
    }


    private val _todayWeight = MutableStateFlow<String?>(null)
    val todayWeight : StateFlow<String?> get() = _todayWeight


    fun setTodayWeight(weight: Int) {
        _todayWeight.value = weight.toString()
        Log.d("nyh", "Viewmodel setTodayWeight: $weight")
    }

    fun addWeight(weight: WeightData){
        viewModelScope.launch {
            repository.addWeight(weight)
        }
    }
    fun getAllWeight() {
        viewModelScope.launch {
            val weights = repository.getAllWeight()
            Log.d("nyh", "getAllWeight: $weights")
        }
    }

    fun getTodayWeight(id: Timestamp) {
        viewModelScope.launch {
            val weight = repository.getTodayWeight(id)
        }
    }
//    fun updateWeight(weight: WeightData) {
//        viewModelScope.launch {
//            repository.updateWeight(weight)
//        }
//    }

}