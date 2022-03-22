package kr.ac.skuniv.apinew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    var mask: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /* try {
             text = URLEncoder.encode("마스크", "UTF-8")
         } catch (e: UnsupportedEncodingException) {
             throw RuntimeException("검색어 인코딩 실패", e)
         }*/
        //text = "마스크"
        btn_mask.setOnClickListener { view: View ->   // 버튼 클릭 시 thread start

            var str_mask = edit_mask.text.toString()    // edit text에 적힌 값을 검색 키 값으로

            mask = str_mask

            val thread = Thread({
                var apiExamSearchBlog = ApiExamSearchBlog()
                apiExamSearchBlog.main()
            })
            thread.start()


        }


    }

    inner class ApiExamSearchBlog {


        val clientId = "U1nJhAodAh6OTS64u_oF"
        val clientSecret = "88gzgMdFPb"


        fun main() {


            val apiURL = "https://openapi.naver.com/v1/search/shop?query=" + mask    // json 결과
            //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과

            val requestHeaders: HashMap<String, String> = HashMap()
            requestHeaders.put("X-Naver-Client-Id", clientId)
            requestHeaders.put("X-Naver-Client-Secret", clientSecret)
            val responseBody = get(apiURL, requestHeaders)

            parseData(responseBody)



        }

        private operator fun get(apiUrl: String, requestHeaders: Map<String, String>): String {
            val con = connect(apiUrl)
            try {
                con.setRequestMethod("GET")
                for ((key, value) in requestHeaders) {
                    con.setRequestProperty(key, value)
                }

                val responseCode = con.getResponseCode()
                return if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                    readBody(con.getInputStream())
                } else { // 에러 발생
                    readBody(con.getErrorStream())
                }

            } catch (e: IOException) {
                throw RuntimeException("API 요청과 응답 실패", e)
            } finally {
                con.disconnect()
            }
        }

        private fun connect(apiUrl: String): HttpURLConnection {
            try {
                val url = URL(apiUrl)
                return url.openConnection() as HttpURLConnection
            } catch (e: MalformedURLException) {
                throw RuntimeException("API URL이 잘못되었습니다. : $apiUrl", e)
            } catch (e: IOException) {
                throw RuntimeException("연결이 실패했습니다. : $apiUrl", e)
            }

        }

        private fun readBody(body: InputStream): String {
            val streamReader = InputStreamReader(body)

            try {
                BufferedReader(streamReader).use({ lineReader ->
                    val responseBody = StringBuilder()

                    var line: String? = lineReader.readLine()
                    while (line != null) {
                        responseBody.append(line)
                        line = lineReader.readLine()
                    }
                    return responseBody.toString()
                })
            } catch (e: IOException) {
                throw RuntimeException("API 응답을 읽는데 실패했습니다.", e)
            }
        }

        fun parseData(responseBody: String) {
            var title: String
            var cnt: Int
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(responseBody)
                val jsonArray = jsonObject.getJSONArray("items")
                runOnUiThread {     // thread 안에서 ui 바꾸고 싶으면 runonuithread
                   cnt =1

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        title = item.getString("title")
                        println("TITLE : $title")

                        text_mask.append(cnt.toString()+"번째\n")
                        text_mask.append(title+"\n")
                        cnt++

                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}

