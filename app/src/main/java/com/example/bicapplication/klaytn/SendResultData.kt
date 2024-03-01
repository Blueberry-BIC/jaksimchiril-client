package com.example.bicapplication.klaytn

import com.google.gson.annotations.SerializedName

data class SendResultData(
    @SerializedName("expiration_time")val expiration_time: Int,
    @SerializedName("request_key")val request_key: String,
    @SerializedName("result")val result: SendResult,
    @SerializedName("status")val status: String,
    @SerializedName("type")val type: String,
    @SerializedName("chain_id")val chain_id: String = "1001", //baobab testnet id
){

}

data class SendResult(
    val signed_tax: String?= null,
    val tx_hash: String? = null
)
