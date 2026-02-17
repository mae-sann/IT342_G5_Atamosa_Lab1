package com.it342.miniapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.it342.miniapp.models.AuthResponse

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_EMAIL = "email"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveAuthResponse(authResponse: AuthResponse) {
        prefs.edit().apply {
            putString(KEY_TOKEN, authResponse.token)
            putString(KEY_EMAIL, authResponse.email)
            putString(KEY_FIRST_NAME, authResponse.firstName)
            putString(KEY_LAST_NAME, authResponse.lastName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun getUserFirstName(): String? = prefs.getString(KEY_FIRST_NAME, null)

    fun getUserLastName(): String? = prefs.getString(KEY_LAST_NAME, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}