package com.example.catnicwarehouse.incoming.shared.presentation.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.main.launched_view.LaunchedActivity
import com.example.shared.networking.interceptor.GlobalNavigationHandler
import com.example.shared.networking.interceptor.GlobalNavigator
import com.example.shared.utils.ProgressBarManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomingActivity : AppCompatActivity(),GlobalNavigationHandler {

    val progressBarManager by lazy { ProgressBarManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_comming)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.incoming_navHostFragment)
                if (navController.currentDestination?.id == R.id.deliveryFragment) {
                    finish()
                } else {
                    navController.navigateUp()
                }
            }
        })
    }

    override fun logout() {
        Intent(this@IncomingActivity, LaunchedActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
        finish()
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

    override fun onDestroy() {
        super.onDestroy()
        progressBarManager.dismiss()
    }

}