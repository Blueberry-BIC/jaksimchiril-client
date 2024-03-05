package com.example.bicapplication.retrofit

import android.annotation.SuppressLint
import android.util.Log
import com.example.bicapplication.GlobalVari
import com.example.bicapplication.datamodel.ChallData
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallDBManager (private val baseurl: String = GlobalVari.getUrl()) {
    val retrofitInterface = RetrofitInterface.create(baseurl)

    // 진행중인 챌린지 정보 가져오기
    // is_progress가 0이면 시작 전인 챌린지, 1이면 진행중인 챌린지
    // getActivedChallInfo는 안 쓰임
    @SuppressLint("SuspiciousIndentation")
    fun getActivedChallInfo(is_progress: Int): ArrayList<ChallData> {
        var challDataArray: ArrayList<ChallData> = ArrayList()

        retrofitInterface.getActivatedChallInfo().enqueue(object : Callback<ArrayList<Any>> {
            override fun onResponse(
                call: Call<ArrayList<Any>>,
                response: Response<ArrayList<Any>>
            ) {
                if (response.isSuccessful) {
                    Log.d("CHALL", "success getActivatedChall1 ${response.body()}")

                    val challArray = JSONArray(response.body())
                    Log.d("CHALL", "challdata length: ${challArray.length()}")

                    for (i in 0 until challArray.length()) {
                        var data = challArray.getJSONObject(i)
                        Log.d("CHALL", "data ${i}: ${data}")

                        var challData = ChallData.getDefault()
                        challData.challId = data.getString("_id")
                        challData.challName = data.getString("chall_name")
                        challData.challDesc = data.getString("chall_desc")
                        challData.startdate = data.getString("start_date").substring(0, 10)
                        challData.enddate = data.getString("end_date").substring(0, 10)
                        challData.authMethod = data.getInt("auth_method")
                        challData.isPublic = data.getBoolean("is_public")
                        challData.category = data.getString("category")
                        challData.passwd = data.getInt("passwd")
                        challData.userNum = data.getInt("user_num")
                        challData.totalDays = data.getLong("total_days")

                        challDataArray.add(challData)
                    }

                } else {
                    Log.d("CHALL", "success getActivatedChall2 ${response.errorBody()}")
                }

            }

            override fun onFailure(call: Call<ArrayList<Any>>, t: Throwable) {
                Log.d("CHALL", "fail getActivatedChall ${t}")
            }
        })


        Log.d("CHALL", "final challArray: ${challDataArray}")
        return challDataArray
    }

    // 완료된 챌린지 정보 가져오기
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