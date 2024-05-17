package com.example.bicapplication.datamodel

import com.google.gson.annotations.SerializedName

data class ImageData(
    @SerializedName("challId") var challId : String? = null,
    @SerializedName("userId") var userId : String? = null,
    @SerializedName("imagePath") var imagePath : String? = null
) {
}