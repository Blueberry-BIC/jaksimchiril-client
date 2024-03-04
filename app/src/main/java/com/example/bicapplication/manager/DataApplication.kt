package com.example.bicapplication.manager

import android.app.Application
import android.content.Context

class DataApplication(): Application() {
    private lateinit var dataStore: DataStoreModule

//    companion object {
//        private lateinit var dataApplication: DataApplication
//        fun getInstance(): DataApplication = dataApplication
//    }

    override fun onCreate() {
        super.onCreate()
        dataApplication = this
        dataStore = DataStoreModule(this)
    }

    fun getDataStore(): DataStoreModule = dataStore

    companion object {
        private lateinit var dataApplication: DataApplication
        fun getInstance(): DataApplication = dataApplication
    }
}