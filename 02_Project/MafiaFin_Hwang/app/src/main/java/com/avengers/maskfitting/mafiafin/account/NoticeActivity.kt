package com.avengers.maskfitting.mafiafin.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.avengers.maskfitting.mafiafin.databinding.ActivityNoticeBinding
import com.avengers.maskfitting.mafiafin.faceshape.FaceShapeActivity
import com.avengers.maskfitting.mafiafin.personal_color.PersonalColorActivity

class NoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시작버튼 터치 시 얼굴형 분석 실행
        binding.startBtn.setOnClickListener {
            val intent = Intent(this, FaceShapeActivity::class.java)
            startActivity(intent)
        }

        // 다음으로 버튼 터치 시 퍼스널 컬러 분석 실행
        binding.passBtn.setOnClickListener {
            val intent = Intent(this, PersonalColorActivity::class.java)
            startActivity(intent)
        }
    }
}