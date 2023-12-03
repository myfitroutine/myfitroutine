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


    fun getAllWeight() {
        viewModelScope.launch {
            val weights = repository.getAllWeight()
            _weights.value = weights
            Log.d("nyh", "view model getAllWeight: $weights")

        }
    }
}