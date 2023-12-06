package com.bestteam.myfitroutine.View

import android.content.Context
import android.widget.TextView
import com.bestteam.myfitroutine.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight

class CustomMarkerView (
    context: Context,
    layoutResource: Int,
    private val dates: List<String>
) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {
            val index = e.x.toInt()
            if (index >= 0 && index < dates.size) {
                tvContent.text = "${dates[index]} \n Weight: ${e.y}"
            } else {
                tvContent.text = "Weight: ${e.y}"
            }

            super.refreshContent(e, highlight)
        }
    }
}