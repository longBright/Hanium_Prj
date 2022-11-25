package com.avengers.maskfitting.mafiafin.main.weather

import android.util.Log
import com.avengers.maskfitting.mafiafin.BuildConfig
import com.avengers.maskfitting.mafiafin.main.weather.api.AirKoreaAPI
import com.avengers.maskfitting.mafiafin.main.weather.api.KakaoLocalAPI
import com.avengers.maskfitting.mafiafin.main.weather.model.airQuality.MeasuredValue
import com.avengers.maskfitting.mafiafin.main.weather.model.nearbycenter.Station
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object Repository {

    suspend fun getNearbyCenter(latitude: Double, longitude: Double): Station? {
        val tmCoordinates = kakaoLocalAPI.getTmCoordinates(longitude, latitude)
            .body()?.documents
            ?.firstOrNull() // 첫번째 값 없으면 null
        val tmX = tmCoordinates?.x
        val tmY = tmCoordinates?.y
        Log.d("ttt", "" + tmX + " " + tmY)
        return airKoreaAPIService
            .getNearbyCenter(tmX!!, tmY!!)
            .body()
            ?.response
            ?.body
            ?.stations
            ?.minByOrNull { // 가장 작은 값 또는 null \
                it.tm ?: Double.MAX_VALUE
            }
    }

    suspend fun getAirQualityData(stationName: String): MeasuredValue? =
        airKoreaAPIService.getAirQuality(stationName)
            .body()?.response?.body?.measuredValues?.firstOrNull()

    private val kakaoLocalAPI: KakaoLocalAPI by lazy {
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }
    private val airKoreaAPIService: AirKoreaAPI by lazy {
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }

    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                // BODY =  다보여줌
                // DEBUG 일때만 다 보여줌
                HttpLoggingInterceptor.Level.BODY
            } else {
                // NONE = 안보여줌
                HttpLoggingInterceptor.Level.NONE
            }
        }).build()
}