package com.example.bicapplication.manager

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreModule(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val WALLET_ADDR = stringPreferencesKey("wallet_addr")
        private val GITHUB_ID =  stringPreferencesKey("github_id")
        private val LOGIN_CHECK = booleanPreferencesKey("login_check")
    }

    val userIdData: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[USER_ID] ?: ""
        }

    val userNameData: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[USER_NAME] ?: ""
        }

    val walletAddrData: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[WALLET_ADDR] ?: ""
        }


    val githubIdData: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[GITHUB_ID] ?: ""
        }

    val isLogin: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[LOGIN_CHECK] ?: false
        }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            Log.d("datastore", userId)
        }
    }

    suspend fun saveUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
            Log.d("datastore", userName)
        }
    }

    suspend fun saveWalletAddr(walletAddr: String) {
        context.dataStore.edit { preferences ->
            preferences[WALLET_ADDR] = walletAddr
        }
        Log.d("datastore", walletAddr)
    }

    suspend fun saveGithubId(githubId: String) {
        context.dataStore.edit { preferences ->
            preferences[GITHUB_ID] = githubId
        }
        Log.d("datastore", githubId)
    }

    suspend fun changeLoginState(state: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOGIN_CHECK] = state
        }
        Log.d("datastore", state.toString())
    }

}