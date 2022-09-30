package com.avengers.maskfitting.mafiafin.main

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.ActivityMainBinding
import com.avengers.maskfitting.mafiafin.main.fitting.Mask_fitting
import com.avengers.maskfitting.mafiafin.main.mypage.MyPage
import com.avengers.maskfitting.mafiafin.main.weather.WeatherInfo
import com.avengers.maskfitting.mafiafin.main.board.bulletinBoard.Board
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var preferences: SharedPreferences
    private var email: String? = ""

    override fun onStart() {
        super.onStart()
        // 로그인한 사용자 정보
        val account = this.let { GoogleSignIn.getLastSignedInAccount(it) }
        if (account != null){
            Toast.makeText(this, "Google 계정을 통해 로그인했습니다.", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "${account.email}")
        }

        email = preferences.getString("email", "")
        if (email != "") {
            Toast.makeText(this, "Ma!fia 계정을 통해 로그인했습니다.", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", email.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if(intent.hasExtra("Fragment")) {
            bnv_main.selectedItemId = intent.getIntExtra("Fragment", R.id.first)
        }

        // 로그인 중인 사용자 정보 획득
        preferences = getSharedPreferences("userEmail", MODE_PRIVATE)

        // GoogleSignInClient 객체 생성
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()

        mGoogleSignInClient = this.let { GoogleSignIn.getClient(it, gso) }


        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.first -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val fitFragment = Mask_fitting()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, fitFragment).commit()
                }
                R.id.second -> {
                    val WeatherFragment = WeatherInfo()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, WeatherFragment).commit()
                }
                R.id.third -> {
                    val BoardFragment = Board()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, BoardFragment).commit()
                }
                R.id.fourth -> {
                    val MypageFragment = MyPage()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, MypageFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.first
        }
    }
}