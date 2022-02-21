package com.example.cam_test

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener{
                override fun onPermissionGranted() {
                    startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                    finish()
                }

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