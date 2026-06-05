package com.credit.bridge.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.credit.bridge.ui.App
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.net.Inet4Address
import kotlin.text.contains
import kotlin.text.lowercase
import kotlin.text.startsWith

/**
 * author : Jason
 * desc   :
 */
object DeviceInfoUtil {

    fun isVirtualDevice(): Boolean {
        val build = Build.FINGERPRINT
        val model = Build.MODEL
        val brand = Build.BRAND
        val device = Build.DEVICE
        val product = Build.PRODUCT
        val hardware = Build.HARDWARE
        val manufacturer = Build.MANUFACTURER

        return build.startsWith("generic")
                || build.lowercase().contains("vbox")
                || build.lowercase().contains("test-keys")
                || model.contains("google_sdk")
                || model.contains("Emulator")
                || model.contains("Android SDK built for x86")
                || manufacturer.contains("Genymotion")
                || hardware.contains("goldfish")
                || hardware.contains("ranchu")
                || product.contains("sdk")
                || product.contains("emulator")
                || product.contains("simulator")
    }

    fun isVpnOpen(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networks = cm.allNetworks

        for (network in networks) {
            val caps = cm.getNetworkCapabilities(network)
            if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true
            }
        }
        return false
    }

    fun isRoot(): Boolean {
        return checkR1() || checkR2() || checkR3()
    }


    fun checkR1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }
    fun checkR2(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }
    fun checkR3(): Boolean {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            return `in`.readLine() != null
        } catch (t: Throwable) {
            return false
        } finally {
            process?.destroy()
        }
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

    fun getWifiConfigure(mContext: Context): String {
        val wifiManager = mContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiArray = JSONArray()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val connectionInfo = wifiManager.connectionInfo
            if (connectionInfo != null && connectionInfo.getNetworkId() != -1) {
                try {
                    val wifiObj = JSONObject();
                    wifiObj.put("ssid", connectionInfo.ssid);
                    wifiObj.put("bssid", connectionInfo.bssid)
                    wifiObj.put("networkId", connectionInfo.networkId)
                    wifiObj.put("rssi", connectionInfo.rssi)
                    wifiObj.put("linkSpeed", connectionInfo.linkSpeed)
                    wifiObj.put("isCurrent", true)
                    wifiArray.put(wifiObj);
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        } else {
            wifiArray.put(JSONObject());
        }
        return wifiArray.toString();
    }

    fun getMemberMounted(type: Int): Long {
        try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
                val blockCount = statFs.blockCountLong
                val blockSize = statFs.blockSizeLong
                val availableBlocks = statFs.availableBlocksLong
                val freeBlocks = statFs.freeBlocksLong
                if (type == 0) {
                    return blockSize * blockCount
                } else if (type == 1) {
                    return availableBlocks * blockSize
                } else if (type == 2) {
                    return blockSize * blockCount - freeBlocks * blockSize
                } else if (type == 3) {
                    return freeBlocks * blockSize
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun getInactiveAnon(): Long {
        return try {
            var readLine: String?
            var inactiveAnon: Long = 0
            val bufferedReader = BufferedReader(FileReader("/proc/meminfo"))
            while (bufferedReader.readLine().also { readLine = it } != null) {
                if (readLine?.startsWith("Inactive(anon):") == true) {
                    val parts = (readLine ?: "").split("\\s+".toRegex())
                    if (parts.size >= 2) {
                        inactiveAnon = parts[1].toLong() * 1024
                    }
                    break
                }
            }
            bufferedReader.close()
            inactiveAnon
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getActiveAnon(): Long {
        return try {
            var readLine: String?
            var inactiveAnon: Long = 0
            val bufferedReader = BufferedReader(FileReader("/proc/meminfo"))
            while (bufferedReader.readLine().also { readLine = it } != null) {
                if (readLine?.startsWith("Active(anon):") == true) {
                    val parts = (readLine ?: "").split("\\s+".toRegex())
                    if (parts.size >= 2) {
                        inactiveAnon = parts[1].toLong() * 1024
                    }
                    break
                }
            }
            bufferedReader.close()
            inactiveAnon
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getActiveFile(): Long {
        return try {
            var readLine: String?
            var activeFile: Long = 0
            val bufferedReader = BufferedReader(FileReader("/proc/meminfo"))
            while (bufferedReader.readLine().also { readLine = it } != null) {
                if (readLine?.startsWith("Active(file):") == true) {
                    val parts = (readLine ?: "").split("\\s+".toRegex())
                    if (parts.size >= 2) {
                        activeFile = parts[1].toLong() * 1024
                    }
                    break
                }
            }
            bufferedReader.close()
            activeFile
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getInactiveFile(): Long {
        return try {
            var readLine: String?
            var inactiveFile: Long = 0
            val bufferedReader = BufferedReader(FileReader("/proc/meminfo"))
            while (bufferedReader.readLine().also { readLine = it } != null) {
                if (readLine?.startsWith("Inactive(file):") == true) {
                    val parts = (readLine ?: "").split("\\s+".toRegex())

                    if (parts.size >= 2) {
                        inactiveFile = parts[1].toLong() * 1024
                    }
                    break
                }
            }
            bufferedReader.close()
            inactiveFile
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun formatBatteryStatus(type: Int, status: Int): String {
        return (if (type == 0) {
            when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "full"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not_charging"
                BatteryManager.BATTERY_STATUS_UNKNOWN -> "unknown"
                else -> "unknown"
            }
        } else {
            when (status) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "over_voltage"
                BatteryManager.BATTERY_HEALTH_COLD -> "cold"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE ->
                    "unspecified_failure"

                BatteryManager.BATTERY_HEALTH_UNKNOWN -> "unknown"
                else -> "unknown"
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun isDualSim(): Boolean {
        val subscriptionManager = App.instance.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
        return subscriptionInfoList != null && subscriptionInfoList.size > 1
    }

    @SuppressLint("MissingPermission")
    fun getOperatorInfo(mContext: Context, type: Int): String {
        return try {
            val telephonyManager =
                mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (type == 0) {
                telephonyManager.networkOperator
            } else if (type == 1) {
                telephonyManager.networkOperatorName
            } else if (type == 2) {
                telephonyManager.simCountryIso
            } else if (type == 3) {
                telephonyManager.simOperator
            } else if (type == 4) {
                telephonyManager.simOperatorName
            } else if (type == 5) {
                telephonyManager.networkCountryIso
            } else if (type == 6) {
                telephonyManager.subscriberId
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @SuppressLint("MissingPermission")
    fun getSysNetworkType(context: Context): Int {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network: Network? = connectivityManager.activeNetwork
        val capabilities = network?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        if (capabilities == null) return 0

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 2
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 1
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> 3
            else -> 0
        }
    }


    fun getCommonMccAndMncInfo(context: Context, type: Int): Int {
        return try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val networkOperator = telephonyManager.networkOperator
            if (networkOperator != null && networkOperator.length >= 5) {
                if (type == 0) {
                    networkOperator.substring(0, 3).toInt()
                }
                networkOperator.substring(3).toInt()
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun fetchRadioType(): String {
        val connectivityManager =
            App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo!!.isConnected) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                return "WIFI"
            }
            if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                val telephonyManager =
                    App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                return when (telephonyManager.networkType) {
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_IDEN,
                        -> "2G"

                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP,
                        -> "3G"

                    TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                    TelephonyManager.NETWORK_TYPE_NR -> "5G"
                    else -> ""
                }
            }

        }
        return ""
    }

    fun getCommonNetworkInfo(context: Context, type: Int): String {
        var ipAddress = ""
        var netmask = ""
        var gateway = ""
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return ""
        val linkProperties = connectivityManager.getLinkProperties(activeNetwork) ?: return ""
        if (type == 0) {
            for (address in linkProperties.linkAddresses) {
                val inetAddress = address.address
                if (inetAddress is Inet4Address) {
                    ipAddress = inetAddress.hostAddress ?: ""
                    netmask = ""
                    break
                }
            }
        } else {
            for (routeInfo in linkProperties.routes) {
                if (routeInfo.hasGateway() && routeInfo.isDefaultRoute) {
                    gateway = routeInfo.gateway?.hostAddress ?: ""
                    break
                }
            }
        }
        if (type == 0) {
            return ipAddress
        } else if (type == 1) {
            return netmask
        } else if (type == 2) {
            return gateway
        }
        return ""
    }





}