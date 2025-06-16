package com.example.catnicwarehouse.splash.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.login.domain.repository.LoginRepository
import com.example.catnicwarehouse.login.presentation.sealedClass.LoginViewState
import com.example.catnicwarehouse.splash.presentation.sealedClass.SplashEvent
import com.example.catnicwarehouse.splash.presentation.sealedClass.SplashViewState
import com.example.catnicwarehouse.tools.liveData.SingleLiveEvent
import com.example.shared.tools.data.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    private val _splashFlow = MutableStateFlow<SplashViewState>(SplashViewState.Empty)
    val splashFlow: StateFlow<SplashViewState> = _splashFlow

    fun onEvent(event: SplashEvent) {
        when (event) {
            is SplashEvent.CheckUserConnected -> {
                checkUserConnected()
            }
        }
    }

    private fun checkUserConnected() {
        viewModelScope.launch {
            loginRepository.checkConnectedUser { isUserExisting ->
                _splashFlow.value = SplashViewState.UserExists(isUserExisting = isUserExisting)
            }
        }
    }
}