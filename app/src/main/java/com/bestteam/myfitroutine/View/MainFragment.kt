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
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.Dialog.TodayWeightDialog
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import com.bestteam.myfitroutine.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE", "DEPRECATION")
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
        weightViewModel.setGoalWeight(requireActivity())

        val todayWeight = binding.txtTodayWeight
        val yesterdayWeight = binding.txtYesterWeight


        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getYesterdayWeight()
            weightViewModel.yesterdayWeight.collect { yesterday ->
                yesterdayWeight.text = (yesterday ?: "없어요").toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getTodayWeight()
            weightViewModel.todayWeight.collect { newWeight ->
                Log.d("nyh main", "onViewCreated: $newWeight")
                todayWeight.text = (newWeight ?: "없어요").toString()
            }
        }

        binding.btnTodayWeight.setOnClickListener {
            val todayWeightDialog = TodayWeightDialog()
            todayWeightDialog.show(childFragmentManager, "TodayWeight")
        }
        binding.btnGraph.setOnClickListener {
            val graphFragment = GraphFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentFrame, graphFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        binding.btnDiary.setOnClickListener {
            val diaryFragment = DiaryFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentFrame, diaryFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        binding.btnMeal.setOnClickListener {
            val mealFragment = MealFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentFrame, mealFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    override fun onResume() {
        super.onResume()

        weightViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val todayWeight = binding.txtTodayWeight
        var weightGap = binding.txtChangeWeight
        var weightGapTxt = binding.txtChagneTxt

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getTodayWeight()
            weightViewModel.todayWeight.collect { newWeight ->
                Log.d("nyh main", "onViewCreated: $newWeight")
                todayWeight.text = (newWeight ?: "없어요").toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getWeightGap()
            weightViewModel.weightGap.collect { getWeightGap ->

                when {
                    getWeightGap != null && getWeightGap < 0 -> {
                        weightGapTxt.text = "감량"
                        weightGap.text = getWeightGap.toString()
                    }

                    getWeightGap != null && getWeightGap == 0 -> {
                        weightGapTxt.text = "같음"
                        weightGap.text = getWeightGap.toString()
                    }

                    getWeightGap != null && getWeightGap > 0 -> {
                        weightGapTxt.text = "증량"
                        weightGap.text = getWeightGap.toString()
                    }

                    else -> {
                        weightGapTxt.text = "가 없어요"
                        weightGap.text = "비교"
                    }
                }
                Log.d("nyh main", "onresume getWeightGap = : $getWeightGap")
                weightGap.text = (getWeightGap ?: "없어요").toString()
            }

        }

    }
}