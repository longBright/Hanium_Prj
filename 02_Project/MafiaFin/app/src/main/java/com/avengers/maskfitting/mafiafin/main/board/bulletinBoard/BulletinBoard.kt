package com.avengers.maskfitting.mafiafin.main.board.bulletinBoard

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.FragmentBoardBinding
import com.avengers.maskfitting.mafiafin.main.MainActivity
import com.avengers.maskfitting.mafiafin.main.alarm.PurchaseAlertActivity
import com.avengers.maskfitting.mafiafin.main.board.qnaBoard.QnABoard
import com.avengers.maskfitting.mafiafin.main.board.bulletinBoard.BulletinBoardRegisterationActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_mask_alert_list.*
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.main.mypage.MyPage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Board.newInstance] factory method to
 * create an instance of this fragment.
 */
class BulletinBoard : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var email: String? = ""

    private lateinit var binding: FragmentBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBoardBinding.inflate(inflater, container, false)
        // Adapter 생성
        val adapter: ListViewAdapter = ListViewAdapter()

        // 리스트뷰 참조 및 Adapter달기
        val listview: ListView = binding.list
        listview.adapter = adapter

        if (MyPage.account == null) {
            // 로그인 중인 사용자 정보 획득
            MyPage.userEmail =
                (activity as MainActivity).preferences.getString("email", "")       // 사용자 이메일 초기화
            //Log.d("MyPage", "email : ${email}")
        }
        // 구글 회원
        else {
            MyPage.userEmail = MyPage.account?.email      // 사용자 이메일 초기화
        }

        // 아이템 추가
        adapter.addItem(
            "코로나 거리두기",
            "코로나 거리두기 풀린지도 좀 됐네요.. 다들 목욕탕은 예전처럼 잘 다니시나요?! 탕이나 실내에서도 마스크를 써야 할지 고민되네요 ㅜㅜ 예전처럼 편하게 즐겨도 될까요.."
        )
        adapter.addItem(
            "요양병원 대면면회",
            "요양병원 대면면회 이제부터 허용된다는 기사 다들 보셨나요? 내년 3월부터는 실내 마스크도 벗을 수 있다던데 다들 기사 한 번 확인해 보세요!!" +
                    "\nhttps://n.news.naver.com/article/056/0011349337?sid=102"
        )
        adapter.addItem(
            "마스크 앞뒤",
            "일회용 마스크 앞뒤 있다는거 나만 몰랐나ㅠㅠ 주름 방향으로 구분하는게 가장 정확하다던데 코시국 n년차지만 어렵다 어려워 "
        )
        adapter.addItem("퍼스널컬러", " 남자라 퍼스널컬러 같은건 잘 모르고 살았는데 마스크 퍼스널컬러 맞춰서 끼면 얼굴 좀 환해보이려나")
        adapter.addItem("내 피부 ㅜ", "실내 마스크 제한도 풀려야 내 피부는 돌아오려나...볼쪽 피부가 완전 난리다")


        // listview에 클릭 이벤트 핸들러 정의.
        listview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as ListViewItem

            val title = item.titleStr
            val content = item.contentStr
        }

        //버튼 클릭-> Q&A 게시판
        binding.board2.setOnClickListener {
            val qnaBoard: Fragment = QnABoard()
            val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_container, qnaBoard).commit()
        }

        //버튼 클릭-> 글쓰기 화면
        binding.contentBtn.setOnClickListener {
            activity?.let{
                val intent = Intent(context, BulletinBoardRegisterationActivity::class.java )
                startActivity(intent)
            }
        }

        return binding.root


        val url = "http://43.200.115.71/BulletinBoardData.php"
//        val nameArr = ArrayList<String>()

        // get json object from url using volley network library
        //progressBar.visibility = View.VISIBLE

        var title = ""
        var content = ""

        // request json object response from the provided url
        val request = JsonObjectRequest(
            Request.Method.GET, // method
            url, // url
            null, // json request
            { response -> // response listener
                try {
                    val obj: JSONObject = response
                    val array = obj.getJSONArray("response")


                    textView.text = ""
                    // loop through the array elements
                    for (i in 0 until array.length()) {
                        // get current json object as student instance
                        val bulletinBoardData: JSONObject = array.getJSONObject(i)
                        // get the current student (json object) data
                        title = bulletinBoardData.getString("title")
                        content = bulletinBoardData.getString("content")
                        //display the formatted json data in text view


                        // 상세 조회로 이동 / 값 intent 전달
//                    listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
//                        val intent = Intent(this, Activity::class.java)
//                        intent.putExtra("content", items[position].content)
//                        intent.putExtra("title", items[position].title)
//                        startActivity(intent)
//                      }
                        // adapter.notifyDataSetChanged()   //변경내용 반영
                    }
                } catch (e: JSONException) {
                    textView.text = e.message
                }
                progressBar.visibility = View.INVISIBLE
            },
            { error -> // error listener
                textView.text = error.message
                progressBar.visibility = View.INVISIBLE
            }
        )
    }
}