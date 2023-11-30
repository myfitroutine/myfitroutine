package com.bestteam.myfitroutine.Dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.Repository.MainRepositoryImpl
import com.bestteam.myfitroutine.View.MainFragment
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

            val weight = WeightData("uni",todayWeight, yearMonthDay)
            if(todayWeightText.isEmpty()) {

                viewModel.addWeight(weight)
                Log.d("nyh", "dialog if onViewCreated: $todayWeight")
                dismiss()
            }
            else {
                viewModel.addWeight(weight)
                Log.d("nyh", "dialog else onViewCreated: $todayWeight")
                dismiss()
            }
        }
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Dialog가 닫힐 때 Fragment를 갱신하는 작업을 수행합니다.
        // 예를 들어, Fragment 내부에서 데이터를 다시 로드하거나 업데이트할 수 있습니다.
        // 아래는 간단한 예시입니다.
        (parentFragment as? MainFragment)?.onResume()
    }
}
