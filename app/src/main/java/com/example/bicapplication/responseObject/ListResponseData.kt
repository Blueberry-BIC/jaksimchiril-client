package com.example.bicapplication.responseObject


import com.google.gson.annotations.SerializedName
import java.util.*


// List<Any>타입으로 서버로부터 오는 json객체 파싱을 위한 클래스
//응답으로 받는 주요 객체: 액션퀴즈, 유저정보
data class ListResponseData(
    @SerializedName("result") var result: List<Any>,
)



//data class ActionQuiz(
//    @SerializedName("_id") var id: Int,
//    @SerializedName("category") var category:String,
//    @SerializedName("limited_time") var limited_time: Date,
//    @SerializedName("problem") var problem: String,
//    @SerializedName("answer") var answer: String,
//)