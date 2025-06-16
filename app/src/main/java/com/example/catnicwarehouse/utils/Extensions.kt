package com.example.catnicwarehouse.utils

import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

fun String.colorSubstringFromCharacter(character: Char, color: Int): SpannableString {
    val spannableString = SpannableString(this)
    val startIndex = indexOf(character)
    if (startIndex != -1) {
        // Find the index of the next space after startIndex
        val spaceIndex = indexOf(' ', startIndex)
        // If no space is found, color until the end of the string
        val endIndex = if (spaceIndex == -1) length else spaceIndex

        // Apply the color from the character's index up to the first space or end of string
        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannableString
}


fun EditText.updateWithNumberTextWatcher(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // No action needed here
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // This is where you handle the text change
            var input = s.toString()
            if (input.isNullOrEmpty())
                input = "0"

            onTextChanged(input)
        }

        override fun afterTextChanged(s: Editable?) {
            // No action needed here
        }
    })
}

fun EditText.updateWithTextWatcher(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // No action needed here
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // This is where you handle the text change
            val input = s.toString().trim()
            onTextChanged(input)
        }

        override fun afterTextChanged(s: Editable?) {
            // No action needed here
        }
    })
}


fun String?.formatTimestamp(): String {
    // Check if the string is null or empty
    if (this.isNullOrEmpty()) {
        return ""
    }

    // Define the input and output date formats
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    return try {
        // Parse the input date string
        val date = inputFormat.parse(this)
        // Format the date into the output format
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        // Handle the case where the input date format is incorrect
        ""
    }
}

fun Context.isEMDKAvailable(): Boolean =
    try {
        Build.MANUFACTURER.contains("Zebra Technologies")
                && packageManager.getPackageInfo("com.symbol.emdk.emdkservice", 0) != null
    } catch (e: Exception) {
        false
    }

fun Response<*>.parseError(): String {
    return try {
        val errorBody = errorBody()?.string()
        val errorResponse = errorBody?.let { JSONObject(it) }

        val title = errorResponse?.optString("title")
        val errorsArray = errorResponse?.optJSONArray("errors")

        val errorMessages = mutableListOf<String>()

        errorsArray?.let { array ->
            for (i in 0 until array.length()) {
                val errorMessage = array.optJSONObject(i)?.optString("message")
                if (!errorMessage.isNullOrEmpty()) {
                    errorMessages.add("${i + 1}. $errorMessage") // Numbered error messages
                }
            }
        }

        when {
            !title.isNullOrEmpty() && errorMessages.isNotEmpty() -> "$title:\n${errorMessages.joinToString("\n")}"
            !title.isNullOrEmpty() && errorMessages.isEmpty() -> "Error: $title"
            errorMessages.isNotEmpty() -> errorMessages.joinToString("\n")
            else -> errorResponse?.optString("error") ?: "Error: ${code()} - ${message()}"
        }
    } catch (e: JSONException) {
        "Error: ${code()} - ${message()}"
    } ?: "Error: ${code()} - ${message()}"
}

