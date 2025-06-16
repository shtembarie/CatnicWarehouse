package com.example.catnicwarehouse.login.presentation.sealedClass

sealed class LoginViewState {
    object Reset : LoginViewState()
    object Empty : LoginViewState()
    object Loading : LoginViewState()
    object LoginSuccessful : LoginViewState()

    data class Error(val errorMessage: String?) : LoginViewState()
}
