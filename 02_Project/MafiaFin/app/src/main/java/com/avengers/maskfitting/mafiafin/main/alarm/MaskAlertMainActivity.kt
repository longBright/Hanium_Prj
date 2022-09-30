package com.avengers.maskfitting.mafiafin.main.alarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.android.volley.Request
import kotlinx.android.synthetic.main.activity_mask_alert_list.*
import org.json.JSONException
import org.json.JSONObject
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityMaskAlertListBinding


class MaskAlertMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaskAlertListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaskAlertListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 마스크 등록 버튼
        binding.editMask.setOnClickListener {
            val intent = Intent(this, MaskRegisterationActivity::class.java)
            startActivity(intent)
        }

        // make text view content scrollable
        textView.movementMethod = ScrollingMovementMethod()

        val url = "http://43.200.115.71/MaskData.php"
//        val nameArr = ArrayList<String>()

        // get json object from url using volley network library
        progressBar.visibility = View.VISIBLE

        var maskNickname = ""
        var maskName = ""
        var maskImage = ""
        var alert = ""
        // request json object response from the provided url
        val request = JsonObjectRequest(
            Request.Method.GET, // method
            url, // url
            null, // json request
            { response -> // response listener
                try {
                    val obj: JSONObject = response
                    val array = obj.getJSONArray("response")

//                    var adapter = ArrayAdapter(this, R.layout.simple_list_item_1, nameArr)
//                    listView.adapter= adapter
                    val items = mutableListOf<ListViewItem>()

                    val adapter = ListViewAdapter(items)
                    listView.adapter = adapter

                    textView.text = ""
                    // loop through the array elements
                    for (i in 0 until  array.length()){
                        // get current json object as student instance
                        val maskData: JSONObject = array.getJSONObject(i)
                        // get the current student (json object) data
                        maskNickname = maskData.getString("mask_nickname")          // 마스크 별명
                        maskName = maskData.getString("mask_name")                  // 마스크 품명
                        maskImage = maskData.getString("mask_type")                 // 마스크 타입 이미지
                        alert = maskData.getInt("set_alert").toString()             // 마스크 재구매 알림 설정 여부
                        //display the formatted json data in text view
                        //textView.append("$maskNickname\n $maskName\n\n")

                        if (alert == "1") { alert = "🔔" }                                 // 알림 설정 햇다면, 이모지 출력
                        else if (alert == "0") { alert = "" }                             // 알림 설정을 안했다면, 공백 출력

                        if (maskImage == "덴탈 마스크") {                                   // 덴탈 마스크 타입이라면 덴탈 이미지 출력
                            items.add(
                                ListViewItem(
                                    ContextCompat.getDrawable(
                                        this,
                                        R.drawable.dental
                                    )!!, maskNickname, maskName, alert
                                )
                            )
                        } else if (maskImage == "KF 80" || maskName == "KF 94") {          // kf 마스크 타입이라면 kf 마스크 이미지 출력
                            items.add(
                                ListViewItem(
                                    ContextCompat.getDrawable(
                                        this,
                                        R.drawable.kf
                                    )!!, maskNickname, maskName, alert
                                )
                            )
                        }
//                        nameArr.add(maskNickname)

                        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                            val intent = Intent(this, PurchaseAlertActivity::class.java)
                            intent.putExtra("maskNickname", maskNickname) //ListViewItem.get(position).title
                            intent.putExtra("maskName", maskName)
                            startActivity(intent)
                        }
                        adapter.notifyDataSetChanged()   //변경내용 반영
                    }
                }catch (e: JSONException){
                    textView.text = e.message
                }
                progressBar.visibility = View.INVISIBLE
            },
            { error -> // error listener
                textView.text = error.message
                progressBar.visibility = View.INVISIBLE
            }
        )
        // add network request to volley queue
        VolleySingleton.getInstance(applicationContext)
            .addToRequestQueue(request)

//        binding.listView.setOnItemClickListener { parent, view, position, id ->
//            val intent = Intent(this, PurchaseAlert::class.java)
//            startActivity(intent)
//        }
    }
}