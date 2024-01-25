package com.example.bicapplication.klaytn

data class PrepareRespData(
    val chain_id: String,
    val request_key: String,
    val status: String,
    val expiration_time: Int
)