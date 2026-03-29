package com.credit.bridge.remote.bean

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.telephony.TelephonyManager
import com.google.gson.Gson
import java.io.Serializable
import java.util.Locale
import java.util.TimeZone

/**
 * author : Jason
 * desc   :
 */
class DeviceInfo {
    var bootTime: String = ""
    var deviceNo: String = ""
    var deviceBrand: String = ""
    var deviceModel: String = ""
    var deviceRelease: String = ""
    var deviceSdk: String = ""
    var deviceBoard: String = ""
    var deviceProduct: String = ""
    var deviceDevice: String = ""
    var deviceFingerprint: String = ""
    var deviceHost: String = ""
    var deviceTags: String = ""
    var deviceType: String = ""
    var deviceTime: String = ""
    var deviceIncremental: String = ""
    var deviceSdkInt: String = ""
    var deviceManufacturer: String = ""
    var deviceBootloader: String = ""
    var deviceCpuAbi: String = ""
    var deviceCpuAbi2: String = ""
    var deviceHardware: String = ""
    var deviceSerial: String  = ""
}