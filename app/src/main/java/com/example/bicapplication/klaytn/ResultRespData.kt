package com.example.bicapplication.klaytn

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.util.Objects

//data class ResultRespData(
//    val expiration_time: Int,
//    val request_key: String,
//    val result: Result,
//    val status: String,
//    val type: String,
//    val chain_id: String = "1001", //baobab testnet id
//    val code: Int = 0,
//    val message: Any? = null
//) {
//    data class Result(
//        val signed_tx: String,
//        val tx_hash: String
//    )
//}


data class ResultRespData(
    @SerializedName("expiration_time")val expiration_time: Int,
    @SerializedName("request_key")val request_key: String,
    @SerializedName("result")val result: Result,
    @SerializedName("status")val status: String,
    @SerializedName("type")val type: String,
    @SerializedName("chain_id")val chain_id: String = "1001", //baobab testnet id

) {

}
data class Result(
    val klaytn_address: String? = null,
    val signed_tax: String?= null,
    val tx_hash: String? = null
)
