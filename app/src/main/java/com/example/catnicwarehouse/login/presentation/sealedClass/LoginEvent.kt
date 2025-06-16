package com.example.catnicwarehouse.login.presentation.sealedClass

sealed class LoginEvent {
    data class LoginView(val userName:String, val password:String): LoginEvent()
}