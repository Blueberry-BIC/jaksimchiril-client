package com.example.bicapplication.klaytn

data class AuthData(
    val type: String,
    var bapp: Bapp,
    var callback: CallBack,
    val chain_id: String = "1001", // baobab testnet id
) {
    data class Bapp(
        val name: String
    )

    data class CallBack(
        val success: String = "bic://main",
        val fail: String = "bic://login"
    )
}