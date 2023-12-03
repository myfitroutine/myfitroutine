package com.bestteam.myfitroutine.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.Model.WeightData
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.ViewModel.GraphViewModel
import com.bestteam.myfitroutine.databinding.FragmentGraghBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

            // 체중의 최대값을 가져와서 Y축의 최대값으로 설정
            val maxWeight = viewModel.weights.value?.maxByOrNull { it.weight }?.weight ?: 50f
            axisLeft.axisMaximum = maxWeight as Float + 100f // 조절 가능한 값으로 설정해보세요.

            legend.apply {
                textSize = 25f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }
        }

    }

//    private fun setupLineChart() {
//
//        lineChart.apply {
//            setDrawGridBackground(false)
//            description.isEnabled = false
//            setTouchEnabled(true)
//            isDragEnabled = true
//            setScaleEnabled(true)
//            axisRight.isEnabled = false
//
//            val xAxis: XAxis = getXAxis()
//
//            xAxis.apply {
//                position = XAxis.XAxisPosition.BOTTOM
//                setDrawGridLines(false)
//                labelRotationAngle = -45f
//                valueFormatter = DateValueFormatter()
//            }
//        }
//    }

    private fun updateChart(weights: List<WeightData>) {
        val entries = mutableListOf<com.github.mikephil.charting.data.Entry>()
        Log.d("nyh", "updateChart: $weights")

        weights.forEachIndexed { index, weightData ->
            entries.add(
                com.github.mikephil.charting.data.Entry(
                    index.toFloat(),
                    weightData.weight.toFloat()
                )
            )
        }

        val dataSet = LineDataSet(entries, "Weight Data").apply {
            setColors(*ColorTemplate.COLORFUL_COLORS)
            setDrawValues(true)
        }

        val lineData = LineData(dataSet)
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = DateValueFormatter(weights.map { it.date })

        lineChart.data = lineData
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