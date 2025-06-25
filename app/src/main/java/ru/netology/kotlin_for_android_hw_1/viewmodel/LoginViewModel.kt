package ru.netology.kotlin_for_android_hw_1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.kotlin_for_android_hw_1.auth.AppAuthorization
import ru.netology.kotlin_for_android_hw_1.auth.AuthUploadResponse
import ru.netology.kotlin_for_android_hw_1.retrofit.PostsRetrofitSuspendInterface
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postsRetrofitSuspendInterface: PostsRetrofitSuspendInterface
) : ViewModel() {

    val data = LoginDefault("student", "secret")

    private val _loginFaultFlag = MutableLiveData<Boolean?>(null)
    val loginFaultFlag: LiveData<Boolean?>
        get() = _loginFaultFlag

    fun loginVM(login: String, pass: String) {
        viewModelScope.launch {
            _loginFaultFlag.value = login(login, pass) == null
        }
    }

    fun loginFaultClear() {
        _loginFaultFlag.value = null
    }

    private suspend fun login(login: String, pass: String): AuthUploadResponse? {
        val response = postsRetrofitSuspendInterface.updateUser(login, pass)
        if (!response.isSuccessful) {
            println("login->response.isSuccessful ERROR: if-else")
            return null
        }
        val authUploadResponse = requireNotNull(response.body())
        println(authUploadResponse)
        AppAuthorization.getInstance()
            .setAuth(id = authUploadResponse.id, token = authUploadResponse.token)
        return response.body()
    }
}

data class LoginDefault(val login: String? = null, val pass: String? = null)

