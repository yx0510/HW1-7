package com.italkutalk.lab17

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var btn_query: Button
    //定義資料結構存放 Server 回傳的資料
    class MyObject {
        lateinit var result: Result
        class Result {
            lateinit var records: Array<Record>
            class Record {
                var SiteName = ""
                var Status = ""
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_query = findViewById(R.id.btn_query)
        btn_query.setOnClickListener {
            //關閉按鈕避免再次查詢
            btn_query.isEnabled = false
            //發送請求
            sendRequest()
        }
    }
    //發送請求
    private fun sendRequest() {
        val url = "https://api.italkutalk.com/api/airhttps://api.italkutalk.com/api/air"

        //建立 Request.Builder 物件，藉由 url()將網址傳入，再建立 Request 物件
        val req = Request.Builder()
            .url(url)
            .build()
        //建立 OkHttpClient 物件，藉由 newCall()發送請求，並在 enqueue()接收回傳
        OkHttpClient().newCall(req).enqueue(object : Callback {
            //發送成功執行此方法
            override fun onResponse(call: Call, response: Response) {
                //使用 response.body?.string()取得 JSON 字串
                val json = response.body?.string()
                //建立 Gson 並使用其 fromJson()方法，將 JSON 字串以 MyObject 格式輸出
                val myObject = Gson().fromJson(json, MyObject::class.java)
                //顯示結果
                showDialog(myObject)
            }
            //發送失敗執行此方法
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    //開啟按鈕可再次查詢
                    btn_query.isEnabled = true
                    Toast.makeText(this@MainActivity,
                        "查詢失敗$e", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    //顯示結果
    private fun showDialog(myObject: MyObject) {
        //建立一個字串陣列，用於存放 SiteName 與 Status 資訊
        val items = arrayOfNulls<String>(myObject.result.records.size)
        //將 API 資料取出並建立字串，並存放到字串陣列
        myObject.result.records.forEachIndexed { index, data ->
            items[index] = "地區：${data.SiteName}, 狀態：${data.Status}"
        }
        //切換到主執行緒將畫面更新
        runOnUiThread {
            //開啟按鈕可再次查詢
            btn_query.isEnabled = true
            //建立 AlertDialog 物件並顯示字串陣列
            AlertDialog.Builder(this@MainActivity)
                .setTitle("臺北市空氣品質")
                .setItems(items, null)
                .show()
        }
    }
}