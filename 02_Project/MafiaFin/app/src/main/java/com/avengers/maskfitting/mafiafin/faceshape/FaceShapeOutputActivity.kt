package com.avengers.maskfitting.mafiafin.faceshape

import androidx.appcompat.app.AppCompatActivity
import android.R
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Build
import android.util.Log
import android.view.Window
import com.avengers.maskfitting.mafiafin.faceshape.FaceShapeActivity
import com.avengers.maskfitting.mafiafin.faceshape.FaceContourDetectionProcessor
import com.avengers.maskfitting.mafiafin.databinding.ActivityFaceShapeOutputBinding
import com.avengers.maskfitting.mafiafin.personal_color.PersonalColorActivity
import com.google.ar.core.dependencies.i

class FaceShapeOutputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceShapeOutputBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding =  ActivityFaceShapeOutputBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        var result = intent.getStringExtra("FaceShape").toString()
        Log.d("얼굴형 분류 결과", result)

        binding.faceShape.text = result

        when(result){
            "0" -> binding.faceShape.text = "하트형 얼굴 입니다"
            "1" -> binding.faceShape.text = "계란형 얼굴 입니다"
            "2" -> binding.faceShape.text = "둥근형 얼굴 입니다"
            "3" -> binding.faceShape.text = "사각형 얼굴 입니다"
            else -> binding.faceShape.text = "분류된 얼굴형이 없습니다"

        }
        binding.btnNext.setOnClickListener {
          var intent = Intent(this, PersonalColorActivity::class.java)
         startActivity(intent)
         this.finish()
        }
    }
}
