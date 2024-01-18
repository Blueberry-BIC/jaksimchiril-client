package com.example.bicapplication.mychall

//챌린지 하나의 정보 담고있는 객체 (챌린지 제목, 인증방식, 참여자수, 만료기간 )
data class Challenge(var stack: String, var title: String, var certifyMethod: String, var number: String, var period:String )
