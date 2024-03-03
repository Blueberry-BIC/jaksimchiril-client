package com.example.bicapplication.retrofit

import com.example.bicapplication.datamodel.*
import com.example.bicapplication.klaytn.*
import com.example.bicapplication.responseObject.ActionQuiz
import com.example.bicapplication.responseObject.BooleanResponse
import com.example.bicapplication.responseObject.UserPostResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface RetrofitInterface {
    //Retrofit 객체. 메모리 낭비 방지를 위해 singleton
    companion object RetrofitObject {
        //retrofit 객체 만들어서 사용할 때, 인자로 baseURL 넘겨주면 됨
        fun create(baseURL: String): RetrofitInterface {
            val gson:Gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitInterface::class.java)
        }
    }

    //수빈 method

    //admin wallet address method
    @GET("admin_wallet")
    fun getAdminWalletAddr(): Call<AdminWalletData>


    //참가하기
    @PUT("participate/{challId}")
    fun putUserList(
        @Path("challId") challId: String,
        @Body challData: ChallData
    ): Call<StringData>


    //user patch
    @PATCH("participate/{userId}")
    fun patchProgressChall(
        @Path("userId") userId: String,
        @Body challId: StringData
    ) : Call<StringData>

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
    ) : Call<ResultRespData>

    // kaikas send_klay
    @POST("prepare")
    fun reqSend(
        @Body sendinfo: AuthData
    ) : Call<PrepareRespData>

    @GET("result/{request_key}")
    fun sendResult(
        @Path("request_key") request_key:String
    ): Call<SendResultData>

    // challInfo update method
    @FormUrlEncoded
    @PUT("chall")
    fun putChallInfo(
//        @Body chall: ChallData
        @Field("challName") challName: String,
        @Field("challDesc") challDesc: String,
        @Field("isPublic") isPublic: Boolean,
        @Field("category") category: String,
        @Field("passwd") passwd: Int
    ) : Call<ChallData>

    @DELETE("chall/{challId}")
    fun deleteChallInfo(
        @Path("challId") challId: Int
    ) : Call<ChallData>

    // 유경 method


    // save userInfo method
    @POST("user/{userId}")
    fun setUserInfo(
        @Body user: UserData
    ) : Call<UserData>

    // edit(update) userInfo method
    @FormUrlEncoded
    @PUT("user/edit/{userId}")
    fun editUserInfo(
        @Field("prizeMoney") prizeMoney: Int
    ) : Call<UserData>

    // get challInfo method
    @GET("activated_chall")
    fun getActivatedChallInfo() : Call<ArrayList<Any>>

    // set challInfo method
    @POST("chall")
    fun postChallInfo(
        @Body challData: ChallData
    ) : Call<String>

    // get userInfo method
    @GET("user/{userId}")
    fun getUserInfo(
        @Path("userId") userId: Int
    ) : Call<UserData>

    // 민우 method

    //액션퀴즈 랜덤으로 하나 가져오기 요청
    @GET("action")
    fun getAction(): Call<ActionQuiz>

    //깃허브 기간내 나의 커밋여부 가져오기 요청
    @GET("github/{githubId}")
    fun getIsCommitted(@Path("githubId") githubId:String): Call<BooleanResponse>

    //신규 유저 등록 요청
    @POST("user/add")
    fun postUser(@Body users: UserData): Call<UserPostResponse>


}