package com.example.catnicwarehouse.CorrectingStock.shared

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.example.catnicwarehouse.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CorrectingStockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correcting_stock)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.correctingStock_navHostFragment)
                if (navController.currentDestination?.id == R.id.warehouseStockyardsFragments){
                    finish()
                }else{
                    navController.popBackStack()
                }
            }
        })
    }
    private var scannedStringBuilder = StringBuilder()
    private var handler: Handler? = null
    private var inputCompleteRunnable: Runnable? = null


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
         return onKeyDowns(keyCode, event)
    }
    fun onKeyDowns(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            val char = event.unicodeChar.toChar()
            if (char != 0.toChar()) { // Filter invalid characters
                scannedStringBuilder.append(char)

                // Reset the timeout for input completion
                inputCompleteRunnable?.let { handler?.removeCallbacks(it) }

                // Schedule a runnable to handle input completion after a delay
                handler = Handler(Looper.getMainLooper())
                inputCompleteRunnable = Runnable {
                    val scannedString = scannedStringBuilder.toString().trim()
                    scannedStringBuilder.clear()
                    handleScannedData(scannedString)
                }
                handler?.postDelayed(inputCompleteRunnable!!, 500) // 500ms timeout
            }
            return true
        }
        return false
    }
    private fun handleScannedData(data: String) {
        if (data.isNotEmpty()) {
            Log.d("MyFragment", "Scanned Data: $data")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inputCompleteRunnable?.let { handler?.removeCallbacks(it) }
        handler = null
    }
    //check Bluetooth is connected is or not
    //?: Bluetooth if it is Zebra or not.

}