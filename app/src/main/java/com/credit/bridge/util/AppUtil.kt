package com.credit.bridge.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object AppUtil {


    private const val DATE_FORMAT = "dd-MM-yyyy"

    fun getTotalSeconds(dateStr: String): Long {
        if (dateStr.isEmpty()) {
            return 0
        }
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()

        val targetDate = sdf.parse(dateStr) ?: return 0
        val targetTimeMillis = targetDate.time
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis >= targetTimeMillis) {
            return 0
        }
        val remainingTime = targetTimeMillis - currentTimeMillis
        return remainingTime
    }
}