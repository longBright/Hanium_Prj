package com.example.cam_test

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

// 카메라 권한 획득 여부 확인 후 메인 액티비티 실행 or 에러 로그 출력
class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        // 권한 확인을 위한 TedPermission
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener{
                // 권한 획득 시 메인 액티비티 실행
                override fun onPermissionGranted() {
                    startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                    finish()
                }

                // 권한 거절 시 에러 로그 출력
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    for(i in deniedPermissions!!)
                        i.showErrLog()
                }

            })
            .setDeniedMessage("앱을 실행하려면 권한을 허가하셔야합니다.")
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }
}