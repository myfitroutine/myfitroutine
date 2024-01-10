package com.bestteam.myfitroutine.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Dialog.TodoDeleteDialog
import com.bestteam.myfitroutine.Model.Todo
import com.bestteam.myfitroutine.databinding.RecyclerviewTodoDiaryBinding

class DiaryTodoRecyclerViewAdapter(val todoList:MutableList<Todo>,val fragmentManager: FragmentManager):RecyclerView.Adapter<DiaryTodoRecyclerViewAdapter.Holder>() {


    fun removeTodo(date: String) {
        val todoToRemove = todoList.firstOrNull { it.date == date }
        todoToRemove?.let {
            todoList.remove(it)
            notifyDataSetChanged()
        }
    }
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
        holder.itemView.setOnLongClickListener {
            val dialog = TodoDeleteDialog(todoList[position].date, this)
            dialog.show(fragmentManager,"DeleteDialog")
            true
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}