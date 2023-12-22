package com.bestteam.myfitroutine.Dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.View.MainFragment
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import com.bestteam.myfitroutine.databinding.FragmentTodayWeightDialogBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.lang.NumberFormatException


class TodayWeightDialog : DialogFragment() {
    private lateinit var binding: FragmentTodayWeightDialogBinding
    private lateinit var viewModel: MainViewModel
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
            val userUid = auth.currentUser?.uid.toString()
            val todayWeightText = binding.getTodayWeight.text.toString()

            if (todayWeightText.isEmpty() || !todayWeightText.isDigitsOnly() || todayWeightText.toInt() == 0) {
                Toast.makeText(requireContext(), "오늘의 체중을 입력해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val todayWeight = if (todayWeightText.isNotEmpty()) todayWeightText.toInt() else 0

            lifecycleScope.launch {
                val yearMonthDay = viewModel.getRepository().getCurrentDate()

                val weight = WeightData(userUid, todayWeight, yearMonthDay)

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
        isCancelable = false
    }

    override fun onResume() {
        super.onResume()


        //디바이스 크기 구하기
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeigh = size.y

        //디바이스 크기의 %로 크기 조정
        params?.width = (deviceWidth * 0.8).toInt()
        params?.height = (deviceHeigh * 0.35).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        //다이얼로그 모서리 둥글게 하기
        dialog?.window?.setBackgroundDrawableResource(R.drawable.meal_dialog_shape)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (parentFragment as? MainFragment)?.onResume()
    }
}
