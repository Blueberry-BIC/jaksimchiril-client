package com.example.bicapplication.certify

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.bicapplication.R

//진행중인 챌린지 인증현황 페이지의 gridView를 위한 어댑터
class CertifyStatusAdapter(
    private var context: CertifyStatusActivity,
    private var certifyList: ArrayList<CertifyData>?
    ) : BaseAdapter() {

    //뷰들을 어떻게 보여줄지, 레이아웃 어떻게 나타낼지 설정 코드
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view:View?= null

        //convertView : 메모리를 위해 new 대신 재사용하는 뷰
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_certifystatus, null)
        } else {
            view = convertView
        }

        val item = certifyList?.get(position)
        view?.findViewById<TextView>(R.id.ItemTextView)?.text = item?.text
        view?.findViewById<TextView>(R.id.ItemTextView2)?.text = item?.text2

        //view?.findViewById<ImageView>(R.id.ItemImageView)?.setImageResource(item!!.img)

        return view
    }


    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return certifyList!!.size
    }

}