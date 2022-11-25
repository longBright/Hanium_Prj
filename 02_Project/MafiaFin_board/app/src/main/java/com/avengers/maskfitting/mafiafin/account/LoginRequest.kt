package com.avengers.maskfitting.mafiafin.account

import com.android.volley.AuthFailureError
import com.android.volley.Response

import com.android.volley.toolbox.StringRequest


class LoginRequest(nickname: String, password: String,
                   listener: Response.Listener<String?>?) :
    StringRequest(Method.POST, URL, listener, null) {
    private val parameters: MutableMap<String, String>

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return parameters
    }

    companion object {
        private const val URL = "http://43.200.115.71/Login.php"
    }

    init {
        parameters = HashMap()
        parameters["nickname"] = nickname
        parameters["password"] = password
    }
}