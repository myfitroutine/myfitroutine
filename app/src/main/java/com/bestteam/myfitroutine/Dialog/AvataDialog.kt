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
import com.bestteam.myfitroutine.View.MainFragment
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import com.bestteam.myfitroutine.databinding.FragmentAvataDialogBinding


class AvataDialog : DialogFragment() {
    private lateinit var binding: FragmentAvataDialogBinding
    private lateinit var viewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAvataDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val avata1 = binding.avata1
        val avata2 = binding.avata2
        val avata3 = binding.avata3
        val avata4 = binding.avata4
        val mainFrag = MainFragment()

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        avata1.setOnClickListener {
            saveSelectedAvata("avata_1")
            viewModel.updateSelectedAvata("avata_1")
            (parentFragment as? MainFragment)?.loadAndSetAvataImage()
            dismiss()
        }
        avata2.setOnClickListener {
            saveSelectedAvata("avata_2")
            viewModel.updateSelectedAvata("avata_2")
            (parentFragment as? MainFragment)?.loadAndSetAvataImage()
            dismiss()
        }
        avata3.setOnClickListener {
            saveSelectedAvata("avata_3")
            viewModel.updateSelectedAvata("avata_3")
            (parentFragment as? MainFragment)?.loadAndSetAvataImage()
            dismiss()
        }
        avata4.setOnClickListener {
            saveSelectedAvata("avata_4")
            viewModel.updateSelectedAvata("avata_4")
            (parentFragment as? MainFragment)?.loadAndSetAvataImage()
            dismiss()
        }
    }

    private fun saveSelectedAvata(avataName: String) {
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("selectedAvata", avataName)
        editor.apply()

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
        params?.height = (deviceHeigh * 0.45).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        //다이얼로그 모서리 둥글게 하기
        dialog?.window?.setBackgroundDrawableResource(R.drawable.meal_dialog_shape)
    }
}