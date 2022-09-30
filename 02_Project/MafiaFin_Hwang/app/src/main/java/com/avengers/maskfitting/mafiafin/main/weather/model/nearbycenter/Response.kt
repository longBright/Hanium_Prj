package com.avengers.maskfitting.mafiafin.main.weather.model.nearbycenter


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("header")
    val header: Header?
)