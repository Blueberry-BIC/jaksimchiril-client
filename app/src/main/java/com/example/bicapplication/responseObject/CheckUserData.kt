package com.example.bicapplication.responseObject

import com.google.gson.annotations.SerializedName

data class CheckUserData(
    @SerializedName("isparticipant") var is_participant:Boolean,
    @SerializedName("certified") var certified:Boolean
)
