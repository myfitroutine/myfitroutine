package com.bestteam.myfitroutine.Dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.View.GraphFragment
import com.bestteam.myfitroutine.databinding.FragmentGetGoalWeightBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Suppress("UNREACHABLE_CODE")
class GetGoalWeightDialog : DialogFragment() {
    private lateinit var binding: FragmentGetGoalWeightBinding
    private lateinit var fireStore: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGetGoalWeightBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userUid = auth.currentUser?.uid.toString()
        fireStore = FirebaseFirestore.getInstance()

        binding.btnConfirm.setOnClickListener {
            if (userUid != null) {
                val goalWeightText = binding.editText.text.toString()
                if (goalWeightText.isEmpty() || !goalWeightText.isDigitsOnly() || goalWeightText.toInt() == 0) {
                    Toast.makeText(requireContext(), "목표 체중을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val goalWeight = goalWeightText.toInt()
                val collection = fireStore.collection("UserData")
                val document = collection.document(userUid)
                val userData = hashMapOf("goalWeight" to goalWeight)

                document.update(userData as Map<String, Any>)
                    .addOnSuccessListener {
                        collection.document("${auth.currentUser!!.uid}")
                        dismiss()
                    }
                    .addOnFailureListener { e ->
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
        val graphFragment = parentFragment as? GraphFragment
        graphFragment?.updateUI()
    }
}