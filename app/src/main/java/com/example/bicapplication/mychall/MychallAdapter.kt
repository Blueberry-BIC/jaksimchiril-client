package com.example.bicapplication.mychall

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.bicapplication.R
import com.example.bicapplication.certify.ActionCertifyActivity
import com.example.bicapplication.certify.CertifyStatusActivity


class MychallAdapter(
    private var context: Context?,
    private var ChallengeList: Array<Challenge>?
) : BaseAdapter() {

    //뷰들을 어떻게 보여줄지, 레이아웃 어떻게 나타낼지 설정 코드
    @SuppressLint("ViewHolder", "InflateParams", "MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view:View?= null

        //convertView : 메모리를 위해 new 대신 재사용하는 뷰
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_challenge, null)
        } else {
            view = convertView
        }

        val item = ChallengeList?.get(position)
        view?.findViewById<TextView>(R.id.stackTextView)?.text = item?.stack  //스택
        view?.findViewById<TextView>(R.id.nameTextView)?.text = item?.title  //제목
        view?.findViewById<TextView>(R.id.contentsTextView)?.text = item?.certifyMethod  //인증방식
        view?.findViewById<TextView>(R.id.numberTextView)?.text = item?.number //참여자수
        view?.findViewById<TextView>(R.id.periodTextView)?.text = item?.period //만료기간

        //그리드뷰의 챌린지뷰 하나 클릭시 인증페이지 이동 (챌린지 id, 챌린지 종료기간 데이터 전송)
        view?.setOnClickListener {
            val intent = Intent(context, CertifyStatusActivity::class.java)
            context?.startActivity(intent)
        }



        return view
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return ChallengeList!!.size
    }

}