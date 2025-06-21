package ru.netology.kotlin_for_android_hw_1.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.netology.kotlin_for_android_hw_1.auth.AppAuthorization
import ru.netology.kotlin_for_android_hw_1.auth.AuthState

class AuthViewModel : ViewModel() {
    val data: StateFlow<AuthState> = AppAuthorization.getInstance().authStateFlow
    val authenticated: Boolean
        get() = data.value.id != 0L
}