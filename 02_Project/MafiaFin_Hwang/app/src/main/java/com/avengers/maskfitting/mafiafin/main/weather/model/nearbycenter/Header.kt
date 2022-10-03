package com.avengers.maskfitting.mafiafin.main.weather.model.nearbycenter


import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("resultCode")
    val resultCode: String?,
    @SerializedName("resultMsg")
    val resultMsg: String?
)