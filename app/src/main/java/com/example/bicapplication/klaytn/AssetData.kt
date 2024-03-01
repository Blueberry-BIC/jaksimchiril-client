package com.example.bicapplication.klaytn

data class AssetData(
    val type: String,
    var bapp: Bapp,
    var watch_asset: AssetData?= null,
    val chain_id: String = "1001", // baobab testnet id
){
    data class Bapp(
        val name: String
    )

    data class SignData(
        var message: String? = null
    )

    data class AssetData(
        var address: String? = null,
        var symbol: String = "KSD",
        var decimals: Int = 18,
        var name: String = "KLAY"
    )
}
