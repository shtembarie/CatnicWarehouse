package com.example.catnicwarehouse.main.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.main.launched_view.LaunchedActivity
import com.example.shared.networking.interceptor.GlobalNavigationHandler
import com.example.shared.networking.interceptor.GlobalNavigator
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),GlobalNavigationHandler {

    val progressBarManager by lazy { ProgressBarManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.main_navHostFragment)
                if (navController.currentDestination?.id == R.id.homeFragment) {
                    finish()
                } else {
                    navController.navigateUp()
                }
            }
        })

        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_navHostFragment)
                    as NavHostFragment
        val navController = navHostFragment.findNavController()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setupWithNavController(navController)

        // Hide bottom nav in all fragments except Home
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            } else {
                bottomNavigationView.visibility = View.GONE
            }
        }
    }

    override fun logout() {
        Intent(this@MainActivity,LaunchedActivity::class.java).apply{
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

    override fun onDestroy() {
        super.onDestroy()
        progressBarManager.dismiss()
    }

}
