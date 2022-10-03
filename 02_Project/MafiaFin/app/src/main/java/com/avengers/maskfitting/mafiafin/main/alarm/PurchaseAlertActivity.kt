package com.avengers.maskfitting.mafiafin.main.alarm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                Uri.parse("https://search.shopping.naver.com/search/all?query=" + Search)
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

        var maskNickname = binding.maskNickname
        if (intent.hasExtra("maskNickname")) {
            maskNickname.text = intent.getStringExtra("maskNickname")
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val Search = "마스크"
    }
}
