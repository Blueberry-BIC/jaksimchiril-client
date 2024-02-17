package com.example.bicapplication.responseObject

import com.google.gson.annotations.SerializedName

//유저 등록 요청 후 서버에게 받는 response값
data class UserPostResponse(@SerializedName("userName") var userName:String, @SerializedName("userId") var userId:String){

}