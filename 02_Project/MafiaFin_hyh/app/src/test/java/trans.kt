import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat


class ListViewBtnAdapter internal constructor(
    context: Context?, // 생성자로부터 전달된 resource id 값을 저장.
    var resourceId: Int, list: ArrayList<ListViewBtnItem?>?, // 생성자로부터 전달된 ListBtnClickListener  저장.
    private val listBtnClickListener: ListBtnClickListener?
) :
    ArrayAdapter<Any?>(context!!, resourceId, list!!), View.OnClickListener {
    // 버튼 클릭 이벤트를 위한 Listener 인터페이스 정의.
    interface ListBtnClickListener {
        fun onListBtnClick(position: Int)
    }

    // 새롭게 만든 Layout을 위한 View를 생성하는 코드
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val context = parent.context

        // 생성자로부터 저장된 resourceId(listview_btn_item)에 해당하는 Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(resourceId /*R.layout.listview_btn_item*/, parent, false)
        }

        // 화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
        val iconImageView = convertView!!.findViewById<View>(R.id.imageView1) as ImageView
        val textTextView = convertView.findViewById<View>(R.id.textView1) as TextView

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem: ListViewBtnItem? = getItem(position) as ListViewBtnItem?

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon())
        textTextView.setText(listViewItem.getText())

        // button1 클릭 시 TextView(textView1)의 내용 변경.
        val button1 = convertView.findViewById<View>(R.id.button1) as Button
        button1.setOnClickListener {
            textTextView.text = Integer.toString(position + 1) + "번 아이템 선택."
        }

        // button2의 TAG에 position값 지정. Adapter를 click listener로 지정.
        val button2 = convertView.findViewById<View>(R.id.button2) as Button
        button2.tag = position
        button2.setOnClickListener(this)
        return convertView
    }

    // button2가 눌려졌을 때 실행되는 onClick함수.
    override fun onClick(v: View) {
        // ListBtnClickListener(MainActivity)의 onListBtnClick() 함수 호출.
        if (listBtnClickListener != null) {
            listBtnClickListener.onListBtnClick(v.tag as Int)
        }
    }

    // ListViewBtnAdapter 생성자. 마지막에 ListBtnClickListener 추가.
    init {

        // resource id 값 복사. (super로 전달된 resource를 참조할 방법이 없음.)
        listBtnClickListener = listBtnClickListener
    }
}


fun loadItemsFromDB(list: ArrayList<ListViewBtnItem?>?): Boolean {
    var list: ArrayList<ListViewBtnItem?>? = list
    var item: ListViewBtnItem
    var i: Int
    if (list == null) {
        list = ArrayList<ListViewBtnItem?>()
    }

    // 순서를 위한 i 값을 1로 초기화.
    i = 1

    // 아이템 생성.
    item = ListViewBtnItem()
    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_account_box_black_36dp))
    item.setText(Integer.toString(i) + "번 아이템입니다.")
    list!!.add(item)
    i++
    item = ListViewBtnItem()
    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_account_circle_black_36dp))
    item.setText(Integer.toString(i) + "번 아이템입니다.")
    list.add(item)
    i++
    item = ListViewBtnItem()
    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_language_black_36dp))
    item.setText(Integer.toString(i) + "번 아이템입니다.")
    list.add(item)
    i++
    item = ListViewBtnItem()
    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_lightbulb_outline_black_36dp))
    item.setText(Integer.toString(i) + "번 아이템입니다.")
    list.add(item)
    return true
}