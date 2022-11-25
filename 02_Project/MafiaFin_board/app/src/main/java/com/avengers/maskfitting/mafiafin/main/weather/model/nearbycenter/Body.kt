package com.avengers.maskfitting.mafiafin.main.weather.model.nearbycenter


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val stations: List<Station>?,
    @SerializedName("numOfRows")
    val numOfRows: Int?,
    @SerializedName("pageNo")
    val pageNo: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?
)