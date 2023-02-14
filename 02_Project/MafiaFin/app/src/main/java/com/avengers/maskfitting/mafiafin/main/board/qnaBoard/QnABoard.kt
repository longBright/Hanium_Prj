package com.avengers.maskfitting.mafiafin.main.board.qnaBoard

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.AdapterView
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.databinding.FragmentBoardBinding
import com.avengers.maskfitting.mafiafin.databinding.FragmentQaBoardBinding
import com.avengers.maskfitting.mafiafin.main.MainActivity
import com.avengers.maskfitting.mafiafin.main.board.bulletinBoard.BulletinBoard

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class QnABoard : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentQaBoardBinding

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
        binding = FragmentQaBoardBinding.inflate(inflater, container, false)

        // Adapter 생성
        val adapter: ListViewAdapter = ListViewAdapter()

        // 리스트뷰 참조 및 Adapter달기
        val listview: ListView = binding.list
        listview.adapter = adapter

        //아이템 추가
        adapter.addItem("마스크 주문제작", "인쇄용 주문제작 마스크 AA회사 이용해보신 분 있으신가요?")
        adapter.addItem("헬스장 다녀보려고 하는데", "헬스할 때 사용할 마스크 추천하는거 있으신 분")
        adapter.addItem("아기들 마스크", "귀여운 캐릭터 그려진 아기들 마스크 추천 부탁드려요~")
        adapter.addItem("엘레베이터","다들 엘레베이터도 실내라고 생각하시나요?\n" +
                "저는 실내라고 생각하는데 요즘 엘레베이터에 마스크를 착용하지 않고 타시는 분들이 꽤 많더라구요? 다들 어떻게 생각하세요")
        adapter.addItem("축제 즐길때","요즘 여러 축제들이 다시 열리기 시작했는데 다들 마스크 벗고 다니시나요? 벗고 다녀도 되지만 아직 불안해서 ㅜㅜ")


        // listview에 클릭 이벤트 핸들러 정의.
        listview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as ListViewItem

            val title = item.titleStr
            val content = item.contentStr
        }

        //버튼 클릭-> 자유게시판으로 전환
        binding.board1.setOnClickListener {
            val bulletinBoard: Fragment = BulletinBoard()
            val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_container, bulletinBoard).commit()
        }

        return binding.root
    }

    companion object {
    }
}