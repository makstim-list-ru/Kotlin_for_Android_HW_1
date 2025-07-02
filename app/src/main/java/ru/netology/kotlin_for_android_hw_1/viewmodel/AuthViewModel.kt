package ru.netology.kotlin_for_android_hw_1.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.netology.kotlin_for_android_hw_1.auth.AppAuthorization
import ru.netology.kotlin_for_android_hw_1.auth.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(appAuthorization: AppAuthorization): ViewModel() {
    val data: StateFlow<AuthState> = appAuthorization.authStateFlow
    val authenticated: Boolean
        get() = data.value.id != 0L
}