package com.avengers.maskfitting.mafiafin.main.board.bulletinBoard

import android.text.Editable
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class BulletinBoardRegisterRequest (
    title: String,
    content: String,
    email: String,
    listener: Response.Listener<String?>?,
):
    StringRequest(Method.POST, URL, listener, null) {
        private val parameters: MutableMap<String, String>

        @Throws(AuthFailureError::class)
        override fun getParams(): MutableMap<String, String>? {
            return parameters
        }

        companion object {
        // 서버 url 설정 (php 파일 연동)
        private const val URL =
            "http://43.200.115.71/WriteBulletinBoard.php"     // "http:// 퍼블릭 DNS 주소/MaskRegister.php"
    }

        init {
            parameters = HashMap()
            parameters["title"] = title
            parameters["content"] = content
            parameters["email"] = email
        }
    }
