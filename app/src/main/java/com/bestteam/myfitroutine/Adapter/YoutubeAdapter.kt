package com.bestteam.myfitroutine.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bestteam.myfitroutine.Model.VideoItem
import com.bestteam.myfitroutine.View.VideoDetailActivity
import com.bestteam.myfitroutine.databinding.VideoItemBinding
import com.bumptech.glide.Glide

class YoutubeAdapter(private val mContext: Context) : RecyclerView.Adapter<YoutubeAdapter.YoutubeHolder>(){
    var videoItems:MutableList<VideoItem> = ArrayList()

    inner class YoutubeHolder(val binding: VideoItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
        var thumbnails: ImageView = binding.thumbnailsIv
        var title: TextView = binding.titleTv
        init {
            thumbnails.setOnClickListener {
                val position = adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
                val clickItem = videoItems[position]
                if (position != RecyclerView.NO_POSITION) {
                    Log.d("nyh itemd", "clcick Item $clickItem")

                    val intent = Intent(thumbnails.context, VideoDetailActivity::class.java)
                    intent.putExtra("title", clickItem.title)
                    intent.putExtra("videoId",clickItem.videoId)
                    intent.putExtra("description",clickItem.description)
                    thumbnails.context.startActivity(intent)
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YoutubeHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = VideoItemBinding.inflate(inflater, parent, false)
        return YoutubeHolder(binding)
    }

    override fun onBindViewHolder(holder: YoutubeHolder, position: Int) {
        val youtubeVideoItems = videoItems[position]
        val youtubeHolder = holder
        Glide.with(mContext)
            .load(youtubeVideoItems.thumbnails)
            .into(youtubeHolder.thumbnails)
        youtubeHolder.title.text = youtubeVideoItems.title

    }

    override fun getItemCount(): Int {
        return videoItems.size
    }
}