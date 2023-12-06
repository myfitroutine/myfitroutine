package com.bestteam.myfitroutine.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.Repository.MainRepository
import com.bestteam.myfitroutine.Repository.MainRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainViewModel(private val repository : MainRepository) : ViewModel() {

    constructor() : this(MainRepositoryImpl(FirebaseFirestore.getInstance()))

    private val _todayWeight = MutableStateFlow<Int?>(null)
    val todayWeight : StateFlow<Int?> get() = _todayWeight

    private val _yesterdayWeight = MutableStateFlow<Int?>(null)
    val yesterdayWeight : StateFlow<Int?> get() = _yesterdayWeight

    private val _getWeightGap = MutableStateFlow<Int?>(null)
    val weightGap : StateFlow<Int?> get() = _getWeightGap


    fun addWeight(weight: WeightData){
        viewModelScope.launch {
            repository.addWeight(weight)
        }
    }


    fun getTodayWeight() {
        viewModelScope.launch {
            repository.getTodayWeight()?.let {
                _todayWeight.value = it.weight
            }
            Log.d("nyh", "getTodayWeight Viewmodel : value = ${_todayWeight.value}")
        }
    }

    fun getYesterdayWeight(){
        viewModelScope.launch {
            repository.getYesterdayWeight()?.let {
                _yesterdayWeight.value = it.weight
            }
            Log.d("nyh", "getgetYesterdayDate Viewmodel : value = ${_yesterdayWeight.value}")
        }
    }
    fun getWeightGap() {
        viewModelScope.launch {
            repository.getWeightGap()?.let {
                _getWeightGap.value = it.weight
                Log.d("nyh", "getWeightGap viewModel: ${it.weight}")
            }
        }
    }

    fun getRepository(): MainRepository {
        return repository
    }

}