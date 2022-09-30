package com.avengers.maskfitting.mafiafin.account

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class SocialLoginRequest(email: String, name: String,
                         listener: Response.Listener<String?>?) :
    StringRequest(Method.POST, URL, listener, null) {
    private val parameters: MutableMap<String, String>

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return parameters
    }

    companion object {
        private const val URL = "http://43.200.115.71/Social_Login.php"
    }

    init {
        parameters = HashMap()
        parameters["email"] = email
        parameters["name"] = name
    }
}