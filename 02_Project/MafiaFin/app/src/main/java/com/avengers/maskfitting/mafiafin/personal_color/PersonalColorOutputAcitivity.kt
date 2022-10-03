package com.avengers.maskfitting.mafiafin.personal_color

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.avengers.maskfitting.mafiafin.databinding.ActivityPersonalColorOutputBinding
import com.avengers.maskfitting.mafiafin.main.fitting.FittingActivity

class PersonalColorOutputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalColorOutputBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPersonalColorOutputBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        var result = intent.getStringExtra("FaceShape").toString()
        Log.d("퍼스널컬러 진단 결과", result)

        binding.personalColor.text = result

        when (result) {
            "1" -> binding.personalColor.text = "봄 웜톤 입니다"
            "2" -> binding.personalColor.text = "가을 웜톤 입니다"
            "3" -> binding.personalColor.text = "여름 쿨톤 입니다"
            "4" -> binding.personalColor.text = "겨울 쿨톤 입니다"
            else -> binding.personalColor.text = "진단된 퍼스널 컬러가 없습니다"

        }
//        binding.btnFinish.setOnClickListener {
        //어디로 연결할지 정해야함
//            var intent = Intent(this, ::class.java)
//            startActivity(intent)
//            this.finish()
//        }
    }
}