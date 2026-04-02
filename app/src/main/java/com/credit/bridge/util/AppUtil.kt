package com.credit.bridge.util

import com.credit.bridge.remote.bean.ProductInfo
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

    fun formatProductId(productList: ArrayList<ProductInfo>): ArrayList<Int> {
        val idList = ArrayList<Int>()
        productList.forEach {
            idList.add(it.ifbivuyws)
        }
        return idList
    }

    fun formatAmount(productList: ArrayList<ProductInfo>): ArrayList<Int> {
        val amountList = ArrayList<Int>()
        productList.forEach {
            amountList.add(it.auaxapvecxrhb)
        }
        return amountList
    }

    fun formatTotalAmount(productList: ArrayList<ProductInfo>): Int {
        var totalAmount = 0
        productList.forEach {
            totalAmount += it.auaxapvecxrhb
        }
        return totalAmount
    }

    fun formatTotalFee(productList: ArrayList<ProductInfo>): Int {
        var fee = 0
        productList.forEach {
            fee += it.xwnpwazpoz
        }
        return fee
    }
}