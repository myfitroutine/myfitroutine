package com.bestteam.myfitroutine.Dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.ViewModel.GraphViewModel
import com.bestteam.myfitroutine.databinding.FragmentFilterDateDialogBinding

class FilterDateDialog : DialogFragment() {

    private lateinit var binding: FragmentFilterDateDialogBinding
    private lateinit var viewModel : GraphViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterDateDialogBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(GraphViewModel::class.java)

        binding.txtWeekly.setOnClickListener {
            // "일주일"이 선택된 경우
            viewModel.filterDataByPeriod(7) // 7일로 필터링
            dismiss()
        }

        binding.txtMonthly.setOnClickListener {
            // "한달"이 선택된 경우
            viewModel.filterDataByPeriod(30) // 30일로 필터링
            dismiss()
        }

        binding.txtYear.setOnClickListener {
            // "일년"이 선택된 경우
            viewModel.filterDataByPeriod(365) // 365일로 필터링
            dismiss()
        }

//        view.findViewById<TextView>(R.id.txt_all).setOnClickListener {
//            // "전체"가 선택된 경우
//            viewModel.showAllData() // 전체 데이터 표시
//            dismiss()
//        }
    }

}