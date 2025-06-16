package com.example.catnicwarehouse.packing.shared.presentation.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.catnicwarehouse.R
import com.example.shared.networking.interceptor.GlobalNavigationHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PackingActivity : AppCompatActivity(), GlobalNavigationHandler {

    val activityCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val navController = findNavController(R.id.packing_navHostFragment)
            if (navController.currentDestination?.id != R.id.packingListFragment) {
                navController.navigateUp()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packing)

        onBackPressedDispatcher.addCallback(this, activityCallback)
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override fun showGeneralNetworkError(error: String) {
        TODO("Not yet implemented")
    }
}