package com.bestteam.myfitroutine.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Model.DiaryData
import com.bestteam.myfitroutine.Model.Todo
import com.bestteam.myfitroutine.databinding.RecyclerviewTargetDiaryBinding

class DiaryTargetRecyclerViewAdapter(val targetList: List<Todo>, position:Int):
    RecyclerView.Adapter<DiaryTargetRecyclerViewAdapter.Holder>() {

    inner class Holder(val binding: RecyclerviewTargetDiaryBinding):RecyclerView.ViewHolder(binding.root){
        val todo = binding.diaryTargetCheckBox
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiaryTargetRecyclerViewAdapter.Holder {
        val binding = RecyclerviewTargetDiaryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: DiaryTargetRecyclerViewAdapter.Holder, position: Int) {
        holder.binding.diaryTargetCheckBox.text = targetList[position].content
        if(targetList[position].check == true) {
            holder.binding.diaryTargetCheckBox.isChecked = true
        }else{
            holder.binding.diaryTargetCheckBox.isChecked = false
        }
    }

    override fun getItemCount(): Int {
        return targetList.size
    }
}

