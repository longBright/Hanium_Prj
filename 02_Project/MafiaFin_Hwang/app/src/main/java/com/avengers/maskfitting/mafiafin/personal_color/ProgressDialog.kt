package com.avengers.maskfitting.mafiafin.personal_color

import android.R
import android.app.Dialog
import android.content.Context
import android.view.Window


class ProgressDialog(context: Context?) : Dialog(context!!) {
    init {

        // 다이얼 로그 제목을 안보이게...
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(com.avengers.maskfitting.mafiafin.R.layout.activity_loading)
        setCancelable(false)

    }
}