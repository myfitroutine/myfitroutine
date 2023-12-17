package com.bestteam.myfitroutine.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Model.Todo
import com.bestteam.myfitroutine.databinding.RecyclerviewTodoDiaryBinding

class DiaryTodoRecyclerViewAdapter(val todoList:MutableList<Todo>):RecyclerView.Adapter<DiaryTodoRecyclerViewAdapter.Holder>() {

    inner class Holder(val binding:RecyclerviewTodoDiaryBinding):RecyclerView.ViewHolder(binding.root){
        val context = binding.TodoTextTv
        val date = binding.TodoDateTv
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiaryTodoRecyclerViewAdapter.Holder {
        val binding = RecyclerviewTodoDiaryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: DiaryTodoRecyclerViewAdapter.Holder, position: Int) {
        holder.context.text = todoList[position].context
        holder.date.text = todoList[position].date
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}