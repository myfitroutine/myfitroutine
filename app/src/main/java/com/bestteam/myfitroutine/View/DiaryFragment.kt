package com.bestteam.myfitroutine.View


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Adapter.DiaryDateRecyclerViewAdapter
import com.bestteam.myfitroutine.Adapter.DiaryTodayTodoAdapter
import com.bestteam.myfitroutine.Model.DiaryData
import com.bestteam.myfitroutine.Model.Todo
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.databinding.FragmentDiaryBinding

class DiaryFragment : Fragment() {
    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = requireNotNull(_binding)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)

        val diaryDataList = mutableListOf<DiaryData>(
            DiaryData("2023-12-13", listOf(Todo("첫번째 할일",true),Todo("두번째할일",false),Todo("세번째할일",true))),
            DiaryData("2023-12-14", listOf(Todo("첫번째 할일",true),Todo("두번째할일",false),Todo("세번째할일")))
        )

        binding.diaryNextTargetBtn.setOnClickListener {
            val todoDialogView = layoutInflater.inflate(R.layout.dialog_target_diary, null)
            val recyclerViewTargetDiary: RecyclerView = todoDialogView.findViewById(R.id.nextTarget_recyclerView)
            val adapter = DiaryTodayTodoAdapter(diaryDataList[0].todos)
            recyclerViewTargetDiary.adapter = adapter
            recyclerViewTargetDiary.layoutManager = LinearLayoutManager(requireContext())

            val todoDialog = AlertDialog.Builder(requireActivity())
                .setView(todoDialogView)
                .create()
            todoDialog.show()
        }



        val adapter = DiaryDateRecyclerViewAdapter(diaryDataList)
        binding.diaryRecyclerView.adapter = adapter
        binding.diaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
