package com.example.bicapplication.klaytn

import android.view.SurfaceControl.Transaction
import com.example.bicapplication.datamodel.AdminWalletData

data class AuthData(
    val type: String,
    var bapp: Bapp,
    var transaction: TransactionData?= null,
    val chain_id: String = "1001", // baobab testnet id
) {
    data class Bapp(
        val name: String
    )

    data class TransactionData(
        var to:String,
        var amount:String
    )
}