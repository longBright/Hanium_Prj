package com.jinny.finedust

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.jinny.finedust.databinding.ActivityMainBinding
import com.jinny.finedust.model.airQuality.Grade
import com.jinny.finedust.model.airQuality.MeasuredValue
import com.jinny.finedust.model.nearbycenter.Station
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var cancellationTokenSource: CancellationTokenSource? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // 코루틴 스콥프
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 새로고침
        bindViews()
        // 위치정보 권한 요청
        requestLocationPermissions()
        initVariables()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource!!.cancel()
        // 같이 종료
        scope.cancel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val locationPermissionGranted =
            requestCode == REQUEST_ACCESS_LOCATION_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (!locationPermissionGranted) {
            Toast.makeText(this, "위치정보 권한을 동의해야합니다.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // 데이터 가져오기
            fetchAirQuality()
        }
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    // 권한 어차피 받고 들어오므로 MissingPermission 해줌
    @SuppressLint("MissingPermission")
    private fun fetchAirQuality() {
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token
        ).addOnSuccessListener { location ->
            //binding.textView.text = "${location.latitude}, ${location.longitude}"
            scope.launch {
                binding.errorTextView.visibility = View.GONE
                try {
                    // 코루틴 시작
                    val monitoringStation =
                        Repository.getNearbyCenter(location.latitude, location.longitude)
                    val measuredValue =
                        Repository.getAirQualityData(monitoringStation!!.stationName!!)
                    displayAirQualityData(monitoringStation, measuredValue!!)
                } catch (exeption: Exception) {
                    binding.errorTextView.visibility = View.VISIBLE
                } finally {
                    binding.progressBar.visibility = View.GONE
                    binding.refresh.isRefreshing = false
                }
            }
        }
    }

    private fun bindViews() {
        //새로고침
        binding.refresh.setOnRefreshListener {
            fetchAirQuality()
        }
    }

    fun displayAirQualityData(monitoringStation: Station, measuredValue: MeasuredValue) {
        // 뷰 서서히 보여지게 함
        binding.mainlayout.animate()
            .alpha(1F)
            .start()
        binding.measuringStationName.text = monitoringStation.stationName
        binding.measuringStationAddressTextView.text = "측정소 : ${monitoringStation.addr}"

        (measuredValue.khaiGrade ?: Grade.UNKNOWN).let { grade ->
            binding.root.setBackgroundResource(grade.colorResId)
            binding.totalGradeLabelTextView.text = grade.label
            binding.totalGradleImojiTextView.text = grade.emoji
        }
        with(measuredValue) {
            binding.fineDustInfoTextView.text =
                "미세먼지: $pm10Value ㎍/㎥ ${(pm10Grade ?: Grade.UNKNOWN).emoji}"
            binding.ultraFineDustInfoTextView.text =
                "초미세먼지: $pm25Value ㎍/㎥ ${(pm25Grade ?: Grade.UNKNOWN).emoji}"

           //---------------------마스크 추천-------------------------------------------
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
    }
}