package com.avengers.maskfitting.mafiafin.main.weather.api

import com.avengers.maskfitting.mafiafin.BuildConfig
import com.avengers.maskfitting.mafiafin.main.weather.model.airQuality.AirQualityResponse
import com.avengers.maskfitting.mafiafin.main.weather.model.nearbycenter.StationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaAPI {
    @GET(
        "B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" +
                "?serviceKey=${BuildConfig.AIRKOREA_SERVICE_KEY}" +
                "&returnType=json"
    )
    suspend fun getNearbyCenter(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<StationResponse>

    @GET(
        "B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty" + "?serviceKey=${BuildConfig.AIRKOREA_SERVICE_KEY}" +
                "&returnType=json" + "&dataTerm=DAILY" + "&ver=1.3"
    )
    suspend fun getAirQuality(
        @Query("stationName") stationName: String
    ): Response<AirQualityResponse>
}