//package com.bestteam.myfitroutine.Adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bestteam.myfitroutine.Model.Todo
//import com.bestteam.myfitroutine.databinding.RecyclerviewTodaytargetDiaryBinding
//
//class DiaryTodayTodoAdapter(val dateList: List<Todo>):RecyclerView.Adapter<DiaryTodayTodoAdapter.Holder>() {
//    inner class Holder(val binding:RecyclerviewTodaytargetDiaryBinding):RecyclerView.ViewHolder(binding.root) {
//        val todo = binding.nextTargetAddTv
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
//        val binding = RecyclerviewTodaytargetDiaryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        return  Holder(binding)
//    }
//
//    override fun getItemCount(): Int {
//        return dateList.size
//    }
//
//    override fun onBindViewHolder(holder: Holder, position: Int) {
//        holder.binding.nextTargetAddTv.text = dateList[position].content
//    }
//}