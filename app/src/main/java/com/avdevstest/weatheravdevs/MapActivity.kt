package com.avdevstest.weatheravdevs

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_map.*
import java.util.*

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this@MapActivity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.color_top)
        }
        setBarChart()
        iv_back.setOnClickListener() {
            finish()
        }
    }

    /**Set local data to bar chart*/
    private fun setBarChart() {


        val allEntries: Map<String, String> =
            SharedPrefManager.getInstance(baseContext).getAllData()

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        var i: Int = 0
        for ((key, value) in allEntries) {
            entries.add(BarEntry(value.replace("°C","").toFloat(), i))
            labels.add(key)
            i = i + 1
        }
        val barDataSet = BarDataSet(entries, "Date vs Temperature")

        val data = BarData(labels, barDataSet)
        barChart.data = data // set the data and list of lables into chart

        barChart.setDescription("This Chart is shows saved date-time with its temperature")
        barChart.setDescriptionColor(R.color.color_gray)

        barDataSet.color = resources.getColor(R.color.color_top)
        barChart.animateY(5000)
    }
}
