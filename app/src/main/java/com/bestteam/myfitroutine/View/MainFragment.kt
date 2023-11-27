package com.bestteam.myfitroutine.View

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.Dialog.TodayWeightDialog
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import com.bestteam.myfitroutine.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weightViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val todayWeight = binding.txtTodayWeight

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.todayWeight.collect { newWeight ->
                Log.d("nyh main", "onViewCreated: $newWeight")
                todayWeight.text = newWeight ?: "없어요"
            }
        }

        binding.btnTodayWeight.setOnClickListener {
            val todayWeightDialog = TodayWeightDialog()
            todayWeightDialog.show(childFragmentManager, "TodayWeight")
        }

    }

    override fun onResume() {
        super.onResume()

        weightViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val todayWeight = binding.txtTodayWeight

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.todayWeight.collect { newWeight ->
                Log.d("nyh main", "onresume: $newWeight")
                todayWeight.text = newWeight ?: "없어요"
            }
        }

    }

}