package com.bestteam.myfitroutine.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestteam.myfitroutine.Dialog.GetGoalWeightDialog
import com.bestteam.myfitroutine.Model.VideoItem
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.Repository.MainRepository
import com.bestteam.myfitroutine.Repository.MainRepositoryImpl
import com.bestteam.myfitroutine.Repository.YoutubeRepository
import com.bestteam.myfitroutine.View.MainFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository : MainRepositoryImpl,
    private val youtubeRepository: YoutubeRepository
) : ViewModel() {

    private val _todayWeight = MutableStateFlow<Int?>(null)
    val todayWeight : StateFlow<Int?> get() = _todayWeight

    private val _yesterdayWeight = MutableStateFlow<Int?>(null)
    val yesterdayWeight : StateFlow<Int?> get() = _yesterdayWeight

    private val _getWeightGap = MutableStateFlow<Int?>(null)
    val weightGap : StateFlow<Int?> get() = _getWeightGap

    private val _currentDate = MutableStateFlow<String?>(null)
    val currentDate: StateFlow<String?> get() = _currentDate

    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> get() = _currentUserName

    private val _selectedAvata = MutableStateFlow<String>("")
    val selectedAvata: StateFlow<String> get() = _selectedAvata

    private val _videoList = MutableStateFlow<List<VideoItem>>(emptyList())
    val videoList: StateFlow<List<VideoItem>> get() = _videoList

    @RequiresApi(Build.VERSION_CODES.O)
    fun addWeight(weight: WeightData){
        viewModelScope.launch {
            repository.addWeight(weight)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayWeight() {
        viewModelScope.launch {
            repository.getTodayWeight()?.let {
                _todayWeight.value = it.weight
            }
            Log.d("nyh", "getTodayWeight Viewmodel : value = ${_todayWeight.value}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYesterdayWeight(){
        viewModelScope.launch {
            repository.getYesterdayWeight()?.let {
                _yesterdayWeight.value = it.weight
            }
            Log.d("nyh", "getgetYesterdayDate Viewmodel : value = ${_yesterdayWeight.value}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    fun setGoalWeight(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            repository.setGoalWeight { result ->
                if (result && fragmentActivity.supportFragmentManager.fragments.any { it is MainFragment }) {
                    val mainFragment = fragmentActivity.supportFragmentManager.fragments.first { it is MainFragment } as MainFragment
                    val dialog = GetGoalWeightDialog()
                    Log.d("nyh", "viewmodel setGoalWeight: $result")
                    dialog.show(mainFragment.childFragmentManager, "get_goal_weight_dialog")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate() {
        viewModelScope.launch {
            val currentDate = repository.getCurrentDate()
            _currentDate.value = currentDate
        }
    }

    fun getUserName(){
        viewModelScope.launch {
            repository.getUserName()?.let {
                _currentUserName.value = it
            }
        }
    }
    fun updateSelectedAvata(avataName: String?) {
        avataName?.let {
            _selectedAvata.value = it
            Log.d("nyh", "updateSelectedAvata:viewmodel ${_selectedAvata.value} ")
        }
    }
    fun getVideo() {
        viewModelScope.launch {
            try {
                val response = youtubeRepository.getVideoList()
                if (response.isSuccessful) {
                    val youtubeApi = response.body()
                    youtubeApi?.let { api ->
                        val videoList = api.items?.mapNotNull { item ->
                            item?.snippet?.let { snippet ->
                                VideoItem(
                                    thumbnails = snippet.thumbnails?.medium?.url ?: "",
                                    title = snippet.title ?: "",
                                    categoryId = snippet.categoryId ?: "",
                                    videoId = item.id?.videoId ?: ""
                                )
                            }
                        } ?: emptyList()

                        _videoList.value = videoList
                        Log.d("nyh", "getVideo: viewmodel $videoList")
                    }
                } else {
                    Log.d("nyh", "getVideo: else")
                }
            } catch (e: Exception) {
                Log.e("nyh","getvideo e:$e")
            }
        }
    }
}