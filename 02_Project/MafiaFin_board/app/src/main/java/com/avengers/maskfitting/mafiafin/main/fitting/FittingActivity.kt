package com.avengers.maskfitting.mafiafin.main.fitting

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.avengers.maskfitting.mafiafin.databinding.ActivityFittingBinding

// 퍼스널 컬러 진단 액티비티
// 이전 액티비티(얼굴형 진단)에서 intent 로 넘어온 값 확인 동작 구현 필요
class FittingActivity : AppCompatActivity() {

    private lateinit var fittingCamera: FittingCamera
    private val binding: ActivityFittingBinding by lazy {
        ActivityFittingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createCameraManager()       // 카메라 매니저 생성
        val view = binding.root
        setContentView(view)

        // 카메라 이용 권한 권한 확인
        if (allPermissionsGranted()) {
            fittingCamera.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        Log.d("FittingActivity", "정상동작!")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                fittingCamera.startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    // 카메라 매니저 생성
    private fun createCameraManager() {
        fittingCamera = FittingCamera(
            this,
            binding.previewViewFinder,
            this,
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}