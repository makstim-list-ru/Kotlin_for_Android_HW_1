package ru.netology.kotlin_for_android_hw_1.auth

import android.content.Context
import android.content.Context.MODE_PRIVATE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppAuthorization private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val id = prefs.getLong(ID_KEY, 0)
        val token = prefs.getString(TOKEN_KEY, null)

        _authStateFlow = if (id == 0L || token == null) {
            MutableStateFlow(AuthState())
        } else {
            MutableStateFlow(AuthState(id, token))
        }
    }

    val authStateFlow = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
            apply()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }

    companion object {
        private const val ID_KEY = "id"
        private const val TOKEN_KEY = "token"

        @Volatile
        private var instance: AppAuthorization? = null

        fun getInstance(): AppAuthorization = requireNotNull(instance) {
            "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
        }

        fun init(context: Context) {
            instance = AppAuthorization(context)
        }
    }
}

data class AuthState(val id: Long = 0, val token: String? = null)