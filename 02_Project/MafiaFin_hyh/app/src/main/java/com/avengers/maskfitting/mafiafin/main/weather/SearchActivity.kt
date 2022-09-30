package com.avengers.maskfitting.mafiafin.main.weather

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.avengers.maskfitting.mafiafin.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var searchName = intent.getStringExtra(SEARCH_WORD).toString()
        // 확인 해봐야함.
        if ("덴탈 마스크" in searchName) {
            searchName = "덴탈마스크"
        }
        Log.d(TAG, searchName)
        // 정상 작동(검색어 제대로 처리되면)
        // 검색어로해서 검색 실행.
        // 색상이 넘겨질거고 마스크의 유형이 넘겨질거임
        // 어떻게 받아서 처리할 지 -> 퍼스널 컬러 추천 -> 마스크 색상. 빨강
        // 검색어 포맷.
    }

    companion object {
        private const val TAG = "SearchActivity"
        private const val SEARCH_WORD = "search_word"
    }
}