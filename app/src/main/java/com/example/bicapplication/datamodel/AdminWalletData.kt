package com.example.bicapplication.datamodel

import com.google.gson.annotations.SerializedName

/*data class AdminWalletData(
    @SerializedName(value="_id")
    val id: String,

    @SerializedName(value="wallet_addr")
    val walletAddr: String
)*/

data class AdminWalletData(
    val walletAddr: String
)

