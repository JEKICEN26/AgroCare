package com.zaky.agrocare.ui.education

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.zaky.agrocare.R
import com.zaky.agrocare.databinding.FragmentGrafikprediksiBinding

class EducationModuleFragment : Fragment() {

    private var _binding: FragmentGrafikprediksiBinding? = null
    private val binding get() = _binding!!

    // Mock Data based on typical BPS fluctuation for 6 months (Jan-Jun)
    private val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun")
    
    private val mockData = mapOf(
        "Bawang Merah" to listOf(35000f, 32000f, 28000f, 30000f, 40000f, 45000f),
        "Cabai Rawit" to listOf(60000f, 75000f, 90000f, 80000f, 50000f, 45000f),
        "Tomat" to listOf(15000f, 14000f, 10000f, 8000f, 12000f, 18000f),
        "Bawang Putih" to listOf(38000f, 39000f, 40000f, 38000f, 42000f, 43000f)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGrafikprediksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupSpinner()
        setupChart()
        
        // Initial load
        updateChartData("Bawang Merah")
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSpinner() {
        val commodities = mockData.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, commodities)
        binding.spinnerCommodity.adapter = adapter
        
        binding.spinnerCommodity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = commodities[position]
                updateChartData(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupChart() {
        val lineChart = binding.lineChart
        
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        
        lineChart.axisRight.isEnabled = false
        
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        
        val yAxis = lineChart.axisLeft
        yAxis.setDrawGridLines(true)
        yAxis.axisMinimum = 0f
        
        lineChart.animateX(1000)
    }

    private fun updateChartData(commodity: String) {
        val prices = mockData[commodity] ?: return
        
        val entries = ArrayList<Entry>()
        for (i in prices.indices) {
            entries.add(Entry(i.toFloat(), prices[i]))
        }
        
        val dataSet = LineDataSet(entries, "Harga $commodity (Rp/Kg)")
        val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        
        dataSet.color = colorPrimary
        dataSet.setCircleColor(colorPrimary)
        dataSet.lineWidth = 2.5f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 10f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = colorPrimary
        dataSet.fillAlpha = 50
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate() // refresh
        binding.lineChart.animateX(800)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
