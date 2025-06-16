package com.example.catnicwarehouse.main.launched_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.catnicwarehouse.R
import com.example.shared.networking.interceptor.GlobalNavigationHandler
import com.example.shared.networking.interceptor.GlobalNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchedActivity : AppCompatActivity(), GlobalNavigationHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launched)

    }

    override fun logout() {
        runOnUiThread {
            val navController = findNavController(R.id.launched_navHostFragment)
            navController.setGraph(R.navigation.start_naviagtion)
        }
    }

    override fun showGeneralNetworkError(error: String) {

    }

    override fun onStart() {
        super.onStart()
        GlobalNavigator.registerHandler(this)
    }

    override fun onStop() {
        super.onStop()
        GlobalNavigator.unregisterHandler()
    }

}