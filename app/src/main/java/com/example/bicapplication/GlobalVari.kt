package com.example.bicapplication

//액티비티간 전역변수 존재하는 클래스
class GlobalVari{
    //GlobalVari 클래스의 모든 객체가 함께 공유하는 baseurl 변수
    companion object {
        private var baseurl:String = "http://10.0.2.2:8081/"
        fun getUrl() : String{
            return baseurl
        }
    }
}