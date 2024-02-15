package com.example.bicapplication.datamodel

import com.google.gson.annotations.SerializedName

data class UserData(
    var userId : String?=null,
    var userName : String?=null,
    @SerializedName (value = "wallet_addr")
    var walletAddr : String?=null,
    @SerializedName (value = "github_id")
    var githubId : String?=null,
    @SerializedName (value = "prize_money")
    var prizeMoney : Int = 0,
    var stack1 : List<String>? =null,
    var stack2 : List<String>? =null,
    var stack3 : List<String>? =null,
    var stack4 : List<String>? =null,
    @SerializedName (value = "progress_chall")
    var progressChall : List<String>? = null
) {
}
