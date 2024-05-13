package com.example.bicapplication

//액티비티간 전역변수 존재하는 클래스
class GlobalVari{
    //GlobalVari 클래스의 모든 객체가 함께 공유하는 baseurl 변수
    companion object {
        private var baseurl:String = "http://192.168.86.6:8081/"    //192.168.0.104
        fun getUrl() : String{
            return baseurl
        }
    }
}