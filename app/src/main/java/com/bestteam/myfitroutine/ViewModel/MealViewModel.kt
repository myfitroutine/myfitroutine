package com.bestteam.myfitroutine.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.MealItem
import com.bestteam.myfitroutine.Model.mealRseponse
import com.bestteam.myfitroutine.Repository.MealRepository
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {

    private val repository = MealRepository()

    private val _searchResult = MutableLiveData<List<MealItem>>()
    val searchResult : LiveData<List<MealItem>> get() = _searchResult

    fun getMealData() = viewModelScope.launch {
        _searchResult.value = repository.getMealData()
    }
}