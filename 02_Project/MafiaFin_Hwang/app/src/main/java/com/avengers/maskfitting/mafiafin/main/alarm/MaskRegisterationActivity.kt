package com.avengers.maskfitting.mafiafin.main.alarm

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mask_registeration.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityMaskRegisterationBinding
import org.json.JSONObject

class MaskRegisterationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaskRegisterationBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaskRegisterationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 마스크 종류/타입 스피너
        val itemList = listOf("마스크 타입을 고르세요.", "덴탈 마스크", "KF 80", "KF 94")
        val adapter = ArrayAdapter(this, R.layout.activity_mask_registeration, itemList)
        binding.maskType.adapter = adapter
        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        maskType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemList)
        //아이템 선택 리스너
        maskType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (position) {
                    0 -> {
                        // default 마스크 이미지 설정
                    }
                    1 -> {
                        binding.maskImage.setImageResource(R.drawable.dental)                   // 덴탈 마스크 이미지
                    }
                    2 -> {
                        binding.maskImage.setImageResource(R.drawable.kf)                       // KF 마스크 이미지
                    }
                    3 -> {
                        binding.maskImage.setImageResource(R.drawable.kf)                       // KF 마스크 이미지
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {                               // 선택을 안했을 경우
                //println("마스크 타입을 고르세요.")
            }
        }

        // 마스크 구매일 기록 기능/ 캘린터 버튼 구현
        var dateString = ""
        binding.CalendarBtn.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                dateString = "${year}-${month+1}-${dayOfMonth}"
                beforePurchaseDate.text = dateString
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 재구매 시기 알림 스위치
        var alert = 0
        setAlert.setOnCheckedChangeListener{CompoundButton, onSwitch ->
            //  스위치가 켜지면
            if (onSwitch){
                Toast.makeText(this, "알람 On", Toast.LENGTH_SHORT).show()
                alert = 1
            }
            //  스위치가 꺼지면
            else{
                Toast.makeText(this, "알람 Off", Toast.LENGTH_SHORT).show()
                alert = 0
            }
        }

        // 뒤로가기 버튼
        binding.returnBtn.setOnClickListener {
            val intent = Intent(this, MaskAlertMainActivity::class.java)
            startActivity(intent)
        }

        // 마스크 등록 기능/ 등록 버튼 구현
        binding.maskRegisterationBtn.setOnClickListener { view ->
            // 빈 값 있을 경우
            if (binding.maskName.text.isEmpty()
                || binding.maskNickname.text.isEmpty()
                || binding.beforePurchaseDate.text.isEmpty()
                || binding.maskCounter.text.isEmpty()
                || binding.maskType.selectedItem == itemList[0]) {
                Toast.makeText(this, "값을 전부 입력해주세요.", Toast.LENGTH_LONG).show()
            }
            // 다 입력 되었을 경우
            else {
                val responseListener: Response.Listener<String?> =
                    Response.Listener { response ->
                        try {
                            var jsonObject = JSONObject(response)
                            val success = jsonObject.getBoolean("success")
                            if (success) {
                                Toast.makeText(this, "마스크 등록 성공.", Toast.LENGTH_LONG).show()
                                // 메인 화면으로 전환
                                val intent = Intent(this, MaskAlertMainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "마스크 등록 실패.", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                        }
                    }

                val maskRegisterRequest = MaskRegisterRequest(
                    binding.maskName.text.toString(),
                    binding.maskNickname.text.toString(),
                    binding.beforePurchaseDate.text.toString(),
                    binding.maskCounter.text,
                    binding.maskType.selectedItem.toString(),
                    alert,
                    responseListener
                )
                val queue: RequestQueue = Volley.newRequestQueue(this)
                queue.add(maskRegisterRequest)
            }
        }
    }
}
