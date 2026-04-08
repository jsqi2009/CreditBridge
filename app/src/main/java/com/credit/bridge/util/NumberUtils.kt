package com.credit.bridge.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Proxy
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.credit.bridge.ui.App
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.BuildConfig
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Inet4Address
import java.net.NetworkInterface
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern
import kotlin.collections.iterator
import kotlin.jvm.internal.Intrinsics
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.ranges.coerceAtMost
import kotlin.text.isNullOrBlank
import kotlin.text.repeat
import kotlin.text.substring
import kotlin.text.toIntOrNull

object NumberUtils {
    fun formatNumber(
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

    @SuppressLint("HardwareIds")
    fun getAndroidId(): String {
        return try {
            Settings.Secure.getString(
                App.instance.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun formatFloatToStr(num: Any): String {
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

    fun formatIntToStr(num: Int): String {
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
