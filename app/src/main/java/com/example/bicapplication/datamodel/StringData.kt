package com.example.bicapplication.datamodel

import com.google.gson.annotations.SerializedName

data class StringData(
    @SerializedName(value="data") var stringData: String
)
