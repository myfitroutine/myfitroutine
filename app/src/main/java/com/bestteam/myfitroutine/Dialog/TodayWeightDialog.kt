package com.bestteam.myfitroutine.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import com.bestteam.myfitroutine.databinding.FragmentTodayWeightDialogBinding
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale


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

            val timestampNow = Timestamp.now()
            val date = timestampNow.toDate()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val yearMonthDay = dateFormat.format(date)

            val weight = WeightData("uni",todayWeight, timestampNow)
            if(todayWeightText.isEmpty()) {

                viewModel.addWeight(weight)
                viewModel.setTodayWeight(todayWeight)
                dismiss()
            }
            else {
                viewModel.addWeight(weight)
//                viewModel.updateWeight(weight)
                viewModel.setTodayWeight(todayWeight)
                dismiss()
            }
        }
    }
}
