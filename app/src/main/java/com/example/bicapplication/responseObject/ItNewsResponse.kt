package com.example.bicapplication.responseObject

import com.google.gson.annotations.SerializedName

//서버가 보낸 it뉴스정보 json값 받는 객체
data class ItNewsResponse(
    @SerializedName("title") var title:String,
    @SerializedName("contents") var contents:String,
    @SerializedName("media") var media:String,
    @SerializedName("time") var time:String,
    @SerializedName("url") var url:String,
)