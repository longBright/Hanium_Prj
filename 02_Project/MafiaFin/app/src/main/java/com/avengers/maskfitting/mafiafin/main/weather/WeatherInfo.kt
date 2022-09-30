package com.avengers.maskfitting.mafiafin.main.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.FragmentWeatherinfoBinding
import com.avengers.maskfitting.mafiafin.main.fitting.dental.FaceLandmarksActivity
import com.avengers.maskfitting.mafiafin.main.weather.model.airQuality.Grade
import com.avengers.maskfitting.mafiafin.main.weather.model.airQuality.MeasuredValue
import com.avengers.maskfitting.mafiafin.main.weather.model.nearbycenter.Station
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Weather_info.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherInfo : Fragment() {
    private var cancellationTokenSource: CancellationTokenSource? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentWeatherinfoBinding
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    // 코루틴 스콥프
    private val scope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWeatherinfoBinding.inflate(inflater, container, false)

        bindViews()

        // 위치정보 권한 요청
        requestLocationPermissions()
        Log.d("Tag", "gogo")
        initVariables()

        // 마스크 검색 버튼
        binding.SearchBtn.setOnClickListener {
            var searchName = binding.MaskRecommendation.text
            if ("덴탈 마스크" in searchName) {                   //'덴탈 마스크 / 미착용'
                searchName = "덴탈마스크"                        //덴탈마스크 텍스트로 검색
            }

            var intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://search.shopping.naver.com/search/all?query=" + searchName)
            )
            startActivity(intent)
        }

        // 가상피팅 버튼
        binding.FittingBtn.setOnClickListener{
            // 가상피팅 프래그먼트로 전환
            val intent_2 = Intent(activity, FaceLandmarksActivity::class.java)
            startActivity(intent_2)
        }
        return binding.root
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
            Toast.makeText(activity, "위치정보 권한을 동의해야합니다.", Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
        } else {
            // 데이터 가져오기
            fetchAirQuality()
        }
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
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

            if (pm10Grade?.label.equals("좋음") || pm10Grade?.label.equals("보통")) { //좋음이거나 보통
                binding.MaskRecommendation.text = "덴탈 마스크 / 미착용"                         //마스크 텍스트
                binding.maskImage.setImageResource(R.drawable.dental)                //마스크 이미지
            } else if (pm10Grade?.label.equals("나쁨")) {
                binding.MaskRecommendation.text = "KF 80"
                binding.maskImage.setImageResource(R.drawable.kf)
            }else if (pm10Grade?.label.equals("매우 나쁨")) {
                binding.MaskRecommendation.text = "KF 94"
                binding.maskImage.setImageResource(R.drawable.kf)
            }
            else
                binding.MaskRecommendation.text = "미측정"
        }
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
        private const val SEARCH_WORD = "search_word"
    }
}