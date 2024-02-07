package com.example.bicapplication.klaytn

data class AuthResultData(
    val chain_id: String,
    val expiration_time: Int,
    val request_key: String,
    val result: Result,
    val status: String,
    val type: String
) {
    data class Result(
        val klaytn_address: String
    )
}