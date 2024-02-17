package com.example.bicapplication.klaytn

data class AuthData(
    val type: String,
    var bapp: Bapp,
    val chain_id: String = "1001", // baobab testnet id
) {
    data class Bapp(
        val name: String
    )
}