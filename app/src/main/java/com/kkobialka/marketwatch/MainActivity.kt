package com.kkobialka.marketwatch

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import kotlin.math.roundToInt

@SuppressLint("StaticFieldLeak")
private lateinit var textSymbol: TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = this.resources.getColor(R.color.accent)

        textSymbol = findViewById(R.id.text_symbol)

        getStockInfo()

    }

    private fun getStockInfo() {
        val baseUrl = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=NVDA&apikey="
        val apiKey = Common.ALPHA_API_KEY
        val url = "$baseUrl$apiKey"

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {

                if(response != null){

                    Toast.makeText(this, "Response is not null", LENGTH_SHORT).show()

                    val symbol = response.getString("Symbol")

                    textSymbol.text = symbol
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