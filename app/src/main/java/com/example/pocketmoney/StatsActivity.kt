package com.example.pocketmoney

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pocketmoney.application.TransactionApplication
import com.example.pocketmoney.database.TransactionRepository
import com.example.pocketmoney.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.PercentFormatter


class StatsActivity : AppCompatActivity() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    private var isIncomeSelected = false // Toggle state for income/expense

    val allColors = mutableListOf<Int>().apply {
        addAll(ColorTemplate.MATERIAL_COLORS.asList())
        addAll(
            listOf(
                Color.rgb(155, 89, 182),
                Color.rgb(230, 126, 34),
                Color.rgb(27, 188, 156),
                Color.rgb(52, 73, 94),
                Color.rgb(243, 156, 18)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val application = applicationContext as TransactionApplication
        val repository = application.repository
        viewModel =
            ViewModelProvider(this, TransactionViewModel.TransactionViewModelFactory(repository))
                .get(TransactionViewModel::class.java)

        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)

        setupPieChart()
        setupBarChart()

        // Example of toggling between income and expense (implement your actual toggle logic)
        val toggleIncomeExpense =
            findViewById<android.widget.ToggleButton>(R.id.toggleIncomeExpense)
        toggleIncomeExpense.setOnCheckedChangeListener { _, isChecked ->
            isIncomeSelected = isChecked
            updateCharts()
        }
        updateCharts()
    }

    private fun updateCharts() {
        if (isIncomeSelected) {
            setupPieChartForIncome()
            setupBarChartForIncome()
        } else {
            setupPieChartForExpense()
            setupBarChartForExpense()
        }
    }

    private fun setupPieChart() {
        if (isIncomeSelected) {
            setupPieChartForIncome()
        } else {
            setupPieChartForExpense()
        }
    }

    private fun setupBarChart() {
        if (isIncomeSelected) {
            setupBarChartForIncome()
        } else {
            setupBarChartForExpense()
        }
    }

    private fun setupPieChartForExpense() {
        viewModel.allExpenses.observe(this, { expenses ->
            val categories = mutableMapOf<Int, Float>()

            // Sum expenses by category
            expenses.forEach { expense ->
                val currentAmount = categories[expense.categoryId] ?: 0f
                categories[expense.categoryId] = currentAmount + expense.amount.toFloat()
            }

            viewModel.allCategories.observe(this, { categoryMap ->
                // Convert map to PieEntries with category names
                val pieEntries = categories.map { (categoryId, amount) ->
                    val categoryName = categoryMap[categoryId]?.name ?: "Unknown"
                    PieEntry(amount, categoryName)
                }

                val pieDataSet = PieDataSet(pieEntries, " ")
                pieDataSet.colors = allColors
                val pieData = PieData(pieDataSet)
                pieData.setValueFormatter(PercentFormatter(pieChart)) // Add percentage formatter
                pieChart.data = pieData
                pieChart.invalidate()

                pieChart.description.isEnabled = false
                pieChart.setDrawEntryLabels(false)
                pieChart.setUsePercentValues(true)
                pieChart.setCenterText("Expenses")
                pieChart.setCenterTextSize(16f)
                pieChart.holeRadius = 45f
                pieChart.transparentCircleRadius = 50f

                val legend = pieChart.legend
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.textSize = 12f
                legend.isWordWrapEnabled = true // Enable word wrap for legend labels
                legend.maxSizePercent = 0.9f // Adjust max size percentage as needed
            })
        })
    }


    private fun setupBarChartForExpense() {
        viewModel.allExpenses.observe(this, { expenses ->
            val categories = mutableMapOf<Int, Float>()

            // Sum expenses by category
            expenses.forEach { expense ->
                val currentAmount = categories[expense.categoryId] ?: 0f
                categories[expense.categoryId] = currentAmount + expense.amount.toFloat()
            }

            viewModel.allCategories.observe(this, { categoryMap ->
                // Convert map to a list of entries
                val categoryEntries = categories.entries.toList()

                // Now use mapIndexed on the list
                val barEntries =
                    categoryEntries.mapIndexed { index: Int, entry: Map.Entry<Int, Float> ->
                        val categoryName = categoryMap[entry.key]?.name ?: "Unknown"
                        BarEntry(index.toFloat(), entry.value, categoryName)
                    }

                val barDataSet = BarDataSet(barEntries, "Categories")
                barDataSet.colors = allColors
                val barData = BarData(barDataSet)
                barChart.data = barData
                barChart.invalidate()

                barChart.description.isEnabled = false
                barChart.setDrawValueAboveBar(true)
                barChart.setFitBars(true)

                barChart.setVisibleXRange(0f, 5f)
                barChart.setPinchZoom(true)
                barChart.isDoubleTapToZoomEnabled = false

                val legend = barChart.legend
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.textSize = 12f

                barChart.xAxis.apply {
                    granularity = 1f
                    setDrawGridLines(false)
                    setLabelCount(barEntries.size, false)
                    valueFormatter = IndexAxisValueFormatter(categoryEntries.map {
                        categoryMap[it.key]?.name ?: "Unknown"
                    })
                }
            })
        })
    }


    private fun setupPieChartForIncome() {
        viewModel.allIncomes.observe(this, { incomes ->
            val categories = mutableMapOf<Int, Float>()

            // Sum incomes by category
            incomes.forEach { income ->
                val currentAmount = categories[income.categoryId] ?: 0f
                categories[income.categoryId] = currentAmount + income.amount.toFloat()
            }

            viewModel.allCategories.observe(this, { categoryMap ->
                // Convert map to PieEntries with category names
                val pieEntries = categories.map { (categoryId, amount) ->
                    val categoryName = categoryMap[categoryId]?.name ?: "Unknown"
                    PieEntry(amount, categoryName)
                }

                val pieDataSet = PieDataSet(pieEntries, " ")
                pieDataSet.colors = allColors
                val pieData = PieData(pieDataSet)
                pieData.setValueFormatter(PercentFormatter(pieChart)) // Add percentage formatter
                pieChart.data = pieData
                pieChart.invalidate()

                pieChart.description.isEnabled = false
                pieChart.setDrawEntryLabels(false)
                pieChart.setUsePercentValues(true)
                pieChart.setCenterText("Income")
                pieChart.setCenterTextSize(16f)
                pieChart.holeRadius = 45f
                pieChart.transparentCircleRadius = 50f

                val legend = pieChart.legend
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.textSize = 12f
                legend.isWordWrapEnabled = true // Enable word wrap for legend labels
                legend.maxSizePercent = 0.9f // Adjust max size percentage as needed
            })
        })
    }


    private fun setupBarChartForIncome() {
        viewModel.allIncomes.observe(this, { incomes ->
            val categories = mutableMapOf<Int, Float>()

            // Sum incomes by category
            incomes.forEach { income ->
                val currentAmount = categories[income.categoryId] ?: 0f
                categories[income.categoryId] = currentAmount + income.amount.toFloat()
            }

            viewModel.allCategories.observe(this, { categoryMap ->
                // Convert map to a list of entries
                val categoryEntries = categories.entries.toList()

                // Now use mapIndexed on the list
                val barEntries =
                    categoryEntries.mapIndexed { index: Int, entry: Map.Entry<Int, Float> ->
                        val categoryName = categoryMap[entry.key]?.name ?: "Unknown"
                        BarEntry(index.toFloat(), entry.value, categoryName)
                    }

                val barDataSet = BarDataSet(barEntries, "Categories")
                barDataSet.colors = allColors
                val barData = BarData(barDataSet)
                barChart.data = barData
                barChart.invalidate()

                barChart.description.isEnabled = false
                barChart.setDrawValueAboveBar(true)
                barChart.setFitBars(true)

                val legend = barChart.legend
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.textSize = 12f

                barChart.xAxis.apply {
                    granularity = 1f
                    setDrawGridLines(false)
                    setLabelCount(barEntries.size, false)
                    valueFormatter = IndexAxisValueFormatter(categoryEntries.map {
                        categoryMap[it.key]?.name ?: "Unknown"
                    })
                }
            })
        })
    }
}
