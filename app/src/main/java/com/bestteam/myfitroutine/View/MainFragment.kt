package com.bestteam.myfitroutine.View

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.Dialog.TodayWeightDialog
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import com.bestteam.myfitroutine.databinding.FragmentMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    private lateinit var weightViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weightViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val todayWeight = binding.txtTodayWeight

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getTodayWeight()
            weightViewModel.todayWeight.collect { newWeight ->
                withContext(Dispatchers.Main) {
                    Log.d("nyh main", "onViewCreated: $newWeight")
                    todayWeight.text = (newWeight ?: "없어요").toString()
                }
            }
        }
        binding.btnTodayWeight.setOnClickListener {
            val todayWeightDialog = TodayWeightDialog()
            todayWeightDialog.show(childFragmentManager, "TodayWeight")
        }
    }

    override fun onResume() {
        super.onResume()

        weightViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val todayWeight = binding.txtTodayWeight

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getTodayWeight()
            weightViewModel.todayWeight.collect { newWeight ->
                withContext(Dispatchers.Main) {
                    Log.d("nyh main", "onViewCreated: $newWeight")
                    todayWeight.text = (newWeight ?: "없어요").toString()
                }
            }
        }
    }

}