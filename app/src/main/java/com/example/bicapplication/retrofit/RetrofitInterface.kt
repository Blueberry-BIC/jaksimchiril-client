package com.example.bicapplication.retrofit

import com.example.bicapplication.klaytn.AuthData
import com.example.bicapplication.klaytn.AuthResultData
import com.example.bicapplication.klaytn.PrepareRespData
import com.example.bicapplication.klaytn.ResultRespData
import com.example.bicapplication.responseObject.ActionQuiz
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RetrofitInterface {
    //Retrofit 객체. 메모리 낭비 방지를 위해 singleton
    companion object RetrofitObject {
        //retrofit 객체 만들어서 사용할 때, 인자로 baseURL 넘겨주면 됨
        fun create(baseURL: String): RetrofitInterface {
            val gson:Gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitInterface::class.java)
        }
    }

    //kaikas method
    //kaikas prepare-Auth step method
    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("prepare")
    fun requestAuth(
        @Body authinfo: AuthData
    ) : Call<PrepareRespData>

    //kaikas result step method
    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("result/{request_key}")
    fun requestResult(
        @Path("request_key") request_key:String
    ) : Call<AuthResultData>


    // 유경 method








    // 민우 method

    //액션퀴즈 랜덤으로 하나 가져오기 요청
    @GET("action")
    fun requestAction(): Call<ActionQuiz>




}