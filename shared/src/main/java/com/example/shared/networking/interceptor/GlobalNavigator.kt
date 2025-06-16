package com.example.shared.networking.interceptor

object GlobalNavigator {

    private var handler: GlobalNavigationHandler? = null

    fun registerHandler(handler: GlobalNavigationHandler) {
        GlobalNavigator.handler = handler
    }

    fun unregisterHandler() {
        handler = null
    }

    fun logout() {
        handler?.logout()
    }

    fun showGeneralNetworkError(error: String) {
        handler?.showGeneralNetworkError(error)
    }
}

interface GlobalNavigationHandler {
    fun logout()
    fun showGeneralNetworkError(error: String)
}