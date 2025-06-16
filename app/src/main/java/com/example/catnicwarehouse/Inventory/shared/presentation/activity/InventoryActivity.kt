package com.example.catnicwarehouse.Inventory.shared.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.example.catnicwarehouse.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.inventory_navHostFragment)
                if (navController.currentDestination?.id == R.id.inventoryFragment){
                    finish()
                }else{
                    navController.popBackStack()
                }
            }
        })
    }
}