package com.example.bicapplication.responseObject

import com.google.gson.annotations.SerializedName

//성공실패 여부 등의  esponse값을 서버로부터 가져올때 사용하는 객체
//깃허브 크롤링 커밋여부 응답값에 사용
data class BooleanResponse(
    @SerializedName("is_committed") var is_committed:Boolean,
    @SerializedName("lastcommitday") var lastcommitday:String,
    @SerializedName("commitRepo") var commitRepo:String,
)