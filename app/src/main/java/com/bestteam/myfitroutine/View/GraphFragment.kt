package com.bestteam.myfitroutine.View

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.Dialog.FilterDateDialog
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.ViewModel.GraphViewModel
import com.bestteam.myfitroutine.databinding.FragmentGraghBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.coroutines.launch

class GraphFragment : Fragment() {
    private lateinit var binding: FragmentGraghBinding
    private lateinit var viewModel: GraphViewModel
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGraghBinding.inflate(inflater, container, false)
        lineChart = binding.lineChart
        setUpChart()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineChart = binding.lineChart
        viewModel = ViewModelProvider(this).get(GraphViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllWeight()
            viewModel.weights.collect { weights ->
                updateChart(weights)
            }
        }

        binding.btnSetDate.setOnClickListener {
            val filterDateDialog = FilterDateDialog()
            filterDateDialog.show(childFragmentManager, "filterDateDialog")
        }
    }

    private fun setUpChart() {
        viewModel = ViewModelProvider(this).get(GraphViewModel::class.java)
        val xAxis = lineChart.xAxis

        xAxis.apply {

            position = XAxis.XAxisPosition.BOTTOM
            textSize = 20f
            setDrawGridLines(false)
            granularity = 1f
            axisMinimum = 1f
            isGranularityEnabled = true
        }

        lineChart.apply {
            axisRight.isEnabled = false

            // 배경색을 흰색으로 설정
            setBackgroundColor(Color.WHITE)

            // 그리드 라인 비활성화
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)

            // X축 라벨을 표시하지 않음
            xAxis.setDrawLabels(false)

            // 범례 비활성화
            legend.isEnabled = false

            // 체중의 최대값을 가져와서 Y축의 최대값으로 설정
            val maxWeight = viewModel.weights.value?.maxByOrNull { it.weight }?.weight ?: 50f
            axisLeft.axisMaximum = maxWeight as Float + 100f

            legend.apply {
                textSize = 25f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }
        }
    }

    private fun updateChart(weights: List<WeightData>) {
        val entries = mutableListOf<Entry>()
        Log.d("nyh", "updateChart: $weights")


        weights.forEachIndexed { index, weightData ->
            entries.add(
               Entry(
                    index.toFloat(),
                    weightData.weight.toFloat()
                )
            )
        }

        val dataSet = LineDataSet(entries, "Weight Data").apply {
            // 선 및 점의 컬러 설정
            color = Color.YELLOW
            setCircleColor(Color.YELLOW)
            circleHoleColor = Color.YELLOW

            // 점의 크기 및 테두리 설정
            setCircleSize(8f)
            setDrawCircleHole(true)
            circleHoleRadius = 4f

            // 선 비활성화
            setDrawValues(false)
            setDrawIcons(false)
        }

        val lineData = LineData(dataSet)
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = DateValueFormatter(weights.map { it.date })

        lineChart.data = lineData

        lineChart.setDrawMarkers(true)
        val mv = XYMarkerView(requireContext(), weights.map { it.date })
        mv.chartView = lineChart
        lineChart.marker = mv

        // 클릭 시 빨간색 테두리 추가
        dataSet.highLightColor = Color.RED

        // 그래프 갱신
        lineChart.invalidate()
    }

    private class DateValueFormatter(private val dates: List<String>) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            val label = if (index >= 0 && index < dates.size) {
                dates[index]
            } else {
                ""
            }
            Log.d("nyh", "getAxisLabel: $label")
            return label
        }
    }
}
class XYMarkerView(
    context: Context,
    private val xAxisDates: List<String>
) : MarkerView(context, R.layout.custom_marker_view) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {
            val index = e.x.toInt()
            val label = if (index >= 0 && index < xAxisDates.size) {
                "${xAxisDates[index]} \n 몸무게: ${e.y}"
            } else {
                "몸무게: ${e.y}"
            }
            tvContent.text = label
            super.refreshContent(e, highlight)
        }
    }
}