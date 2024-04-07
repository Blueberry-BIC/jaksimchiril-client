package com.example.bicapplication.responseObject

import com.google.gson.annotations.SerializedName

// user db에 있는 유저인지 확인
data class UserBooleanResponse(
    @SerializedName("existUser") var existUser:Boolean
)
