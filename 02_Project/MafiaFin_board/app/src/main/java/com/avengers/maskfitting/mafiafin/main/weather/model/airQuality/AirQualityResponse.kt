package com.avengers.maskfitting.mafiafin.main.weather.model.airQuality


import com.google.gson.annotations.SerializedName

data class AirQualityResponse(
    @SerializedName("response")
    val response: Response?
)