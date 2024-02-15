package com.example.bicapplication.datamodel

import com.google.gson.annotations.SerializedName
import java.util.*

data class ChallData(
    @SerializedName("challId") var challId : String? = null,
    @SerializedName("challName") var challName : String,
    @SerializedName("startdate") var startdate : String,
    @SerializedName("enddate") var enddate : String,
    @SerializedName("authMethod") var authMethod : Int,
    @SerializedName("challDesc") var challDesc : String,
    @SerializedName("isPublic") var isPublic : Boolean,
    @SerializedName("category") var category : String,
    @SerializedName("passwd") var passwd : Int = 0,
    @SerializedName("money") var money : Int = 0,
    @SerializedName("userNum") var userNum : Int = 0,
    @SerializedName("userList") var userList : List<Int>? = null,
    @SerializedName("totalDays") var totalDays : Long,
    @SerializedName("isSuccess") var isSuccess : List<Int>? = null,
    @SerializedName("isProgress") var isProgress : Int = 0
) {
    companion object : DataModelCreateInterface<ChallData> {
        override fun getDefault(): ChallData
            = ChallData("", "", "", "", 1, "", true, "", 0, 0, 0, null, 0,null, 0)

    }
}
