package com.example.bicapplication.klaytn

data class ResultRespData(
    val expiration_time: Int,
    val request_key: String,
    val result: Result,
    val status: String,
    val type: String,
    val chain_id: String = "1001", //baobab testnet id
    val code: Int = 0,
    val message: Any? = null
) {
    data class Result(
        val signed_tx: String,
        val tx_hash: String
    )
}