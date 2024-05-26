package com.example.bicapplication.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bicapplication.R
import com.example.bicapplication.databinding.ItemChallengeBinding
import com.example.bicapplication.datamodel.ChallData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChallListAdapter(val challList: ArrayList<ChallData>, val onClick: (ChallData) -> (Unit)) : RecyclerView.Adapter<ChallListAdapter.ChallListHolder>() {

    inner class ChallListHolder(val binding: ItemChallengeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(challData: ChallData) {
            binding.apply {
                nameTextView.text = challData.challName
                stackTextView.text = challData.category
                contentsTextView.text = challData.challDesc
                numberTextView.text = "참여자: " + challData.userNum.toString() + "명"
                var today = Calendar.getInstance()
                var df = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
                var dday = (today.time.time - df.parse(challData.startdate).time) / (60*60*24*1000)
                if (dday >= 0){
                    periodTextView.text = "D+" + dday
                } else {
                    periodTextView.text = "D" + dday
                }

                when(challData.category){
                    "시사/교양" -> itemChallCardview.setBackgroundResource(R.drawable.light_pink_gradient)
                    "신체 단련" -> {
                        itemChallCardview.setBackgroundResource(R.drawable.purple_gradient)
                        contentsTextView.setTextColor(Color.parseColor("#FFFFFF"))
                        stackTextView.setTextColor(Color.parseColor("#FFFFFF"))
                        numberTextView.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    "생활" -> {
                        itemChallCardview.setBackgroundResource(R.drawable.pink_gradient)
                        contentsTextView.setTextColor(Color.parseColor("#FFFFFF"))
                        stackTextView.setTextColor(Color.parseColor("#FFFFFF"))
                        numberTextView.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    else -> itemChallCardview.setBackgroundResource(R.drawable.light_purple_gradient)
                }

            }

            binding.root.setOnClickListener {
                onClick(challData)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallListHolder {
        val binding = ItemChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallListHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallListHolder, position: Int) {
        holder.bind(challList[position])
    }

    override fun getItemCount(): Int {
        return challList.size
    }

}