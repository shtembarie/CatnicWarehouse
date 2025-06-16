package com.example.catnicwarehouse.login.presentation

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.login.domain.usecase.LoginUseCase
import com.example.catnicwarehouse.login.presentation.sealedClass.LoginEvent
import com.example.catnicwarehouse.login.presentation.sealedClass.LoginViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getLoginUseCase: LoginUseCase
) : BaseViewModel() {

    private val _loginFlow = MutableStateFlow<LoginViewState>(LoginViewState.Empty)
    val loginFlow: StateFlow<LoginViewState> = _loginFlow


    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginView -> {
                executeLoginProcess(
                    userName = event.userName,
                    password = event.password
                )
            }
        }
    }

    private fun executeLoginProcess(userName: String, password: String) {
        if (hasNetwork()) {
            getLoginUseCase(
                userName = userName,
                password = password
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loginFlow.value = LoginViewState.Loading
                    }

                    is Resource.Error -> {
                        _loginFlow.value = LoginViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _loginFlow.value = LoginViewState.LoginSuccessful
                    }
                }

            }.launchIn(viewModelScope)
        } else {
            snackbarMessages.value = getString(R.string.no_internet_connection)
        }
    }
}