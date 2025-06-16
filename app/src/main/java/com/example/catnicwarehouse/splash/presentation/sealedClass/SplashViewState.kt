package com.example.catnicwarehouse.splash.presentation.sealedClass

import com.example.catnicwarehouse.login.presentation.sealedClass.LoginViewState

sealed class SplashViewState {

    object Empty : SplashViewState()
    class UserExists(val isUserExisting: Boolean) : SplashViewState()

}