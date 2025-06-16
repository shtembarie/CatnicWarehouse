package com.example.catnicwarehouse.movement.movementList.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.example.catnicwarehouse.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovementsActivity : AppCompatActivity() {
    private var hasMovements: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movements)
        hasMovements = intent.getBooleanExtra("hasMovements", false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.movements_navHostFragment)
                if (navController.currentDestination?.id == R.id.movementsListFragment) {
                    finish()
                } else {
                    navController.navigateUp()
                }
            }
        })
    }
}