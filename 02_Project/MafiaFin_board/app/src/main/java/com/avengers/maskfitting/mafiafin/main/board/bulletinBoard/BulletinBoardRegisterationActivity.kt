package com.avengers.maskfitting.mafiafin.main.board.bulletinBoard

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bulletin_board_registeration.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityBulletinBoardRegisterationBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class BulletinBoardRegisterationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBulletinBoardRegisterationBinding
    private lateinit var preferences: SharedPreferences
    private var email: String? = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBulletinBoardRegisterationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val account = this.let { GoogleSignIn.getLastSignedInAccount(it) }
        if (account == null) {
            // 로그인 중인 사용자 정보 획득
            // preferences를 통해 userEmail 값 전달 받음
            preferences = getSharedPreferences("userEmail", MODE_PRIVATE)
            email = preferences.getString("email", "").toString()
            //Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        }
        // 구글 회원
        else {
            email = account?.email      // 사용자 이메일 초기화
            //Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        }


        // 뒤로가기 버튼
        binding.returnBtn.setOnClickListener {
            val intent = Intent(this, BulletinBoard::class.java)
            startActivity(intent)
        }

        // 게시물 등록 기능/ 등록 버튼 구현
        binding.regBtn.setOnClickListener { view ->
            // 빈 값 있을 경우
            if (binding.title.text.isEmpty()
                || binding.content.text.isEmpty() ){
                Toast.makeText(this, "제목 및 내용을 전부 입력해주세요.", Toast.LENGTH_LONG).show()
            }
            // 다 입력 되었을 경우
            else {
                val responseListener: Response.Listener<String?> =
                    Response.Listener { response ->
                        try {
                            var jsonObject = JSONObject(response)
                            val success = jsonObject.getBoolean("success")
                            if (success) {
                                Toast.makeText(this, "게시물이 등록되었습니다.", Toast.LENGTH_LONG).show()

                                // 메인 화면으로 전환
                                val intent = Intent(this, BulletinBoard::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(this, "게시물 올리기 실패.", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, e.message , Toast.LENGTH_LONG).show()
                        }
                    }

                val bulletinBoardRegisterRequest = email?.let{
                    BulletinBoardRegisterRequest(
                        binding.title.text.toString(),
                        binding.content.text.toString(),
                        it,
                        responseListener
                    )
                }
                val queue: RequestQueue = Volley.newRequestQueue(this)
                queue.add(bulletinBoardRegisterRequest)
            }
        }
    }
}
