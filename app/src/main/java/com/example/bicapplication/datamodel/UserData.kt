package com.example.bicapplication.datamodel

data class UserData(
    var userName : String?=null,
    var walletAddr : String?=null,
    var githubId : String?=null,
    var userId : String?=null,
    var prizeMoney : Int = 0,  //총 얻은 상금값
    var stack1 : List<String>? =null,  //완료한 코딩 챌린지들
    var stack2 : List<String>? =null,  //완료한  시사교양  챌린지들
    var stack3 : List<String>? =null,  //완료한 신체단련 챌린지들
    var stack4 : List<String>? =null,  //완료한 생활 챌린지들
    var progress_chall :List<String>? =null //유저가 현재 진행중인 챌린지 목록
) {
}
