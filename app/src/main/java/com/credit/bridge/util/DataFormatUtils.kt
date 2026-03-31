package com.credit.bridge.util

import android.annotation.SuppressLint
import android.provider.Settings
import com.credit.bridge.ui.App

import kotlin.ranges.coerceAtMost
import kotlin.text.isNullOrBlank
import kotlin.text.repeat
import kotlin.text.substring

object DataFormatUtils {
    fun numberGeneral(
        content: String?,
        startLength: Int,
        endLength: Int
    ): String {
        if (content.isNullOrBlank()) return ""
        val totalLen = content.length
        val realSLen = startLength.coerceAtMost(totalLen)
        val realELen = endLength.coerceAtMost(totalLen - realSLen)
        if (realSLen + realELen >= totalLen) return content
        val start = content.substring(0, realSLen)
        val end = content.substring(totalLen - realELen)
        val star = "*".repeat(totalLen - realSLen - realELen)
        return "$start$star$end"
    }



    fun float2Str(num: Any): String {
        return if (num is Double || num is Float) {
            val doubleValue = num.toDouble()
            if (doubleValue == doubleValue.toLong().toDouble()) {
                doubleValue.toLong().toString()
            } else {
                num.toString()
            }
        } else {
            num.toString()
        }
    }
}
