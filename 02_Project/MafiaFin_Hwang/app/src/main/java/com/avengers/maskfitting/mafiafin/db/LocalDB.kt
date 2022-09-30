package com.avengers.maskfitting.mafiafin.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import org.threeten.bp.LocalDate

class LocalDB (
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version){
    //코드 참조
//https://developer.android.com/training/data-storage/sqlite?hl=ko
    override fun onCreate(db: SQLiteDatabase?) {
        // DB 생성시 실행
        if (db != null) {
            createDatabase(db)
            Log.d("LocalDB", "DB 생성 완료")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // DB 버전 변경시 실행됨
        val sql : String = "DROP TABLE if exists ${LocalDatas.userData.TABLE_NAME}"

        if (db != null) {
            db.execSQL(sql)
            onCreate(db)
        } //  버전이 변경되면 기존 Table을 삭제후 재생성함.
    }

    fun createDatabase(db: SQLiteDatabase) {
        // 테이블이 존재하지 않는경우 생성
        var sql: String = "CREATE TABLE if not exists ${LocalDatas.userData.TABLE_NAME} (" +
                "${BaseColumns._ID} integer primary key autoincrement," +
                "${LocalDatas.userData.USERNAME} text not null," +
                "${LocalDatas.userData.PASSWORD} text not null,"+
                "${LocalDatas.userData.GENDER} text not null,"+
                "${LocalDatas.userData.PERSONAL_COLOUR} text,"+
                "${LocalDatas.userData.FACE_SHAPE} text,"+
                "${LocalDatas.userData.REG_DATE} date default CURRENT_DATE"+
                ");"
        db.execSQL(sql)
    }

    fun registerUser(username: String, password:String,
                     gender:String, personal_colour:String,
                     face_shape:String){
        val db =this.writableDatabase
        val date: LocalDate = LocalDate.now()
        val values = ContentValues().apply {// insert될 데이터값
            put(LocalDatas.userData.USERNAME, username)
            put(LocalDatas.userData.PASSWORD, password)
            put(LocalDatas.userData.GENDER, gender)
            put(LocalDatas.userData.PERSONAL_COLOUR, personal_colour)
            put(LocalDatas.userData.FACE_SHAPE, face_shape)
            put(LocalDatas.userData.REG_DATE, date.toString())
        }
        val newRowId = db?.insert(LocalDatas.userData.TABLE_NAME, null, values)
        // 인서트후 인서트된 primary key column의 값(_id) 반환.
    }

    fun checkIdExist(id: String): Boolean {
        val db = this.readableDatabase

        // 리턴받고자 하는 컬럼 값의 array
        val projection = arrayOf(BaseColumns._ID)
        //,LocalDatas.userData.COLUMN_NAME_ID, LocalDatas.userData.COLUMN_NAME_PASSWORD)


        //  WHERE "id" = id AND "password"=password 구문 적용하는 부분
        val selection = "${LocalDatas.userData.USERNAME} = ?"
        val selectionArgs = arrayOf(id)

        // 정렬조건 지정
        // val sortOrder = "${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

        val cursor = db.query(
            LocalDatas.userData.TABLE_NAME,   // 테이블
            projection,             // 리턴 받고자 하는 컬럼
            selection,              // where 조건
            selectionArgs,          // where 조건에 해당하는 값의 배열
            null,                   // 그룹 조건
            null,                   // having 조건
            null               // orderby 조건 지정
        )
        return cursor.count > 0
    }

    fun logIn(username: String, password:String): Boolean {
        val db = this.readableDatabase

        // 리턴받고자 하는 컬럼 값의 array
        val projection = arrayOf(BaseColumns._ID)
        //,LocalDatas.userData.COLUMN_NAME_ID, LocalDatas.userData.COLUMN_NAME_PASSWORD)


        //  WHERE "id" = id AND "password"=password 구문 적용하는 부분
        val selection = "${LocalDatas.userData.USERNAME} = ? AND ${LocalDatas.userData.PASSWORD} = ?"
        val selectionArgs = arrayOf(username,password)

        // 정렬조건 지정
        // val sortOrder = "${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

        val cursor = db.query(
            LocalDatas.userData.TABLE_NAME,   // 테이블
            projection,             // 리턴 받고자 하는 컬럼
            selection,              // where 조건
            selectionArgs,          // where 조건에 해당하는 값의 배열
            null,                   // 그룹 조건
            null,                   // having 조건
            null               // orderby 조건 지정
        )


        return cursor.count > 0
    }

}