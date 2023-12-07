package com.bestteam.myfitroutine.Dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.View.MainFragment
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import com.bestteam.myfitroutine.databinding.FragmentTodayWeightDialogBinding
import kotlinx.coroutines.launch


class TodayWeightDialog : DialogFragment() {
    private lateinit var binding: FragmentTodayWeightDialogBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayWeightDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.btnConfirm.setOnClickListener {
            val todayWeightText = binding.editText.text.toString()
            val todayWeight = if (todayWeightText.isNotEmpty()) todayWeightText.toInt() else 0

            lifecycleScope.launch { // lifecycleScope.launch를 사용하여 코루틴 시작
                val yearMonthDay = viewModel.getRepository().getCurrentDate()

                val weight = WeightData("uni", todayWeight, yearMonthDay)

                if (todayWeightText.isEmpty()) {
                    viewModel.addWeight(weight)
                    Log.d("nyh", "dialog if onViewCreated: $todayWeight")
                    dismiss()
                } else {
                    viewModel.addWeight(weight)
                    Log.d("nyh", "dialog else onViewCreated: $todayWeight")
                    dismiss()
                }
            }
        }
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (parentFragment as? MainFragment)?.onResume()
    }
}
