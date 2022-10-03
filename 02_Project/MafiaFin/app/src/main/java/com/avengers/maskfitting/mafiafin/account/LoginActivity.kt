package com.avengers.maskfitting.mafiafin.account

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityLoginBinding
import com.avengers.maskfitting.mafiafin.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONException
import org.json.JSONObject

// 앱의 첫 시작 액티비티
// 로그인 액티비티. 로그인이나 회원가입 동작 수행
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var hasSignIn = false
    private lateinit var preferences: SharedPreferences

    override fun onStart() {
        super.onStart()
        val account = this.let { GoogleSignIn.getLastSignedInAccount(it) }
        if (account != null && !hasSignIn){
            hasSignIn = true
            googleSignIn()
        }
        // 자동 로그인 여부 확인 후 로그인
        preferences = getSharedPreferences("userEmail", MODE_PRIVATE)
        val savedEmail = preferences.getString("email", "").toString()
        if (savedEmail != "") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)    // 뷰바인딩 사용
        val view = binding.root
        setContentView(view)

        // ActivityResultLauncher
        setResultSignUp()

        // Google 로그인을 위한 GoogleSignInOptions 개체 생성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 로그인 버튼
        binding.btnGoogleLogin.setOnClickListener { view ->
            googleSignIn()
        }

        // 일반 로그인버튼
        binding.btnLogin.setOnClickListener { view ->
            val nickname = binding.editId.text.toString()
            val password = binding.editPw.text.toString()
            val responseListener: Response.Listener<String?> = Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        // 로그인한 회원 정보 SharedPreference 에 저장
                        val jsonEmail = jsonObject.getString("email")
                        val jsonName = jsonObject.getString("name")
                        val jsonNickname = jsonObject.getString("nickname")
                        val edit = preferences.edit()
                        edit.putString("email", jsonEmail)
                        edit.putString("nickname", jsonNickname)
                        edit.putString("name", jsonName)
                        edit.apply()

                        Log.d("LoginActivity", jsonEmail.toString())

                        // 로그인 완료
                        Toast.makeText(this@LoginActivity, "로그인 성공했습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity,
                            "아이디/비밀번호를 다시 확인해주세요.",
                            Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            // 로그인 요청
            val loginRequest = LoginRequest(
                nickname,
                password,
                responseListener)
            val queue: RequestQueue = Volley.newRequestQueue(this@LoginActivity)
            queue.add(loginRequest)
        }

        // NoticeActivity 로 전환(회원가입 절차 1)
        binding.btnRegister.setOnClickListener { view ->
            val intent = Intent(this, NoticeActivity::class.java)
            startActivity(intent)
        }
    }

    // registerForActivityResult 를 이용한 콜백 함수
    private fun setResultSignUp() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // 정상적으로 결과가 받아와진다면 조건문 실행(로그인 성공 시)
                if (result.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    // 구글 로그인 성공. 로그인 결과 처리
                    handleSignInResult(task)
                }
            }
    }

    // 로그인 결과 처리 함수
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val name = account?.displayName.toString()

            val responseListener: Response.Listener<String?> = Response.Listener { response ->
                try {
                    var jsonObject = JSONObject(response)
                    val registered = jsonObject.getBoolean("registered")
                    // 가입한 회원의 경우
                    if (registered) {
                        // 토스트 메시지 출력 후 메인 액티비티 실행
                        Toast.makeText(this@LoginActivity, "로그인 성공했습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    // 가입하지 않은 회원의 경우
                    else {
                        Toast.makeText(this, "등록된 정보가 없어 추가 정보 수집을 위해 마이페이지로 이동합니다.", Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("Fragment", R.id.fourth)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            // 소셜 로그인 요청
            val socialLoginRequest = SocialLoginRequest(
                email,
                name,
                responseListener
            )
            val queue: RequestQueue = Volley.newRequestQueue(this)
            queue.add(socialLoginRequest)

        } catch (e: ApiException) {
            // The Application status code indicates the detailed failure reason.
            // Please refer to GoogleSignInStatusCodes class reference for more information.
            Log.w("failed", "SignInResult: failed code = " + e.statusCode)
        }
    }

    // 구글 로그인 인텐트 실행
    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }
}