package com.jinny.finedust.model.airQuality


import com.google.gson.annotations.SerializedName

data class MeasuredValue(
    @SerializedName("coFlag")
    val coFlag: Any?,
    @SerializedName("coGrade")
    val coGrade: Grade?,
    @SerializedName("coValue")
    val coValue: String?,
    @SerializedName("dataTime")
    val dataTime: String?,
    @SerializedName("khaiGrade")
    val khaiGrade: Grade?,
    @SerializedName("khaiValue")
    val khaiValue: String?,
    @SerializedName("mangName")
    val mangName: String?,
    @SerializedName("no2Flag")
    val no2Flag: Any?,
    @SerializedName("no2Grade")
    val no2Grade: Grade?,
    @SerializedName("no2Value")
    val no2Value: String?,
    @SerializedName("o3Flag")
    val o3Flag: Any?,
    @SerializedName("o3Grade")
    val o3Grade: Grade?,
    @SerializedName("o3Value")
    val o3Value: String?,
    @SerializedName("pm10Flag")
    val pm10Flag: Any?,
    @SerializedName("pm10Grade")
    val pm10Grade: Grade?,
    @SerializedName("pm10Grade1h")
    val pm10Grade1h: String?,
    @SerializedName("pm10Value")
    val pm10Value: String?,
    @SerializedName("pm10Value24")
    val pm10Value24: String?,
    @SerializedName("pm25Flag")
    val pm25Flag: Any?,
    @SerializedName("pm25Grade")
    val pm25Grade: Grade?,
    @SerializedName("pm25Grade1h")
    val pm25Grade1h: String?,
    @SerializedName("pm25Value")
    val pm25Value: String?,
    @SerializedName("pm25Value24")
    val pm25Value24: String?,
    @SerializedName("so2Flag")
    val so2Flag: Any?,
    @SerializedName("so2Grade")
    val so2Grade: Grade?,
    @SerializedName("so2Value")
    val so2Value: String?
)
// Grade는 원래 String? 값으로 받지만 따로 커스팅 하기위해 enum을 만들고
// Grade? 타입으로 변경했음