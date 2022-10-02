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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [Board.newInstance] factory method to
 * create an instance of this fragment
 */
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

        // 첫 번째 아이템 추가.
        adapter.addItem("user1", "테스트 입니다")
        // 두 번째 아이템 추가.
        adapter.addItem("user2", "테스트 입니다")
        // 세 번째 아이템 추가.
        adapter.addItem("user3", "테스트 입니다")


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