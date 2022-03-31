package com.jinny.finedust.model.nearbycenter


import com.google.gson.annotations.SerializedName

data class StationResponse(
    @SerializedName("response")
    val response: Response?
)