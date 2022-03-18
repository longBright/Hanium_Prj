package com.example.naver_api_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val thread: Thread = object : Thread() {
            override fun run() {
                //val api = ApiExamSearchBlog
                val api = ApiExamSearchShop
                api.main()
            }
        }
        thread.start()
    }
}