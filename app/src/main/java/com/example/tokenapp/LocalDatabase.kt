package com.example.tokenapp

import android.content.Context
import android.content.SharedPreferences


class LocalDatabase(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("TestApp", Context.MODE_PRIVATE)

    private val editor = prefs.edit()

    companion object {
        const val USER_NUMBER = "number"
        const val ACCESS = "access"
        const val REFRESH = "refresh"
    }

    fun saveAccessToken(token: String?) {
        editor.putString(ACCESS, token)
        editor.apply()
    }

    fun saveRefreshToken(token: String?) {
        editor.putString(REFRESH, token)
        editor.apply()
    }

    fun saveUserNumber(number: Int?) {
        if (number != null) {
            editor.putInt(USER_NUMBER, number)
            editor.apply()
        }
    }

    fun fetchAccessToken(): String? {
        return prefs.getString(ACCESS, null)
    }

    fun fetchRefreshToken(): String? {
        return prefs.getString(REFRESH, null)
    }

    fun fetchUserNumber(): Int {
        return prefs.getInt(USER_NUMBER, 0)
    }

    fun clearData() {
        prefs.edit().remove(ACCESS).remove(REFRESH).remove(USER_NUMBER).apply()
    }
}