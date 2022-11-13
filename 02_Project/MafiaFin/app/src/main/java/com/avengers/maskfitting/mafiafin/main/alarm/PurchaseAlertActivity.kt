package com.avengers.maskfitting.mafiafin.main.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityMaskPurchaseDetailBinding
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.databinding.MaskAlertCustomListItemBinding
import kotlinx.android.synthetic.main.activity_mask_purchase_detail.*
import org.json.JSONObject
import java.time.LocalDate

class PurchaseAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaskPurchaseDetailBinding

    //푸시 알림 채널 생성
    private val CHANNEL_ID = "testChannel01"   // Channel for notification
    private var notificationManager: NotificationManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaskPurchaseDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        // 구매하기 버튼
        binding.shoppingBtn.setOnClickListener {
            val intentShop = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://search.shopping.naver.com/search/all?query=" + binding.maskName.text)
            )
            startActivity(intentShop)
        }

        // 삭제 버튼 / 정보 삭제
        binding.DeleteBtn.setOnClickListener {
            val responseListener: Response.Listener<String?> =
                Response.Listener { response ->
                    try {
                        var jsonObject = JSONObject(response)
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            Toast.makeText(this, "마스크 정보를 삭제했습니다.", Toast.LENGTH_LONG).show()

                            // 메인 화면으로 전환
                            val intent = Intent(this, MaskAlertMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "마스크 정보 삭제를 실패했습니다.", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                    }
                }

            val maskDeleterRequest = MaskDeleteRequest(
                binding.maskNickname.text.toString(),
                responseListener
            )
            val queue: RequestQueue = Volley.newRequestQueue(this)
            queue.add(maskDeleterRequest)
        }

        // 확인 버튼
        binding.CloseBtn.setOnClickListener {
            Log.d("count", count.toString())
            if (setAlert.isChecked()) {
                editAlert = 1
            }                                                   // 알람 스위치 setting
            else {
                editAlert = 0
            }
            println(editAlert)

            // 메인 화면으로 전환
            val intent = Intent(this, MaskAlertMainActivity::class.java)
            startActivity(intent)
        }

        //var alert = ""
        //var count = 0
        var maskType = ""

        var maskNickname = binding.maskNickname
        var maskName = binding.maskName
        var purchaseDate = binding.BeforePurchase
        var maskImage = binding.maskImage
        var setAlert = binding.setAlert
        if (intent.hasExtra("maskNickname") && intent.hasExtra("maskName") && intent.hasExtra("purchaseDate")
            && intent.hasExtra("count") && intent.hasExtra("maskType") && intent.hasExtra("setAlert")
        ) {
            maskNickname.text = intent.getStringExtra("maskNickname")
            maskName.text = intent.getStringExtra("maskName")
            purchaseDate.text = intent.getStringExtra("purchaseDate")
            maskType = intent.getStringExtra("maskType").toString()
            Log.d("마스크 타입", maskType)
            binding.Counter.text = intent.getStringExtra("count")
            alert = intent.getStringExtra("setAlert").toString()
            Log.d("알림설정", alert)
        }                                                                                               // intent 값 받아오기
        else {
            Toast.makeText(this, "데이터가 정상적으로 전달되지 않아 이전 화면으로 복귀합니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MaskAlertMainActivity::class.java)
            startActivity(intent)
        }

        count = Integer.parseInt(binding.Counter.text as String)
        if (maskType == "덴탈 마스크") {
            maskImage.setImageResource(R.drawable.dental)
        }                  // 마스크 이미지 출력하기
        else if (maskType == "KF 80" || maskType == "KF 94") {
            maskImage.setImageResource((R.drawable.kf))
        }

        if (alert != "") {
            setAlert.setChecked(true)
        }                                                                              // 알람 스위치 setting
        else if (alert == "") {
            setAlert.setChecked(false)
        }

        binding.wearBtn.setOnClickListener {                                           // '착용완료' 버튼 클릭 시
            if (count > 0) {
                count--
            } else {
                count = 0
            }                                                                          // 마스크 수량 차감
            binding.Counter.text = count.toString()
        }

        // 구매 예정일 계산
        val cal = Calendar.getInstance()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")

        try {
            cal.time = df.parse(binding.BeforePurchase.text as String?)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        cal.add(Calendar.DATE, count)

        binding.AfterPurchase.text = df.format(cal.time)

        // 알림 호출
        createNotificationChannel(CHANNEL_ID, "testChannel", "this is a test Channel")
        if (binding.AfterPurchase.text == LocalDate.now().toString() && setAlert.isChecked) {
            displayNotification()
        }
    }

    //알람 설정
    @RequiresApi(Build.VERSION_CODES.O)
    fun displayNotification() {         // 푸시알림 시각적 배치 및 텍트
        val notificationId = 45

        val notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.dental)
            .setContentTitle("마스크 재구매 알리미")
            .setContentText(binding.maskNickname.text)
            .build()

        notificationManager?.notify(notificationId, notification)
    }

    fun createNotificationChannel(channelId: String, name: String, channelDescription: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT // set importance
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = channelDescription
            }
            // Register the channel with the system
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        private var count = 0                 // 마스크 수량 변수
        private var editAlert = 0
        private var alert = ""
        private const val URL =
            "http://43.200.115.71/MaskEdit.php"     // "http:// 퍼블릭 DNS 주소/MaskEdit.php"
        private const val url = "http://43.200.115.71/MaskDelete.php"
    }
}
