package com.example.catnicwarehouse.tools

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun reParseDate(time: String, inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss", outputFormat:String = "dd.MM.yyyy hh:mm"): String?{
    val sdf = SimpleDateFormat(inputFormat)

    return try {
        val date = sdf.parse(time)
        sdf.applyPattern(outputFormat)
        sdf.format(date)
    } catch (e: ParseException) {
        time
    }
}