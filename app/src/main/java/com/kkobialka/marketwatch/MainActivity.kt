package com.kkobialka.marketwatch

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
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
import com.squareup.picasso.Picasso
import org.json.JSONException


@SuppressLint("StaticFieldLeak")
private lateinit var textSymbol: TextView
private lateinit var barChart: AnyChartView
private lateinit var topLinearLayout: LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = this.resources.getColor(R.color.accent)

        textSymbol = findViewById(R.id.text_symbol)
        barChart = findViewById(R.id.bar_chart)
        topLinearLayout = findViewById(R.id.top_layout)

        getNewsFeed()

        getStockInfo()

    }

    private fun getNewsFeed() {
        //API

        val baseUrl = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&topics=technology&apikey="
        val apiKey = Common.ALPHA_API_KEY
        val url = "$baseUrl$apiKey"

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {

                if (response != null) {

                    val feed = response.getJSONArray("feed")

                    for (i in 0 until feed.length()){
                        val news = feed.getJSONObject(i)
                        val title = news.getString("title")
                        val summary = news.getString("summary")
                        val bannerImage = news.getString("banner_image")
                        val sentimentLevel = news.getString("overall_sentiment_label")

                        //Card layout
                        val cardLinearLayout = LinearLayout(this)

                        cardLinearLayout.orientation = LinearLayout.VERTICAL

                        val params = RelativeLayout.LayoutParams(
                            500,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        params.setMargins(40, 40, 20, 40)

                        val cardView = CardView(this)
                        cardView.radius = 40f
                        cardView.setCardBackgroundColor(Color.parseColor("#F9A825"))
                        cardView.layoutParams = params
                        cardView.cardElevation = 40f

                        val topImage = ImageView(this)

                        val imageParams = RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            330
                        )

                        topImage.layoutParams = imageParams

                        if(bannerImage.length > 5){
                            Picasso.get()
                                .load(bannerImage)
                                .resize(300, 150)
                                .centerInside()
                                .into(topImage)
                        }

                        val typeface = ResourcesCompat.getFont(this, R.font.maven_pro)

                        val sentiment = TextView(this)
                        sentiment.text = sentimentLevel
                        sentiment.textSize = 10f
                        sentiment.setTextColor(Color.parseColor("#fc0335"))
                        sentiment.setTypeface(typeface)

                        val quote = TextView(this)
                        quote.text = title
                        quote.textSize = 12f
                        quote.setTextColor(Color.WHITE)
                        quote.setTypeface(typeface, Typeface.BOLD)

                        val name = TextView(this)
                        name.text = summary
                        name.textSize = 10f
                        name.setTextColor(Color.parseColor("#E0F2F1"))
                        name.setTypeface(typeface, Typeface.ITALIC)

                        cardLinearLayout.addView(sentiment)
                        cardLinearLayout.addView(topImage)
                        cardLinearLayout.addView(quote)
                        cardLinearLayout.addView(name)
                        cardView.addView(cardLinearLayout)

                        topLinearLayout.addView(cardView)
                    }

                } else {
                   Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show()
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })
        Volley.newRequestQueue(this).add(request)

    }

    private fun getStockInfo() {
        val baseUrl = "https://www.alphavantage.co/query?function=INFLATION&apikey="
        val apiKey = Common.ALPHA_API_KEY
        val url = "$baseUrl$apiKey"

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {

                if (response != null) {

                    Toast.makeText(this, "Response is not null", LENGTH_SHORT).show()

                    val data: MutableList<DataEntry> = ArrayList()

                    val inflationData = response.getJSONArray("data")

                    for (i in 0 until inflationData.length()) {
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

                } else {
                    textSymbol.text = "Null"
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })
        Volley.newRequestQueue(this).add(request)
    }
}