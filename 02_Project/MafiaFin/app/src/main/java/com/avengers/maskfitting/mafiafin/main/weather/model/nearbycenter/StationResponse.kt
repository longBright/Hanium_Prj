package com.avengers.maskfitting.mafiafin.main.weather.model.nearbycenter


import com.google.gson.annotations.SerializedName

data class StationResponse(
    @SerializedName("response")
    val response: Response?
)