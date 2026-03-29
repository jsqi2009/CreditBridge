package com.credit.bridge.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.util.Locale
import kotlin.collections.forEachIndexed
import kotlin.text.isNullOrEmpty
import kotlin.text.substring
import kotlin.text.trim
import kotlin.text.uppercase
import kotlin.to

object PhoneOperatorsUtils {

    private const val INDIA_MCC_1 = "404"
    private const val INDIA_MCC_2 = "405"


    private val INDIA_CARRIER_MAPPING = mapOf(
        "05" to "Jio", "06" to "Jio", "07" to "Jio", "08" to "Jio",
        "01" to "Airtel", "03" to "Airtel", "04" to "Airtel", "10" to "Airtel",
        "02" to "Vodafone Idea", "09" to "Vodafone Idea", "11" to "Vodafone Idea",
        "14" to "BSNL", "15" to "BSNL", "16" to "BSNL",
        "00" to "MTNL",
        "12" to "Tata Docomo",
        "13" to "Reliance Communications"
    )

    /**
     *Resolve Indian operator names from MCC+MNC
     * @param simOperator format: MCCMNC (e.g. 40405 → MCC=404, MNC=05)
     * @return Carrier name, returns "Unknown Carrier" if non-Indian/Unknown
     */
    private fun parseIndiaCarrier(simOperator: String?): String {
        if (simOperator.isNullOrEmpty() || simOperator.length < 5) {
            return "Unknown Carrier"
        }
        val mcc = simOperator.substring(0, 3)
        val mnc = simOperator.substring(3, 5)
        return if (mcc == INDIA_MCC_1 || mcc == INDIA_MCC_2) {
            INDIA_CARRIER_MAPPING.getOrDefault(mnc.uppercase(Locale.ENGLISH), "Unknown Carrier($mnc)")
        } else {
            "Non-India Carrier($mcc)"
        }
    }

    @SuppressLint("ServiceCast")
    fun getSingleSimCarrier(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val systemCarrier = telephonyManager.simOperatorName?.trim()
            return if (!systemCarrier.isNullOrEmpty() && systemCarrier != "unknown") {
                systemCarrier.uppercase(Locale.ENGLISH)
            } else {
                parseIndiaCarrier(telephonyManager.simOperator)
            }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    @SuppressLint("ServiceCast")
    fun getDualSimCarrier(context: Context): Pair<String, String> {
        val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        var sim1Carrier = "No SIM Card 1"
        var sim2Carrier = "No SIM Card 2"
            val subscriptionList: List<SubscriptionInfo>? =
                subscriptionManager.activeSubscriptionInfoList
            subscriptionList?.forEachIndexed { index, info ->
                val carrier = if (!info.carrierName.isNullOrEmpty()) {
                    info.carrierName.toString().trim().uppercase(Locale.ENGLISH)
                } else {
                    parseIndiaCarrier(info.iccId ?: info.mncString)
                }
                when (index) {
                    0 -> sim1Carrier = carrier
                    1 -> sim2Carrier = carrier
                }
            }
        return Pair(sim1Carrier, sim2Carrier)
    }
}