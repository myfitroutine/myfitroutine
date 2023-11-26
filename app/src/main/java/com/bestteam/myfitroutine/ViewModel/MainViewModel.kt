package com.bestteam.myfitroutine.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.Repository.MainRepository
import kotlinx.coroutines.launch
import java.sql.Timestamp

class MainViewModel(private val repository : MainRepository) : ViewModel() {

    fun addWeight(weight:WeightData){
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
    fun updateWeight(weight: WeightData) {
        viewModelScope.launch {
            repository.updateWeight(weight)
        }
    }

}