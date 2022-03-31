package com.jinny.finedust.api

import com.jinny.finedust.BuildConfig
import com.jinny.finedust.model.airQuality.AirQualityResponse
import com.jinny.finedust.model.nearbycenter.StationResponse
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