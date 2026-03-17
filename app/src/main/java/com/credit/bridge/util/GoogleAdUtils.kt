package com.credit.bridge.util

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

/**
 * author : Jason
 * date   : 2026/3/17 23:15
 * desc   :
 */
object GoogleAdUtils {
    private const val EMPTY_GOOGLE_AD_ID = "00000000-0000-0000-0000-0000000"

    suspend fun getGoogleAdId(context: Context): GoogleAdIDResult = withContext(Dispatchers.IO) {
        try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.applicationContext)
            val gaid = adInfo.id ?: EMPTY_GOOGLE_AD_ID
            val isLimited = adInfo.isLimitAdTrackingEnabled
            GoogleAdIDResult(gaid = gaid, isAdTrackingLimited = isLimited)
        } catch (e: GooglePlayServicesNotAvailableException) {
            GoogleAdIDResult(EMPTY_GOOGLE_AD_ID, true, "no GAID")
        } catch (e: GooglePlayServicesRepairableException) {
            GoogleAdIDResult(EMPTY_GOOGLE_AD_ID, true, "Google Play error")
        } catch (e: IOException) {
            GoogleAdIDResult(EMPTY_GOOGLE_AD_ID, true, "IO error：${e.message}")
        } catch (e: SecurityException) {
            GoogleAdIDResult(EMPTY_GOOGLE_AD_ID, true, "permisstion error：${e.message}")
        } catch (e: Exception) {
            GoogleAdIDResult(EMPTY_GOOGLE_AD_ID, true, "fail ：${e.message}")
        }
    }


    fun isValid(gaid: String): Boolean {
        return try {
            gaid.isNotBlank() && gaid != EMPTY_GOOGLE_AD_ID && UUID.fromString(gaid) != null
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    data class GoogleAdIDResult(
        val gaid: String,
        val isAdTrackingLimited: Boolean,
        val errorMsg: String? = null
    )

}