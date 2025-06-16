package com.example.catnicwarehouse.defectiveItems.shared.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.example.catnicwarehouse.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefectiveItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_defective_items)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.defectiveItems_navHostFragment)
                if (navController.currentDestination?.id == R.id.defectiveItemsFragment){
                    finish()
                }else{
                    navController.popBackStack()
                }
            }
        })
    }
}