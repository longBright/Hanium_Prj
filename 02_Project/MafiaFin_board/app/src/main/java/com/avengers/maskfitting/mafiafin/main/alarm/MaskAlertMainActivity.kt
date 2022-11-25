package com.avengers.maskfitting.mafiafin.main.alarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import kotlinx.android.synthetic.main.activity_mask_alert_list.*
import org.json.JSONException
import org.json.JSONObject
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityMaskAlertListBinding
import com.avengers.maskfitting.mafiafin.main.MainActivity
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import android.content.SharedPreferences
import android.text.Editable
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn


class MaskAlertMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaskAlertListBinding
    private lateinit var preferences: SharedPreferences
    private var email: String? = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaskAlertListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val account = this.let { GoogleSignIn.getLastSignedInAccount(it) }
        if (account == null) {
            // 로그인 중인 사용자 정보 획득
            // preferences를 통해 userEmail 값 전달 받음
            preferences = getSharedPreferences("userEmail", MODE_PRIVATE)
            email = preferences.getString("email", "").toString()
            //Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        }
        // 구글 회원
        else {
            email = account?.email      // 사용자 이메일 초기화
            //Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        }

        // 마스크 등록 버튼
        binding.editMask.setOnClickListener {
            val intent = Intent(this, MaskRegisterationActivity::class.java)
            startActivity(intent)
        }

        // 메인 화면으로 돌아가는 버튼 / '메인으로' 버튼
        binding.ReturnBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // make text view content scrollable
        textView.movementMethod = ScrollingMovementMethod()

        // get json object from url using volley network library
        progressBar.visibility = View.VISIBLE

        var maskNickname = ""
        var maskName = ""
        var maskType = ""
        var alert = ""
        var purchaseDate = ""
        var count = ""
        // request json object response from the provided url
        val responseListener: Response.Listener<String?> =
            Response.Listener { response ->
                try {
                    var jsonObject = JSONObject(response)
                    var array = jsonObject.getJSONArray("response")

                    val items = mutableListOf<ListViewItem>()
                    val adapter = ListViewAdapter(items)
                    listView.adapter = adapter

                    textView.text = ""
                    // loop through the array elements
                    for (i in 0 until array.length()) {
                        // get current json object as student instance
                        val maskData: JSONObject = array.getJSONObject(i)
                        // get the current student (json object) data
                        maskNickname = maskData.getString("mask_nickname")          // 마스크 별명
                        maskName = maskData.getString("mask_name")                  // 마스크 품명
                        maskType =
                            maskData.getString("mask_type")                  // 마스크 타입 이미지
                        alert =
                            maskData.getInt("set_alert")
                                .toString()             // 마스크 재구매 알림 설정 여부
                        purchaseDate = maskData.getString("purchase_date")          // 마스크 구매 일자
                        count = maskData.getInt("mask_count").toString()            // 마스크 수량
                        //display the formatted json data in text view

                        if (alert == "1") {
                            alert = "🔔"
                        }                                 // 알림 설정 햇다면, 이모지 출력
                        else if (alert == "0") {
                            alert = ""
                        }                             // 알림 설정을 안했다면, 공백 출력

                        if (maskType == "덴탈 마스크") {                                    // 덴탈 마스크 타입이라면 덴탈 이미지 출력
                            items.add(
                                ListViewItem(
                                    ContextCompat.getDrawable(
                                        this,
                                        R.drawable.dental
                                    )!!,
                                    maskNickname,
                                    maskName,
                                    alert,
                                    purchaseDate,
                                    count,
                                    maskType
                                )
                            )
                        } else if (maskType == "KF 80" || maskType == "KF 94") {          // kf 마스크 타입이라면 kf 마스크 이미지 출력
                            items.add(
                                ListViewItem(
                                    ContextCompat.getDrawable(
                                        this,
                                        R.drawable.kf
                                    )!!,
                                    maskNickname,
                                    maskName,
                                    alert,
                                    purchaseDate,
                                    count,
                                    maskType
                                )
                            )
                        }

                        // 클릭 시, 상세 조회 화면 전환
                        binding.listView.setOnItemClickListener { parent, view, position, id ->
                            val intent = Intent(this, PurchaseAlertActivity::class.java)
                            startActivity(intent)
                        }

                        // 상세 조회로 이동 / 값 intent 전달
                        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                            val intent = Intent(this, PurchaseAlertActivity::class.java)
                            intent.putExtra("maskNickname", items[position].title)
                            intent.putExtra("maskName", items[position].subTitle)
                            intent.putExtra("purchaseDate", items[position].purchaseDate)
                            intent.putExtra("count", items[position].count)
                            intent.putExtra("maskType", items[position].maskType)
                            intent.putExtra("setAlert", items[position].setAlert)
                            startActivity(intent)
                        }
                        adapter.notifyDataSetChanged()   //변경내용 반영
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    textView.text = e.message
                }
                progressBar.visibility = View.INVISIBLE
            }
        val maskDataListRequest = email?.let {
            MaskDataListRequest(
                it,
                responseListener
            )
        }

        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(maskDataListRequest)
    }
}