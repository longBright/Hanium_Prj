package com.avengers.maskfitting.mafiafin.account

import com.android.volley.AuthFailureError
import com.android.volley.Response

import com.android.volley.toolbox.StringRequest


class RegisterRequest(
    nickname: String,
    password: String,
    name: String,
    email: String,
    gender: String,
    personalColor: String,
    faceShape: String,
    listener: Response.Listener<String?>?,
) :
    StringRequest(Method.POST, URL, listener, null) {
    private val parameters: MutableMap<String, String>

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return parameters
    }

    companion object {
        // 서버 url 설정 (php 파일 연동)
        private const val URL = "http://43.200.115.71/Register.php" // "http:// 퍼블릭 DNS 주소/Register.php"
    }

    init {
        parameters = HashMap()
        parameters["nickname"] = nickname
        parameters["password"] = password
        parameters["name"] = name
        parameters["email"] = email
        parameters["gender"] = gender
        parameters["personal_color"] = personalColor
        parameters["face_shape"] = faceShape
    }
}