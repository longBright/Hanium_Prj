package com.avengers.maskfitting.mafiafin.db

import android.provider.BaseColumns

object LocalDatas { //  로컬 데이터 베이스의 자료형태 정의된 object
    object userData : BaseColumns {  //  users 라는 DB 테이블의 데이터 컬럼 내용 정리
        const val TABLE_NAME = "users"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val GENDER = "gender"
        const val PERSONAL_COLOUR = "personal_color"
        const val FACE_SHAPE = "face_shape"
        const val REG_DATE = "reg_date"
    }
    object Groups :BaseColumns{ // 만약 그룹에 관련한 DB 형식을 지정하고 싶다면 동일한 방식으로 추가합니다.

    }
}