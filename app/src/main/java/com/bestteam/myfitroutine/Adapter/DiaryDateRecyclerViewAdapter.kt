//package com.bestteam.myfitroutine.Adapter
//
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bestteam.myfitroutine.Model.DiaryData
//import com.bestteam.myfitroutine.databinding.RecyclerviewDateDiaryBinding
//
//
//class DiaryDateRecyclerViewAdapter(val dateList: MutableList<DiaryData>):RecyclerView.Adapter<DiaryDateRecyclerViewAdapter.Holder>(){
//
//    inner class Holder(val binding:RecyclerviewDateDiaryBinding):RecyclerView.ViewHolder(binding.root){
//        fun bind(position: Int){
//            binding.diaryDateDate.text = dateList[position].date
//            binding.diaryDateRecyclerView.apply {
//                adapter = DiaryTargetRecyclerViewAdapter(dateList[position].todos, position)
//                layoutManager = LinearLayoutManager(binding.diaryDateRecyclerView.context, LinearLayoutManager.VERTICAL, false)
//                setHasFixedSize(true)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
//        val binding = RecyclerviewDateDiaryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        return Holder(binding)
//    }
//
//    override fun onBindViewHolder(holder: Holder, position: Int) {
//        holder.bind(position)
//    }
//
//    override fun getItemCount(): Int {
//        return dateList.size
//    }
//
//}