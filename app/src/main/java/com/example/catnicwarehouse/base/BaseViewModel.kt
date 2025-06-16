package com.example.catnicwarehouse.base

import androidx.lifecycle.ViewModel
import com.example.catnicwarehouse.tools.liveData.SingleLiveEvent

open class BaseViewModel : ViewModel() {

    val snackbarMessages = SingleLiveEvent<String>()

    val mainProgressBar = SingleLiveEvent<Boolean>()

    fun showProgressBar(){
        mainProgressBar.postValue(true)
    }

    fun hideProgressBar(){
        mainProgressBar.postValue(false)
    }
}