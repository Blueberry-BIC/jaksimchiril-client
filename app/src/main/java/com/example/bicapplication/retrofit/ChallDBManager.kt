package com.example.bicapplication.retrofit

import android.util.Log
import com.example.bicapplication.datamodel.ChallData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallDBManager (private val baseurl: String = "http://10.0.2.2:8081/") {
    val retrofitInterface = RetrofitInterface.create(baseurl)

    // 진행중인 챌린지 정보 가져오기
    // is_progress가 0이면 시작 전인 챌린지, 1이면 진행중인 챌린지
    fun getActivedChallInfo(is_progress: Int): ArrayList<ChallData> {
        var challdata: ArrayList<ChallData> = ArrayList()

        retrofitInterface.getActivatedChallInfo().enqueue(object : Callback<ArrayList<ChallData>> {
            override fun onResponse(
                call: Call<ArrayList<ChallData>>,
                response: Response<ArrayList<ChallData>>
            ) {
                if (response.isSuccessful) {
                    Log.d("CHALL", "success getActivatedChall1 ${response.body()}")
                    challdata = response.body()!!
                }
                else {
                    Log.d("CHALL", "success getActivatedChall2 ${response.errorBody()}")
                }

            }

            override fun onFailure(call: Call<ArrayList<ChallData>>, t: Throwable) {
                Log.d("CHALL", "fail getActivatedChall ${t}")
            }
        })

        return challdata
    }

    // 진행중인 챌린지 정보 가져오기
    fun getCompletedChallInfo() {

    }

    // 새로운 챌린지 추가 (챌린지 개설하기)
    fun postChallInfo(challdata: ChallData): Boolean {
        var result: Boolean = false
        retrofitInterface.postChallInfo(challdata).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("CHALL", "success saveChall1 ${response.body()}")
                    result = true
                }
                else {
                    Log.d("CHALL", "success saveChall2 ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("CHALL", "fail saveChall ${t}")
            }
        })

        return result
    }

    // 챌린지 DB에 사용자 추가 (챌린지 참여하기)
    fun addUserToChall() {

    }

    // 챌린지 DB에서 사용자 삭제 (챌린지 참여 취소)
    fun removeUserFromChall() {

    }

}