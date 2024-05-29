package com.example.bicapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bicapplication.R
import com.example.bicapplication.ShowPrizeStatusActivity
import com.example.bicapplication.databinding.ItemMyhistoryBinding
import com.example.bicapplication.datamodel.ChallData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyhistoryAdapter(val completedChallList: ArrayList<ChallData>, val onClick: (ChallData) -> (Unit)): RecyclerView.Adapter<MyhistoryAdapter.MyhistoryViewHolder>() {
    inner class MyhistoryViewHolder(val binding: ItemMyhistoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(completedData: ChallData){
            binding.apply {
                itemMyhistoryChallname.text = completedData.challName
                when(completedData.authMethod){
                    1 -> itemMyhistoryAuth.text = "이미지 인증"
                    2 -> itemMyhistoryAuth.text = "깃허브 인증"
                    3 -> itemMyhistoryAuth.text = "액션 인증"
                    4 -> itemMyhistoryAuth.text = "걸음수 인증"
                    5 -> itemMyhistoryAuth.text = "시사뉴스 인증"
                }
                itemMyhistoryParticipant.text = "참가인원 " + completedData.userNum.toString() +"명"
                itemMyhistoryStartdate.text = "시작일 " + completedData.startdate

                when(completedData.category){
                    "시사/교양" -> itemMyhistoryInnercardview.setBackgroundResource(R.drawable.light_pink_gradient)
                    "신체 단련" -> itemMyhistoryInnercardview.setBackgroundResource(R.drawable.purple_gradient)
                    "생활" -> itemMyhistoryInnercardview.setBackgroundResource(R.drawable.pink_gradient)
                    else -> itemMyhistoryInnercardview.setBackgroundResource(R.drawable.light_purple_gradient)
                }

                myhistoryCard.setOnClickListener {
                    val intent = Intent(binding.root.context, ShowPrizeStatusActivity::class.java)
                    intent.run{ binding.root.context.startActivity(this)}
                }

                root.setOnClickListener {
                    onClick(completedData)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return completedChallList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyhistoryViewHolder {
        val binding = ItemMyhistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyhistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyhistoryViewHolder, position: Int) {
        holder.bind(completedChallList[position])
    }
}