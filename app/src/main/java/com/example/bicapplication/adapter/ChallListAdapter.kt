package com.example.bicapplication.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bicapplication.databinding.ItemChallengeBinding
import com.example.bicapplication.datamodel.ChallData

class ChallListAdapter(val challList: ArrayList<ChallData>, val onClick: (ChallData) -> (Unit)) : RecyclerView.Adapter<ChallListAdapter.ChallListHolder>() {

    inner class ChallListHolder(val binding: ItemChallengeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(challData: ChallData) {
            binding.stackTextView.text = challData.category
            binding.nameTextView.text = challData.challName
            binding.contentsTextView.text = challData.challDesc
            binding.numberTextView.text = challData.userNum.toString()
            binding.periodTextView.text = challData.enddate

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