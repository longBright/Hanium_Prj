package com.avengers.maskfitting.mafiafin.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.jakewharton.threetenabp.AndroidThreeTen
import org.json.JSONObject

// 회원가입 액티비티
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onStart() {
        super.onStart()
        val account = this.let { GoogleSignIn.getLastSignedInAccount(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)    // 뷰 바인딩
        val view = binding.root
        setContentView(view)

        // ThreeTen 백포트 사용(API 26 이하에서 java.time 패키지 사용을 위함)
        AndroidThreeTen.init(this)

        // 이전 액티비티(얼굴형, 퍼스널 컬러 진단)에서 intent로 보낸 값이 있을 경우 받아줘야함
        val faceShapeResult = intent.getStringExtra("FaceShape")
        val personalColorResult = intent.getStringExtra("PersonalColor")
        Log.d("얼굴형 결과", faceShapeResult.toString())
        Log.d("퍼스널컬러 결과", personalColorResult.toString())

        // 스피너 어댑터 연결
        binding.spinFaceShape.adapter = ArrayAdapter.createFromResource(this,
            R.array.faceshapeList,
            android.R.layout.simple_spinner_item)   //connect faceshape adapter
        binding.spinPersonalColor.adapter = ArrayAdapter.createFromResource(this,
            R.array.personalcolourList,
            android.R.layout.simple_spinner_item)  // connect personal colour adapter

        // 초기값 설정
        binding.spinFaceShape.setSelection(getIndex(binding.spinFaceShape, faceShapeResult))
        binding.spinPersonalColor.setSelection(getIndex(binding.spinPersonalColor, personalColorResult))

        // 얼굴형 스피너 선택 시 동작
        binding.spinFaceShape.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 안했을경우 -> 선택하세요 Toast!
                // 다음 화면으로 전환 안되게(회원가입 안되게)
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (position) {
                    // 선택안함
                    1 -> {
                        //To do선택 시 동작
                        // DB insert 값을 null 설정
                    }
                    // 둥근형
                    2 -> {
                        //To do선택 시 동작
                    }
                    // 사각형
                    3 -> {
                        //To do선택 시 동작
                    }
                    // 하트형
                    4 -> {
                        //To do선택 시 동작
                    }
                    // 계란형
                    5 -> {
                        //To do선택 시 동작
                    }
                }
            }
        }

        // 퍼스널 컬러 스피너 선택 시 동작
        binding.spinPersonalColor.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // 아무것도 안했을경우 -> 선택하세요 Toast!
                    // 다음 화면으로 전환 안되게(회원가입 안되게)
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    when (position) {
                        //선택안함
                        1 -> {
                            //To do선택 시 동작
                            // DB insert 값을 null 설정
                        }
                        //봄 웜톤
                        2 -> {
                            //To do선택 시 동작
                        }
                        //여름 쿨톤
                        3 -> {
                            //To do선택 시 동작
                        }
                        //가을 웜톤
                        4 -> {
                            //To do선택 시 동작
                        }
                        //겨울 쿨톤
                        5 -> {
                            //To do선택 시 동작
                        }
                    }
                }
            }

        // 가입하기 버튼 동작.
        // 1. Spinner 에서 선택된 값 가져올 수 있게 해야함 -> 완료.
        // 2. Spinner 에서 선택 안한 경우 / RadioBtn 선택 안한 경우 만들어야함.
        // 3. 얼굴형 / 퍼스널 컬러 진단 시 해당 값을 받도록 해야함.
        binding.btnRegister.setOnClickListener { view ->
            // 빈 값 있을 경우
            if (binding.editNickname.text.isEmpty()
                || binding.editPw.text.isEmpty()
                || binding.editPwRe.text.isEmpty()
                || binding.editName.text.isEmpty()
                || binding.editEmail.text.isEmpty()
            ) {
                Toast.makeText(this, "값을 전부 입력해주세요.", Toast.LENGTH_LONG).show()
            }
            // 다 입력 되었을 경우
            else {
                if (binding.editPw.text.toString() == binding.editPwRe.text.toString()) {    //패스워드/패스워드 확인이 일치
                    val responseListener: Response.Listener<String?> =
                        Response.Listener { response ->
                            try {
                                var jsonObject = JSONObject(response)
                                val success = jsonObject.getBoolean("success")
                                if (success) {
                                    Toast.makeText(this, "회원가입 성공.", Toast.LENGTH_LONG).show()
                                    // 로그인 화면으로 복귀
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "회원가입 실패.", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    // 성별 텍스트
                    val gender = when (binding.radioGroup.checkedRadioButtonId) {
                        R.id.MaleRadioBtn -> binding.MaleRadioBtn.text.toString()
                        else -> binding.FemaleRadioBtn.text.toString()
                    }

                    val registerRequest = RegisterRequest(
                        binding.editNickname.text.toString(),
                        binding.editPw.text.toString(),
                        binding.editName.text.toString(),
                        binding.editEmail.text.toString(),
                        gender,
                        binding.spinPersonalColor.selectedItem.toString(),
                        binding.spinFaceShape.selectedItem.toString(),
                        responseListener
                    )
                    val queue: RequestQueue = Volley.newRequestQueue(this)
                    queue.add(registerRequest)
                } else {
                    Toast.makeText(this, "패스워드가 틀렸습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getIndex(spinner: Spinner, item: String?): Int {
        for (i: Int in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == item) return i
        }
        return 0
    }
}