package com.bestteam.myfitroutine.Dialog

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.bestteam.myfitroutine.Model.Todo
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.databinding.DialogEditTargetDiaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class TodoAddDialog:DialogFragment() {

    private lateinit var binding: DialogEditTargetDiaryBinding
    private var auth: FirebaseAuth? = null
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditTargetDiaryBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        var addText = binding.editTargetText
        binding.nextTargetAddBtn.setOnClickListener {
            val onlyDate = LocalDate.now().toString()
            var todo = Todo(context = addText.text.toString(), date = onlyDate)
            db.collection("${auth!!.uid!!}")
                .document("일지")
                .collection("일지")
                .document(onlyDate)
                .set(todo)
                .addOnCompleteListener {
                }
            dismiss()
        }
        return binding.root
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

        //디바이스 크기의 %로 크기 조정
        params?.width = (deviceWidth * 0.95).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        //다이얼로그 모서리 둥글게 하기
        dialog?.window?.setBackgroundDrawableResource(R.drawable.meal_dialog_shape)

    }
}