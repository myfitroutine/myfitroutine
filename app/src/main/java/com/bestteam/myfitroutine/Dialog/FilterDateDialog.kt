package com.bestteam.myfitroutine.Dialog

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
    override fun onResume() {
        super.onResume()

        //디바이스 크기 구하기
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params : ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeigh = size.y

        //디바이스 크기의 %로 크기 조정
        params?.width = (deviceWidth * 0.8).toInt()
        params?.height = (deviceHeigh * 0.35).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        //다이얼로그 모서리 둥글게 하기
        dialog?.window?.setBackgroundDrawableResource(R.drawable.meal_dialog_shape)
    }

}