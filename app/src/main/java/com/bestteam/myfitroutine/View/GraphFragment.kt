package com.bestteam.myfitroutine.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bestteam.myfitroutine.Dialog.GetGoalWeightDialog
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

@Suppress("UNREACHABLE_CODE")
class GraphFragment : Fragment() {
    private lateinit var binding: FragmentGraghBinding
    private lateinit var viewModel: GraphViewModel
    private lateinit var lineChart: LineChart
    private var selectedButton: AppCompatButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGraghBinding.inflate(inflater, container, false)
        lineChart = binding.lineChart
        setUpChart()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineChart = binding.lineChart
        viewModel = ViewModelProvider(this).get(GraphViewModel::class.java)

        val btnWeekly = binding.btnWeekly
        val btn14days = binding.btn14days
        val btnMonthly = binding.btnMonthly
        val btn3Monthly = binding.btn3monthly
        val btnYear = binding.btnYear
        val buttons = listOf(btnWeekly, btn14days, btnMonthly, btn3Monthly, btnYear)

        buttons.forEach { button ->
            button.setOnClickListener {
                onButtonClick(button, buttons)
            }
        }

        val btnGraphBack = binding.btnGraphBack
            btnGraphBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            btnGraphBack.setTextColor(Color.WHITE)
            btnGraphBack.setBackgroundResource(R.drawable.login_signup_button)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllWeight()
            viewModel.weights.collect { weights ->
                updateChart(weights)
            }
        }

        val goalWeight = binding.txtGoalWeight
        val goalWeightGap = binding.txtGapGaolWeight

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getGoalWeight()
            viewModel.goalWeight.collect { goal ->
                if (goal != null) {
                    goalWeight.text = goal.toString() + " KG"
                } else {
                    goalWeight.text = "?" + " KG"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getGoalWeightGap()
            viewModel.goaweightGap.collect { goalGap ->
                if (goalGap != null) {
                    goalWeightGap.text = goalGap.toString()
                } else {
                    goalWeightGap.text = "?"
                }
            }
        }

        val btnSetGoal = binding.btnSetGoal
        btnSetGoal.setOnClickListener {
            Log.d("nyh", "onViewCreated: touched ")
            val getGoalWeightDialog = GetGoalWeightDialog()
            getGoalWeightDialog.show(childFragmentManager, "getGoalWeight")
        }
    }

    private fun onButtonClick(clickedButton: AppCompatButton, allButtons: List<AppCompatButton>) {

        if (selectedButton != clickedButton) {

            selectedButton?.let {
                it.setTextColor(Color.BLACK)
                it.setBackgroundResource(android.R.color.transparent)
            }

            clickedButton.setTextColor(Color.WHITE)
            clickedButton.setBackgroundResource(R.drawable.login_signup_button)

            selectedButton = clickedButton

            allButtons.filter { it != selectedButton }.forEach { button ->
                button.setTextColor(Color.BLACK)
                button.setBackgroundResource(android.R.color.transparent)
            }

            viewModel.filterDataByPeriod(getPeriodForButton(clickedButton))
        }
    }

    private fun getPeriodForButton(button: AppCompatButton): Int {

        return when (button.id) {
            R.id.btn_weekly -> 7
            R.id.btn_14days -> 14
            R.id.btn_monthly -> 30
            R.id.btn_3monthly -> 90
            R.id.btn_year -> 365
            else -> 7
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
            // X축 라인의 색상을 Black으로 설정

            axisLineColor = Color.BLACK
            // x축 라인 두께

            axisLineWidth = 1f
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

            // 왼쪽 Y축 라벨을 표시하지 않음
            axisLeft.setDrawLabels(false)

            // 왼쪽 Y축 라인의 색상을 Black으로 설정
            axisLeft.axisLineColor = Color.BLACK
            // y축 라인 두께
            axisLeft.axisLineWidth = 1f
            // 범례 비활성화
            legend.isEnabled = false

            // Description Label 비활성화
            description.isEnabled = false

            // 체중의 최대값을 가져와서 Y축의 최대값으로 설정
            val maxWeight =
                viewModel.weights.value?.maxByOrNull { it.weight }?.weight?.toFloat() ?: 50f
            axisLeft.axisMaximum = maxWeight + 100f
            axisLeft.axisMinimum = maxWeight - 30f

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
            color = Color.parseColor("#FFC300")
            setCircleColor(Color.parseColor("#FFC300"))


            // 점의 크기 및 테두리 설정
            setCircleSize(7f)
//            setDrawCircleHole(true)

////             선택 시 라인 하이라이트
//            setHighlightEnabled(true)
//            highLightColor = Color.RED
//            highlightLineWidth = 1f


            // 선 비활성화
            lineWidth = 0f
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
//        dataSet.highLightColor = Color.RED

//         그래프 갱신
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

    fun updateUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getGoalWeight()
            viewModel.goalWeight.collect { goal ->
                binding.txtGoalWeight.text = goal.toString() + "KG"
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getGoalWeightGap()
            viewModel.goaweightGap.collect { goalGap ->
                binding.txtGapGaolWeight.text = goalGap.toString() + " KG"
            }
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
                "${xAxisDates[index]} / ${e.y}Kg"
            } else {
                "몸무게: ${e.y}"
            }
            tvContent.text = label
            super.refreshContent(e, highlight)
        }
    }
}
