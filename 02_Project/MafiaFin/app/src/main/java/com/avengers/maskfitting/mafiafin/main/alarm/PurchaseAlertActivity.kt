package com.avengers.maskfitting.mafiafin.main.alarm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityMaskPurchaseDetailBinding


class PurchaseAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaskPurchaseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaskPurchaseDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 구매하기 버튼
        binding.shoppingBtn.setOnClickListener {
            val intentShop = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://search.shopping.naver.com/search/all?query=" + binding.maskName.text)
            )
            startActivity(intentShop)
        }

        // 확인 버튼 / 정보 수정
        binding.CloseBtn.setOnClickListener {
            val intentClose = Intent(this, MaskAlertMainActivity::class.java)
            startActivity(intentClose)
        }

        // 삭제 버튼 / 정보 삭제
        binding.DeleteBtn.setOnClickListener {
            val intentDelete = Intent(this, MaskAlertMainActivity::class.java)
            startActivity(intentDelete)
        }

        var maskType = ""
        var alert = ""
        var count = 0

        var maskNickname = binding.maskNickname
        var maskName = binding.maskName
        var purchaseDate = binding.BeforePurchase
        var maskImage = binding.maskImage
        var setAlert = binding.setAlert
        if (intent.hasExtra("maskNickname") && intent.hasExtra("maskName") && intent.hasExtra("purchaseDate")
            && intent.hasExtra("count") && intent.hasExtra("maskImage") && intent.hasExtra("setAlert")) {
            maskNickname.text = intent.getStringExtra("maskNickname")
            maskName.text = intent.getStringExtra("maskName")
            purchaseDate.text = intent.getStringExtra("purchaseDate")
            binding.Counter.text = intent.getStringExtra("count")
            maskType = intent.getByteArrayExtra("maskImage").toString()
            alert = intent.getStringExtra("setAlert").toString()
        }                                                                                               // intent 값 받아오기

        count = Integer.parseInt(binding.Counter.text as String)

        Log.d("maskType", maskImage.toString())
        if (maskType == "덴탈 마스크") { maskImage.setImageResource(R.drawable.dental) }                  // 마스크 이미지 출력하기
        else if (maskType == "KF 80" || maskType == "KF 94") { maskImage.setImageResource((R.drawable.kf)) }

        if (alert != "") { setAlert.setChecked(true)}                                                   // 알람 스위치 setting
        else if (alert == "") {setAlert.setChecked(false)}

        binding.wearBtn.setOnClickListener {                                                            // '착용완료' 버튼 클릭 시
            count--                                                                                     // 마스크 수량 차감
            binding.Counter.text = count.toString()

            if (binding.Counter.text == "0") { count = 0 }
        }

        //binding.AfterPurchase.text = (Integer.parseInt(purchaseDate.text as String)+count).toString()
    }

//    companion object {
//        private const val Search = "마스크"
//    }
}
