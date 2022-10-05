package com.avengers.maskfitting.mafiafin.personal_color

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.account.RegisterActivity
import com.avengers.maskfitting.mafiafin.databinding.ActivityPersonalColorOutputBinding
import com.avengers.maskfitting.mafiafin.main.fitting.FittingActivity

class PersonalColorOutputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalColorOutputBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPersonalColorOutputBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        var personalColorResult = intent.getStringExtra("PersonalColor").toString() // 숫자
        val faceShapeResult = intent.getStringExtra("FaceShape").toString() // 숫자
        Log.d("퍼스널컬러 진단 결과", personalColorResult)

        binding.personalColor.text = personalColorResult

        when (personalColorResult) {
            "1" -> {
                binding.personalColor.text = "봄 웜톤 입니다"
                binding.personalColorImg.setImageResource(R.drawable.springwarm)
            }
            "2" -> {
                binding.personalColor.text = "가을 웜톤 입니다"
                binding.personalColorImg.setImageResource(R.drawable.autumnwarm)
            }
            "3" -> {
                binding.personalColor.text = "여름 쿨톤 입니다"
                binding.personalColorImg.setImageResource(R.drawable.summercool)
            }
            "4" -> {
                binding.personalColor.text = "겨울 쿨톤 입니다"
                binding.personalColorImg.setImageResource(R.drawable.wintercool)
            }
            else -> binding.personalColor.text = "진단된 퍼스널 컬러가 없습니다"
        }

        binding.btnFinish.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            when (personalColorResult) {
                "1" -> intent.putExtra("PersonalColor", "봄 웜톤")
                "2" -> intent.putExtra("PersonalColor", "가을 웜톤")
                "3" -> intent.putExtra("PersonalColor", "여름 쿨톤")
                "4" -> intent.putExtra("PersonalColor", "겨울 쿨톤")
                else -> intent.putExtra("PersonalColor", "선택안함")
            }
            when (faceShapeResult) {
                "0" -> intent.putExtra("FaceShape", "하트형")
                "1" -> intent.putExtra("FaceShape", "계란형")
                "2" -> intent.putExtra("FaceShape", "둥근형")
                "3" -> intent.putExtra("FaceShape", "사각형")
                else -> intent.putExtra("FaceShape", "선택안함")
            }
            startActivity(intent)
            this.finish()
        }
    }
}