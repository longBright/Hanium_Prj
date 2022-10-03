package com.avengers.maskfitting.mafiafin.main.board.bulletinBoard

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.AdapterView
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.FragmentBoardBinding
import com.avengers.maskfitting.mafiafin.main.MainActivity
import com.avengers.maskfitting.mafiafin.main.board.qnaBoard.QnABoard

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

        // 아이템 추가
        adapter.addItem("코로나 거리두기",
            "코로나 거리두기 풀린지도 좀 됐네요.. 다들 목욕탕은 예전처럼 잘 다니시나요?! 탕이나 실내에서도 마스크를 써야 할지 고민되네요 ㅜㅜ 예전처럼 편하게 즐겨도 될까요..")
        adapter.addItem("요양병원 대면면회",
            "요양병원 대면면회 이제부터 허용된다는 기사 다들 보셨나요? 내년 3월부터는 실내 마스크도 벗을 수 있다던데 다들 기사 한 번 확인해 보세요!!" +
                    "\nhttps://n.news.naver.com/article/056/0011349337?sid=102")
        adapter.addItem("마스크 앞뒤", "일회용 마스크 앞뒤 있다는거 나만 몰랐나ㅠㅠ 주름 방향으로 구분하는게 가장 정확하다던데 코시국 n년차지만 어렵다 어려워 ")
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

        return binding.root
    }

    companion object {
    }
}