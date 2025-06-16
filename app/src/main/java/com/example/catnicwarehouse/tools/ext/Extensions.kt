package com.example.catnicwarehouse.tools.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.catnicwarehouse.base.BaseApplication
import com.example.catnicwarehouse.tools.network.checkConnectedToANetwork
import java.text.SimpleDateFormat
import java.util.Locale

fun ViewModel.hasNetwork(): Boolean {
    return checkConnectedToANetwork(BaseApplication.INSTANCE)
}

fun ViewModel.getString(resId: Int):String{
    return BaseApplication.INSTANCE.getString(resId)
}

fun AppCompatActivity.hasNetwork(): Boolean {
    return checkConnectedToANetwork(BaseApplication.INSTANCE.applicationContext)
}

fun String.toFormattedDate(): String {
    return try {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault())

        // Parse the date and format it
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        "" // Return an empty string if parsing fails
    }
}