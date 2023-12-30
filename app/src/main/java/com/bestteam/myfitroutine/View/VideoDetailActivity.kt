package com.bestteam.myfitroutine.View

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import com.bestteam.myfitroutine.databinding.ActivityVideoDetailBinding

class VideoDetailActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var mContext: Context
    private lateinit var binding: ActivityVideoDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webView = binding.webView

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        val videoId = intent.getStringExtra("videoId")
        Log.d("kyi","${videoId}")
        val iframeCode = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/$videoId\" frameborder=\"0\" allowfullscreen></iframe>"
        val videotitle = intent.getStringExtra("title")
        val videodescription = intent.getStringExtra("description")
        binding.tvDetailDesc.text = videodescription
        binding.tvTitle.text = videotitle

        // WebView에 iframe 코드 로드
        webView.loadData(iframeCode, "text/html", "utf-8")

        binding.btnBack.setOnClickListener {
            finish()
        }

    }
}