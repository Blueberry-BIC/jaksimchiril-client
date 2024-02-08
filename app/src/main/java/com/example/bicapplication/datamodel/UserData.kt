package com.example.bicapplication.datamodel

data class UserData(
    var userId : Int?=null,
    var userName : String?=null,
    var walletAddr : String?=null,
    var githubId : String?=null,
    var prizeMoney : Int = 0,
    var stack1 : List<ChallData>? =null,
    var stack2 : List<ChallData>? =null,
    var stack3 : List<ChallData>? =null
) {
}
