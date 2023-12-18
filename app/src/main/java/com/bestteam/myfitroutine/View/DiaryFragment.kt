package com.bestteam.myfitroutine.View


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestteam.myfitroutine.Adapter.DiaryTodoRecyclerViewAdapter
import com.bestteam.myfitroutine.Dialog.TodoAddDialog
import com.bestteam.myfitroutine.Model.Todo
import com.bestteam.myfitroutine.databinding.FragmentDiaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var auth: FirebaseAuth? = null
    private val db = Firebase.firestore
    private lateinit var Diaryadapter: DiaryTodoRecyclerViewAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        Log.d("uid", auth!!.uid.toString())
        var diaryDataList = mutableListOf<Todo>()
        db.collection("${auth!!.uid!!}")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    diaryDataList.add(
                                Todo(
                                    "${document.data["context"]}",
                                    "${document.data["date"]}"
                                )
                            )
                }
                Diaryadapter = DiaryTodoRecyclerViewAdapter(diaryDataList)
                binding.diaryRecyclerView.adapter = Diaryadapter
                binding.diaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
            .addOnFailureListener { exception ->
                Log.d("파이어", "Error getting documents: ", exception)
            }

        binding.diaryNextTargetBtn.setOnClickListener {
            val dialog = TodoAddDialog()
            dialog.show(childFragmentManager,"TodoAddDialog")
        }
        binding.diaryBackBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}

