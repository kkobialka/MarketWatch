package com.kkobialka.marketwatch

import android.annotation.SuppressLint
import android.icu.text.Transliterator
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.TooltipPositionMode
import org.json.JSONException


@SuppressLint("StaticFieldLeak")
private lateinit var textSymbol: TextView
private lateinit var barChart: AnyChartView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = this.resources.getColor(R.color.accent)

        textSymbol = findViewById(R.id.text_symbol)
        barChart = findViewById(R.id.bar_chart)

        getStockInfo()

    }

    private fun getStockInfo() {
        val baseUrl = "https://www.alphavantage.co/query?function=INFLATION&apikey="
        val apiKey = Common.ALPHA_API_KEY
        val url = "$baseUrl$apiKey"

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {

                if(response != null){

                    Toast.makeText(this, "Response is not null", LENGTH_SHORT).show()

                    val data: MutableList<DataEntry> = ArrayList()

                    val inflationData = response.getJSONArray("data")

                    for (i in 0 until inflationData.length()){
                        val rate = inflationData.getJSONObject(i)
                        val date = rate.getString("date")
                        val value = rate.getString("value")

                        data.add(ValueDataEntry(date, value.toDouble()))
                    }

                    val cartesian = AnyChart.column()

                    val column: Column = cartesian.column(data)

                    column.tooltip()
                        .titleFormat("{%X}")
                        .anchor(Anchor.CENTER_BOTTOM)
                        .offsetX(0.0)
                        .offsetY(5.0)
                        .format("\${%Value}{groupsSeparator: }")

                    cartesian.animation(true)
                    cartesian.title("Annual inflation in the US")

                    cartesian.yScale().minimum(0.0)

                    cartesian.yAxis(0).labels().format("\${%Value}{groupsSeparator: }")

                    cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
                    cartesian.interactivity().hoverMode(HoverMode.BY_X)

                    cartesian.xAxis(0).title("Year")
                    cartesian.yAxis(0).title("Inflation rate")

                    cartesian.background("#212832")

                    barChart.setChart(cartesian)

                }else{
                    textSymbol.text = "Null"
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })
        Volley.newRequestQueue(this).add(request)
    }
}