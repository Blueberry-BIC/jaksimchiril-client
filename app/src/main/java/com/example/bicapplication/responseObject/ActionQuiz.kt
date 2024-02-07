package com.example.bicapplication.responseObject


import com.google.gson.annotations.SerializedName
import java.util.*

//서버에서 보낸 액션퀴즈 담을 객체
data class ActionQuiz(
    @SerializedName("result") var result: List<Any>,

)

//data class ActionQuiz(
//    @SerializedName("_id") var id: Int,
//    @SerializedName("category") var category:String,
//    @SerializedName("limited_time") var limited_time: Date,
//    @SerializedName("problem") var problem: String,
//    @SerializedName("answer") var answer: String,
//)