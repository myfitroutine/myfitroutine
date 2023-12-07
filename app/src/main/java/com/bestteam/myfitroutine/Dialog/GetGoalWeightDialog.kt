package com.bestteam.myfitroutine.Dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.View.GraphFragment
import com.bestteam.myfitroutine.View.MainFragment
import com.bestteam.myfitroutine.ViewModel.GraphViewModel
import com.bestteam.myfitroutine.databinding.FragmentGetGoalWeightBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


@Suppress("UNREACHABLE_CODE")
class GetGoalWeightDialog : DialogFragment() {
    private lateinit var binding: FragmentGetGoalWeightBinding
    private lateinit var fireStore: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()
    private lateinit var viewModel: GraphViewModel

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
            viewModel = ViewModelProvider(this).get(GraphViewModel::class.java)
            if (userUid != null) {
                val collection = fireStore.collection("UserData")
                val document = collection.document(userUid)
                val goalWeight = binding.editText.text.toString()
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
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val graphFragment = parentFragment as? GraphFragment
        graphFragment?.updateUI()
    }
}