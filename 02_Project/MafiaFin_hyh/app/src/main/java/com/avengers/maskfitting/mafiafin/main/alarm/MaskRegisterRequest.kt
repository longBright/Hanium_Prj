package com.example.alarm.db

import android.text.Editable
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class MaskRegisterRequest(
    maskName: String,
    maskNickname: String,
    beforePurchaseDate: String,             // 구매일 문자열로 입력 후 처리
    maskCounter: Editable,
    maskType: String,
    alert: Int,
    listener: Response.Listener<String?>?,
) :
    StringRequest(Method.POST, URL, listener, null) {
    private val parameters: MutableMap<String, String>

    @Throws(AuthFailureError::class)
    override fun getParams(): MutableMap<String, String>? {
        return parameters
    }

    companion object {
        // 서버 url 설정 (php 파일 연동)
        private const val URL = "http://43.200.115.71/MaskRegister.php"     // "http:// 퍼블릭 DNS 주소/MaskRegister.php"
    }

    init {
        parameters = HashMap()
        parameters["mask_name"] = maskName
        parameters["mask_nickname"] = maskNickname
        parameters["purchase_date"] = beforePurchaseDate
        parameters["mask_count"] = maskCounter.toString()
        parameters["mask_type"] = maskType
        parameters["set_alert"] = alert.toString()
    }
}