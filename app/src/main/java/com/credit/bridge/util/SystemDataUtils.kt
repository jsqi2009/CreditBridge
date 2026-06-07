package com.credit.bridge.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import java.util.UUID
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream
import android.util.Base64
import android.util.DisplayMetrics
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.gson.Gson
import pub.devrel.easypermissions.BuildConfig
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.InputStreamReader
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.text.toIntOrNull
import androidx.core.content.edit
import com.credit.bridge.remote.bean.BaseDeviceInfo
import com.credit.bridge.remote.bean.BatteryInfo
import com.credit.bridge.remote.bean.DeviceInfo
import com.credit.bridge.remote.bean.GeographicInfo
import com.credit.bridge.remote.bean.PhoneNetworkInfo
import com.credit.bridge.remote.bean.StorageInfo
import com.credit.bridge.remote.bean.SystemInfo
import com.credit.bridge.ui.App
import java.io.RandomAccessFile
import java.lang.RuntimeException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.getDefault
import kotlin.also
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.getOrNull
import kotlin.collections.isNotEmpty
import kotlin.collections.isNullOrEmpty
import kotlin.collections.mapNotNull
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.collections.toMutableList
import kotlin.io.use
import kotlin.let
import kotlin.plus
import kotlin.takeIf
import kotlin.text.contains
import kotlin.text.equals
import kotlin.text.format
import kotlin.text.isBlank
import kotlin.text.isNotEmpty
import kotlin.text.lowercase
import kotlin.text.replace
import kotlin.text.split
import kotlin.text.startsWith
import kotlin.text.substring
import kotlin.text.toByteArray
import kotlin.text.toInt
import kotlin.text.toLongOrNull
import kotlin.text.toRegex
import kotlin.text.trim
import kotlin.toString

object SystemDataUtils {

    fun getInstalledAppList(context: Context): Array<BaseDeviceInfo> {
        val list = ArrayList<BaseDeviceInfo>()
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val installedPackages = packageManager.queryIntentActivities(intent, 0) ?: return emptyArray()

        for (resolveInfo in installedPackages) {
            val packageName = resolveInfo.activityInfo?.packageName ?: continue
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                val appInfo = packageInfo.applicationInfo ?: continue
                val data = BaseDeviceInfo()
                data.appName = appInfo.loadLabel(packageManager)?.toString() ?: packageName
                data.firstInstallTime = packageInfo.firstInstallTime.toString()
                data.isGameApp = (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
                data.uninstalled = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                data.packageName = packageName
                data.lastUpdateTime = packageInfo.lastUpdateTime.toString()
                data.versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode.toString()
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toString()
                }
                data.versionName = packageInfo.versionName ?: "un version"
                data.isSystemApp = isSystemA(packageInfo)
                data.requestedPermissions = try {
                    val permInfo = packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_PERMISSIONS
                    )
                    if (permInfo.requestedPermissions.isNullOrEmpty()) {
                        mutableListOf()
                    } else {
                        permInfo.requestedPermissions!!.toMutableList()
                    }
                } catch (_: Exception) {
                    mutableListOf()
                }
                list.add(data)
            } catch (_: Exception) {
            }
        }
        return list.toTypedArray()
    }

    @SuppressLint("HardwareIds")
    @RequiresPermission(allOf = [ Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getDeviceInfo(context: Context): Array<SystemInfo>{
        val deviceInfo = SystemInfo()
        deviceInfo.appSign = getAppSign().uppercase(getDefault())
        deviceInfo.baseBandVersion = Build.getRadioVersion()
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = level?.div(scale?.toFloat() ?: 1.0f)
        deviceInfo.battery = getBatteryPercentage(context)
        deviceInfo.bluetooth = getBluetoothMacAddress()
        deviceInfo.board = Build.BOARD
        deviceInfo.brand = Build.BRAND
        deviceInfo.buildId = Build.ID
        deviceInfo.cameraNum = cameraNumber()
        deviceInfo.cameraSize = getSystemCameraConfig( context).toString()
        deviceInfo.city = TimeZone.getDefault().id
        deviceInfo.country = getCurrentCountry(context)
        deviceInfo.cpuAbi = Build.SUPPORTED_ABIS[0]
        deviceInfo.device = Build.DEVICE
        deviceInfo.display = Build.DISPLAY
        deviceInfo.diskSpace = getStorageSize(Environment.getExternalStorageDirectory()).toString()
        deviceInfo.diskFreeSpace = getFreeStorageSize(Environment.getExternalStorageDirectory()).toString()
        deviceInfo.displayCountry = Locale.getDefault().displayCountry
        deviceInfo.displayName = Locale.getDefault().displayName
        deviceInfo.displayLanguage = Locale.getDefault().displayLanguage
        deviceInfo.freeMemory = getSysFreeStorage().toString()
        deviceInfo.fingerPrint = Build.FINGERPRINT
        deviceInfo.hardware = Build.HARDWARE
        deviceInfo.host = Build.HOST
        deviceInfo.imsi = Settings.Secure.getString(App.instance.contentResolver, Settings.Secure.ANDROID_ID)
        deviceInfo.isDebug = "${BuildConfig.DEBUG}"
        deviceInfo.isNetworkingRoaming = false
        deviceInfo.isProxy = isProxy()
        deviceInfo.isRoot = checkR1() || checkR2() || checkR3()
        deviceInfo.isSimulator = isDeviceEmulator()
        deviceInfo.kernelVersion = System.getProperty("os.version")
        deviceInfo.language = Locale.getDefault().language
        deviceInfo.macAddress = getSsidInfo(context, 2)
        deviceInfo.manufacturer = Build.MANUFACTURER
        deviceInfo.modelNo = Build.MODEL
        deviceInfo.networkCountryIso = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso
        deviceInfo.networkOperator = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperator
        deviceInfo.networkType = getSysNetWorkType(context)
        deviceInfo.osVersion ="${Build.VERSION.RELEASE}"
        deviceInfo.product =  Build.PRODUCT
        deviceInfo.locationInfo = getLoc()
        deviceInfo.networkInfo = getNetworkInfo()
        deviceInfo.batteryInfo = Gson().toJson(getBatteryInfo(App.instance))
        deviceInfo.deviceInfo = Gson().toJson(getDeviceInfoInfo())
        deviceInfo.memoryInfo = Gson().toJson(getMemoryStorageInfo(App.instance))
        deviceInfo.screenHeight = height()
        deviceInfo.screenWidth = width()
        deviceInfo.sdkVersion = "${Build.VERSION.SDK_INT}"
        deviceInfo.serialNo = Build.SERIAL
        deviceInfo.simCountryIso =  (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simCountryIso
        deviceInfo.simOperator = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simOperator
        deviceInfo.simOperatorName = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simOperatorName
        deviceInfo.simMobile = ""
        deviceInfo.tags = Build.TAGS
        deviceInfo.time = "${Build.TIME}"
        deviceInfo.timezone = TimeZone.getDefault().displayName
        deviceInfo.timezoneLong = TimeZone.getDefault().getDisplayName(false, TimeZone.LONG)
        deviceInfo.timezoneShort = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
        val memoryInfo = ActivityManager.MemoryInfo()
        deviceInfo.totalMemory = getSysStorage().toString()
        deviceInfo.type = Build.TYPE
        deviceInfo.upTime = "${System.currentTimeMillis() - Build.TIME}"
        deviceInfo.user = Build.USER
        deviceInfo.wifiBssid =  (App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.bssid
        deviceInfo.wifiSsid = (App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.ssid
        deviceInfo.wifiRssi = "${(App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.rssi}"
        deviceInfo.deviceNo = Settings.Secure.getString(App.instance.contentResolver, Settings.Secure.ANDROID_ID)
        deviceInfo.keyboard = getSysKeyboard().toString()
        deviceInfo.memorySpace = getMemberSpace(0).toString()
        deviceInfo.memoryUseSpace = getMemberSpace(1).toString()
        deviceInfo.imagesInternal = getDataCount(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DATA)
        ).toString()
        deviceInfo.imagesExternal = getDataCount(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DATA)
        ).toString()
        deviceInfo.audioInternal = getDataCount(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Media.DATA)
        ).toString()
        deviceInfo.audioExternal = getDataCount(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DATA)
        ).toString()
        deviceInfo.videoInternal = getDataCount(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DATA)
        ).toString()
        deviceInfo.videoExternal = getDataCount(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DATA)
        ).toString()
        deviceInfo.downloadFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles().size.toString()
        deviceInfo.wifi = wifiB()
        deviceInfo.lastBootTime = "${SystemClock.elapsedRealtimeNanos()}"
        deviceInfo.productionDate = Build.TIME.toString()
        deviceInfo.wifiCount = 0.toString()
        deviceInfo.configuredWifi = DeviceInfoUtil.getWifiConfigure(context)
        deviceInfo.cores = cor()
        deviceInfo.deviceHeight = height()
        deviceInfo.deviceWidth = width()
        deviceInfo.inphysicalSize = screenS()
        deviceInfo.phoneType = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).phoneType.toString()
        deviceInfo.memoryCardSize = DeviceInfoUtil.getMemberMounted(0).toString()
        deviceInfo.memoryCardUsableSize = DeviceInfoUtil.getMemberMounted(1).toString()
        deviceInfo.memoryCardSizeUse = DeviceInfoUtil.getMemberMounted(2).toString()
        deviceInfo.memoryCardFreeSize = DeviceInfoUtil.getMemberMounted(3).toString()
        deviceInfo.picCount = "${getDataCount(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DATA)
        )+ getDataCount(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.DATA)
        )}"
        return arrayOf(deviceInfo)
    }


    fun getDeviceInfoInfo(): DeviceInfo {
        var info =  DeviceInfo()
        info.bootTime = getBootTime()
        info.deviceNo = getDeviceNo(App.instance)
        info.deviceBrand = Build.BRAND ?: "unknow"
        info.deviceModel = Build.MODEL ?: "unknow"
        info.deviceRelease = Build.VERSION.RELEASE ?: "unknow"
        info.deviceSdk = Build.VERSION.SDK ?: "unknow"
        info.deviceBoard = Build.BOARD ?: "unknow"
        info.deviceProduct = Build.PRODUCT ?: "unknow"
        info.deviceDevice = Build.DEVICE ?: "unknow"
        info.deviceFingerprint = Build.FINGERPRINT ?: "unknow"
        info.deviceHost = Build.HOST ?: "unknow"
        info.deviceTags = Build.TAGS ?: "unknow"
        info.deviceType = Build.TYPE ?: "unknow"
        info.deviceTime = formatBuildTime()
        info.deviceIncremental = Build.VERSION.INCREMENTAL ?: "unknow"
        info.deviceSdkInt = Build.VERSION.SDK_INT.toString()
        info.deviceManufacturer = Build.MANUFACTURER ?: "unknow"
        info.deviceBootloader = Build.BOOTLOADER ?: "unknow"
        info.deviceCpuAbi = Build.SUPPORTED_ABIS.getOrNull(1) ?: ""
        info.deviceCpuAbi2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Build.SUPPORTED_ABIS.getOrNull(1) ?: ""
            } else {
                Build.CPU_ABI2 ?: ""
            }
        info.deviceHardware = Build.HARDWARE ?: "unknow"
        info.deviceSerial = getDeviceSerial(App.instance)
        return info
    }
    private fun formatBuildTime(): String {
        return DATE_FORMAT.format(Date(Build.TIME))
    }
    private const val BYTE_TO_MB = 1024 * 1024L
    fun getTotalStorageMB(context: Context): Int {
        return try {
            val statFs = StatFs(Environment.getExternalStorageDirectory().path)
            val totalBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                statFs.totalBytes
            } else {
                @Suppress("DEPRECATION")
                statFs.blockCount.toLong() * statFs.blockSize
            }
            (totalBytes / BYTE_TO_MB).toInt()
        } catch (e: Exception) {
            0
        }
    }
    fun getFreeStorageMB(context: Context): Int {
        return try {
            val statFs = StatFs(Environment.getExternalStorageDirectory().path)
            val freeBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                statFs.availableBytes
            } else {
                @Suppress("DEPRECATION")
                statFs.availableBlocks.toLong() * statFs.blockSize
            }
            (freeBytes / BYTE_TO_MB).toInt()
        } catch (e: Exception) {
            0
        }
    }
    private fun parseMemInfo(): Map<String, Long> {
        val memInfoMap = mutableMapOf<String, Long>()
        try {
            val randomAccessFile = RandomAccessFile("/proc/meminfo", "r")
            var line: String?
            while (randomAccessFile.readLine().also { line = it } != null) {
                line?.trim()?.takeIf { it.isNotEmpty() }?.let {
                    val parts = it.split("\\s+".toRegex())
                    if (parts.size >= 2) {
                        val key = parts[0].lowercase().replace(":", "")
                        val value = parts[1].toLongOrNull() ?: 0
                        val unit = if (parts.size >= 3) parts[2] else ""

                        val valueInByte = if (unit.equals("kb", ignoreCase = true)) {
                            value * 1024
                        } else {
                            value
                        }
                        memInfoMap[key] = valueInByte
                    }
                }
            }
            randomAccessFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return memInfoMap
    }
    fun getMemoryStorageInfo(context: Context): StorageInfo {
        val memInfo = parseMemInfo()

        val totalStorageMB = getTotalStorageMB(context)
        val freeStorageMB = getFreeStorageMB(context)

        var info =  StorageInfo()
        info.aSpace = totalStorageMB
        info.rSpace = freeStorageMB
        info.memTotal = memInfo["memtotal"] ?: 0
        info.memFree = memInfo["memfree"] ?: 0
        info.buffers = memInfo["buffers"] ?: 0
        info.cached = memInfo["cached"] ?: 0
        info.wwapCached = memInfo["swapcached"] ?: 0
        info.active = memInfo["active"] ?: 0
        info.inactive = memInfo["inactive"] ?: 0
        info.activeAnon = DeviceInfoUtil.getActiveAnon() ?: 0
        info.inactiveAnon = DeviceInfoUtil.getInactiveAnon() ?: 0
        info.activeFile = DeviceInfoUtil.getActiveFile() ?: 0
        info.inactiveFile = DeviceInfoUtil.getInactiveFile() ?: 0
        info.unevictable = memInfo["unevictable"] ?: 0
        info.mlocked = memInfo["mlocked"] ?: 0
        info.highTotal = memInfo["hightotal"] ?: 0
        info.highFree = memInfo["highfree"] ?: 0
        info.lowTotal = memInfo["lowtotal"] ?: 0
        info.lowFree = memInfo["lowfree"] ?: 0
        info.swapTotal = memInfo["swaptotal"] ?: 0
        info.swapFree = memInfo["swapfree"] ?: 0
        info.dirty = memInfo["dirty"] ?: 0
        info.writeback = memInfo["writeback"] ?: 0
        info.anonPages = memInfo["anonpages"] ?: 0
        info.mapped = memInfo["mapped"] ?: 0
        info.shmem = memInfo["shmem"] ?: 0
        info.slab = memInfo["slab"] ?: 0
        info.sreclaimable = memInfo["sreclaimable"] ?: 0
        info.sunreclaim = memInfo["sunreclaim"] ?: 0
        info.kernelStack = memInfo["kernelstack"] ?: 0
        info.pageTables = memInfo["pagetables"] ?: 0
        info.nfsUnstable = memInfo["nfs_unstable"] ?: 0
        info.bounce = memInfo["bounce"] ?: 0
        info.writebackTmp = memInfo["writebacktmp"] ?: 0
        info.commitLimit = memInfo["commitlimit"] ?: 0
        info.committedAs = memInfo["committed_as"] ?: 0
        info.vmallocTotal = memInfo["vmalloctotal"] ?: 0
        info.vmallocUsed = memInfo["vmallocused"] ?: 0
        info.vmallocChunk = memInfo["vmallocchunk"] ?: 0
        info.freeCma = memInfo["cmafree"] ?: 0
        info.cmaTotal = memInfo["cmatotal"] ?: 0
        return info
    }
    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    private fun getDeviceSerial(context: Context): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Manifest.permission.READ_PHONE_NUMBERS
                        } else {
                            Manifest.permission.READ_PHONE_STATE
                        }
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Build.getSerial() ?: "Unknow"
                } else {
                    "Unknow"
                }
            } else {
                Build.SERIAL ?: "Unknow"
            }
        } catch (e: Exception) {
            "Unknow"
        }
    }
    private fun getDeviceNo(context: Context): String {
        return try {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "unknow"
        } catch (e: Exception) {
            "unknow"
        }
    }
    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private fun getBootTime(): String {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        val bootTimeMillis = System.currentTimeMillis() - elapsedRealtime
        return DATE_FORMAT.format(Date(bootTimeMillis))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    @SuppressLint("ServiceCast")
    private fun getMccMncFromMultiSim(): String? {
        return try {
            val subscriptionManager = App.instance.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val subscriptionInfos: List<SubscriptionInfo>? = subscriptionManager.activeSubscriptionInfoList
            subscriptionInfos?.forEach { info ->
                val mccMnc = info.mccString + info.mncString
                if (mccMnc.isNotEmpty() && mccMnc.length >= 5) {
                    return mccMnc
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    fun getBatteryInfo(context: Context): BatteryInfo {
        val batteryInfo = BatteryInfo()
        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return batteryInfo

        batteryInfo.batteryStatus = DeviceInfoUtil.formatBatteryStatus(0, batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1))
        batteryInfo.batteryHealth = if (batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) == 2) {
            "good"
        } else {
            "bad"
        }
        batteryInfo.batteryPresent = batteryIntent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false).toString()
        batteryInfo.batteryLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1).toString()
        batteryInfo.batteryScale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1).toString()
        batteryInfo.batteryPlugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1).toString()
        batteryInfo.batteryVoltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).toString()
        batteryInfo.batteryTemperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1).toString()
        batteryInfo.batteryTechnology = batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
        batteryInfo.batteryIconSmall = batteryIntent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1).toString()
        batteryInfo.androidId = getAndroidId(context)
        batteryInfo.createTime = System.currentTimeMillis().toString()

        return batteryInfo
    }

    private fun getAndroidId(context: Context): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getNetworkInfo():String{
        var networkInfo = PhoneNetworkInfo()
        networkInfo.ip = getLocalIp()
        networkInfo.localMobile = 0.toString()
        networkInfo.isEmulator = isDeviceEmulator()
        networkInfo.isMod = false
        networkInfo.isRoot =  checkR1() || checkR2() || checkR3()
        networkInfo.isDualSim = DeviceInfoUtil.isDualSim()
        networkInfo.imeiSim1 = ""
        networkInfo.imeiSim2 =  ""
        networkInfo.imsiSim1 = ""
        networkInfo.imsiSim2 = ""
        networkInfo.isSim1Ready = getSimState(0) == TelephonyManager.SIM_STATE_READY
        networkInfo.isSim2Ready = getSimState(1) == TelephonyManager.SIM_STATE_READY
        networkInfo.networkCountryIso = network1()
        networkInfo.networkOperator = DeviceInfoUtil.getOperatorInfo(App.instance.applicationContext, 0)
        networkInfo.networkOperatorName = DeviceInfoUtil.getOperatorInfo(App.instance.applicationContext, 1)
        networkInfo.networkType = DeviceInfoUtil.getSysNetworkType(App.instance.applicationContext).toString()
        networkInfo.phoneType = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).phoneType.toString()
        networkInfo.simCountryIso = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simCountryIso.toString()
        networkInfo.simOperator = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simOperator.toString()
        networkInfo.simOperatorName = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simOperatorName
        networkInfo.simSerialNumber = ""
        getSimNetworkInfo(networkInfo)
        networkInfo.getSimState = if(getSimState(0) == TelephonyManager.SIM_STATE_READY) "1" else "0"
        networkInfo.subscriberId = ""
        networkInfo.voiceMailNumber = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).voiceMailNumber?.toString()
        val operator = (App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperator
        networkInfo.mcc = DeviceInfoUtil.getCommonMccAndMncInfo(App.instance.applicationContext, 0).toString()
        networkInfo.mnc = if (operator.length > 3) operator.substring(3) else ""
        networkInfo.lac = ""
        networkInfo.cell = ""
        networkInfo.systemId = ""
        networkInfo.networkId = getSsidInfo(App.instance.applicationContext, 4)
        networkInfo.radioType = DeviceInfoUtil.fetchRadioType()
        networkInfo.wifiState = wifiStateMY()
        networkInfo.ssid = ssidMY()
        networkInfo.bssid = bssidMY()
        networkInfo.macAddress = getSsidInfo(App.instance.applicationContext, 2)
        networkInfo.linkSpeed = linkSpeedMY()
        networkInfo.rssi = my_rssi()
        networkInfo.supplicantState = my_supplicantState()
        networkInfo.hiddenSsid = my_hiddenSsid()
        networkInfo.frequency = my_frequency()
        networkInfo.dns1 = getDnsServers().firstOrNull() ?: ""
        networkInfo.dns2 = getDnsServers().getOrNull(1) ?: ""
        networkInfo.ipAddress = ipAddressmy()
        networkInfo.netmask = DeviceInfoUtil.getCommonNetworkInfo(App.instance.applicationContext, 1)
        networkInfo.gateway = DeviceInfoUtil.getCommonNetworkInfo(App.instance.applicationContext, 2)
        networkInfo.dhcp = getSsidInfo(App.instance.applicationContext, 13)
        return Gson().toJson(networkInfo)
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    @SuppressLint("NewApi")
    fun getSimNetworkInfo(target: PhoneNetworkInfo) {
        val context = App.instance
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val activeSubs = subscriptionManager.activeSubscriptionInfoList
            if (!activeSubs.isNullOrEmpty()) {
                val firstSub = activeSubs[0]
                target.simCountryIso = firstSub.countryIso ?: ""
                target.simOperator = "${firstSub.mccString ?: ""}${firstSub.mncString ?: ""}"
                target.simOperatorName = firstSub.carrierName?.toString() ?: ""
                target.simSerialNumber = ""
                return
            }

            target.simCountryIso = telephonyManager.simCountryIso ?: ""
            target.simOperator = telephonyManager.simOperator ?: ""
            target.simOperatorName = telephonyManager.simOperatorName ?: ""
            target.simSerialNumber = ""

            if (target.simOperator.isNullOrBlank()) {
                target.simOperator = telephonyManager.networkOperator ?: ""
            }
            if (target.simCountryIso.isNullOrBlank()) {
                target.simCountryIso = telephonyManager.networkCountryIso ?: ""
            }
        } catch (_: Exception) {
        }
    }
    fun netmaskMY(): String {
        val cm = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val linkProperties = cm.getLinkProperties(activeNetwork)
        return linkProperties?.linkAddresses?.firstOrNull()?.let { addr ->
            val prefixLength = addr.prefixLength
            val mask = (0xFFFFFFFF shl (32 - prefixLength)) and 0xFFFFFFFF
            String.format(
                "%d.%d.%d.%d",
                (mask and 0xff),
                (mask shr 8 and 0xff),
                (mask shr 16 and 0xff),
                (mask shr 24 and 0xff)
            )
        } ?: ""
    }

    fun gatewayMY(): String {
        val cm = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val linkProperties = cm.getLinkProperties(activeNetwork)
        return linkProperties?.httpProxy.toString()
    }

    fun dhcpMY(): String {
        val cm = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val linkProperties = cm.getLinkProperties(activeNetwork)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            linkProperties?.dhcpServerAddress?.hostAddress ?: ""
        } else {
            ""
        }
    }

    @SuppressLint("DefaultLocale")
    fun ipAddressmy(): String {
        val wifiManager = App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ipInt = wifiInfo.ipAddress
        return if (ipInt == 0) "" else String.format(
            "%d.%d.%d.%d",
            (ipInt and 0xff),
            (ipInt shr 8 and 0xff),
            (ipInt shr 16 and 0xff),
            (ipInt shr 24 and 0xff)
        )
    }
    private fun getDnsServers(): List<String> {
        val cm = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val linkProperties = cm.getLinkProperties(activeNetwork)
        return linkProperties?.dnsServers?.mapNotNull { it.hostAddress } ?: emptyList()
    }
    fun my_frequency(): String {
        val wifiManager = App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.frequency.toString()
    }
    fun my_rssi(): String {
        val wifiManager = App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.rssi.toString()
    }

    fun my_supplicantState(): String {
        val wifiManager = App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.supplicantState?.toString() ?: ""
    }

    fun my_hiddenSsid(): String {
        val wifiManager = App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return if (wifiInfo.hiddenSSID) "1" else "0"
    }
    fun ssidMY(): String {
        val wifiManager = App.instance.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ssid?.replace("\"", "") ?: ""
    }

    fun bssidMY(): String {
        val wifiManager = App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.bssid ?: ""
    }

    fun linkSpeedMY(): String {
        val wifiManager = App.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.linkSpeed.toString()
    }
    fun wifiStateMY(): String {
        try {
            val wifiManager = App.instance.getSystemService(WIFI_SERVICE) as WifiManager
            return wifiManager.wifiState.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return "0"
        }
    }
    fun radioTypeMY(): String {
        return when ((App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).toString().toIntOrNull()) {
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
            TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_GPRS -> "2G"
            else -> "UNKNOWN"
        }
    }
    private fun getMySimState(slotIndex: Int): Int {
        val tm = App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.getSimState(slotIndex)
            } else {
                0
            }
        } catch (e: Exception) {
            TelephonyManager.SIM_STATE_UNKNOWN
        }
    }
    fun network1(): String {
        val tm = App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkCountryIso ?: ""
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun network2(): String {
        val (sim1, sim2) = PhoneOperatorsUtils.getDualSimCarrier(App.instance)
        val tip = "SIM1: $sim1\nSIM2: $sim2"

        return tip
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun network3(): String {
        val (sim1, sim2) = PhoneOperatorsUtils.getDualSimCarrier(App.instance)
        val tip = "SIM1: $sim1\nSIM2: $sim2"
        return tip
    }
    private fun getSimState(slotIndex: Int): Int {
        val tm = App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.getSimState(slotIndex)
            } else {
                0
            }
        } catch (e: Exception) {
            TelephonyManager.SIM_STATE_UNKNOWN
        }
    }
    fun getLocalIp(): String {
        return try {
            val networkInter = NetworkInterface.getNetworkInterfaces()
            while (networkInter.hasMoreElements()) {
                val network= networkInter.nextElement()
                val inetAdd= network.inetAddresses
                while (inetAdd.hasMoreElements()) {
                    val inetAddre = inetAdd.nextElement()
                    if (!inetAddre.isLoopbackAddress && inetAddre is Inet4Address) {
                        return inetAddre.hostAddress
                    }
                }
            }
            ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    fun getAppSign(): String {
        try {
            val packageInfo = App.instance.packageManager.getPackageInfo(App.instance.packageName, PackageManager.GET_SIGNATURES)
            if (packageInfo != null && packageInfo.signatures != null && packageInfo.signatures?.isNotEmpty() == true) {
                return getSHA1My(packageInfo.signatures!![0].toByteArray())
            } else {
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
    private fun getSHA1My(byteArray: ByteArray): String {
        try {
            val md = MessageDigest.getInstance("SHA1")
            val digest = md.digest(byteArray)
            val hexString = kotlin.text.StringBuilder()

            for (b in digest) {
                hexString.append(String.format("%02X:", b))
            }

            if (hexString.isNotEmpty()) {
                hexString.deleteCharAt(hexString.length - 1)
            }

            return hexString.toString().lowercase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
    @RequiresPermission(allOf = [ Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLastLoc(locationManager: LocationManager): Location? {
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val lastKnownLocation = locationManager.getLastKnownLocation(provider)
            if (lastKnownLocation == null) {
                continue
            }
            if (bestLocation == null || lastKnownLocation.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = lastKnownLocation
            }
        }
        return bestLocation
    }
    @RequiresPermission(allOf = [ Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLoc(): String {
        var loc = GeographicInfo()
        var locationM = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = getLastLoc(locationM)
        if (location != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                loc.isMock = location.isMock
            }
            loc.latitude = location.latitude
            loc.accuracy = location.accuracy.toDouble()
            loc.longitude = location.longitude
            loc.bearing = location.bearing.toDouble()
            loc.altitude = location.altitude
            loc.speed = location.speed.toDouble()
            loc.time = location.time.toString() + ""

            loc.provider = location.getProvider()

            val geocoder = Geocoder(App.instance)
            try {
                val addresses = geocoder.getFromLocation(location.latitude.toDouble(), location.longitude.toDouble(), 1)
                if (addresses != null && !addresses.isEmpty()) {
                    val address = addresses[0]
                    loc.adminArea = address.adminArea ?: ""
                    loc.countryCode = address.countryCode ?: ""
                    loc.countryName = address.countryName ?: ""
                    loc.locality = address.locality ?: ""
                    loc.featureName = address.featureName ?: ""
                    loc.gpsAddress = address.getAddressLine(0) ?: ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return Gson().toJson(loc)
        }else{
            return ""
        }
    }


    @SuppressLint("HardwareIds")
    fun macFromHardware(): String {
        val androidId = Settings.Secure.getString(
            App.instance.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        if (TextUtils.isEmpty(androidId) || "9774d56d682e549c" == androidId) {
            val sp = App.instance.getSharedPreferences("device_info", Context.MODE_PRIVATE)
            var uuid = sp.getString("device_uuid", null)
            if (TextUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString()
                sp.edit { putString("device_uuid", uuid) }
            }
            return uuid.toString()
        } else {
            return androidId
        }
    }
    fun isProxy(): Boolean {
        val proxyPort: Int
        val proxyAddress = System.getProperty("http.proxyHost")
        val portStr = System.getProperty("http.proxyPort")
        proxyPort = (portStr ?: "-1").toInt()
        val b = (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1)
        return b
    }
    fun cor(): String {
        class CpuFilter : FileFilter {
            override fun accept(pathname: File): Boolean {
                return Pattern.matches("cpu[0-9]", pathname.name)
            }
        }
        try {
            val dir = File("/sys/devices/system/cpu/")
            val files = dir.listFiles(CpuFilter())
            return files?.size.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return "1"
        }
    }
    fun wifiB(): Boolean {
        try {
            val network = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = network.activeNetworkInfo
            return activeNetwork?.type == ConnectivityManager.TYPE_WIFI
        } catch (e: Exception) {
            return false
        }

    }
    fun getDataCount(uri: Uri?, projection: Array<String>?): Int {
        var count = 0
        val contentResolver: ContentResolver = App.instance.contentResolver
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null) {
            count = cursor.count
            cursor.close()
        }
        return count
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
    fun isEmulatorMy(): Boolean {
        val checkProperty = Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.lowercase(Locale.getDefault()).contains("vbox") || Build.FINGERPRINT.lowercase(Locale.getDefault()).contains("test-keys") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || "google_sdk" == Build.PRODUCT
        if (checkProperty) return true
        var operatorName = ""
        val tm = App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val name = tm.networkOperatorName
        if (name != null) {
            operatorName = name
        }
        val checkOperatorName = operatorName.lowercase(Locale.getDefault()) == "android"
        if (checkOperatorName) return true
        val url = "tel:" + "10086"
        val intent = Intent()
        intent.data = url.toUri()
        intent.action = Intent.ACTION_DIAL
        val checkDial = intent.resolveActivity(App.instance.packageManager) == null
        if (checkDial) return true
        return false
    }
    fun cameraNumber(): String {
        val manager = App.instance.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraIdList = manager.cameraIdList
            return "${cameraIdList.size}"
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            return ""
        }
    }
    fun getKeyb(): String {
        val hasM = ViewConfiguration.get(App.instance).hasPermanentMenuKey()
        val hasB = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        if (!hasM && !hasB) {
            return "0"
        }
        return "1"
    }
    fun screenS(): String {
        val point = Point()
        val w1 = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d1: DisplayMetrics = App.instance.resources.displayMetrics
        w1.defaultDisplay.getRealSize(point)
        val x: Float = (point.x / d1.xdpi).pow(2f)
        val y: Float = (point.y / d1.ydpi).pow(2.0f)
        val screen = sqrt(x + y)
        return screen.toString()
    }
    fun getStorageSize(path: File): Long {
        try {
            val statFs = StatFs(path.path)
            val blockSize: Long = statFs.blockSizeLong
            val blockCount: Long = statFs.blockCountLong
            return blockSize * blockCount
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
    fun getFreeStorageSize(path: File): Long {
        try {
            val statFs = StatFs(path.path)
            val blockSize: Long = statFs.blockSizeLong
            val availableBlocks: Long = statFs.availableBlocksLong
            return blockSize * availableBlocks
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
    fun height(): String {
        val outSize = Point()
        val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.getDefaultDisplay().getRealSize(outSize)
        val y = outSize.y
        return y.toString() + ""
    }

    fun width(): String {
        val outSize = Point()
        val wm = App.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.getDefaultDisplay().getRealSize(outSize)
        val x = outSize.x
        return x.toString() + ""
    }


    fun getZipData(json: String): String {
        return try {
            val input = json.toByteArray(Charsets.UTF_8)
            val outputStream = ByteArrayOutputStream()
            GZIPOutputStream(outputStream).use { gzip ->
                gzip.write(input)
            }
            Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            "error"
        }
    }

    private fun isSystemA(packageInfo: PackageInfo): Boolean {
        return if (packageInfo.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) != 0) {
            true
        } else {
            false
        }
    }


    fun isDeviceEmulator(): Boolean {
        // ##########New: Highest priority - Accurately exclude Google Pixel (Core Fix 1) ##########
        if (isGooglePixelDevice()) return false

        // The original Build property detection logic → is completely retained without any modifications
        val checkProperty = Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.lowercase(Locale.getDefault()).contains("vbox") ||
                Build.FINGERPRINT.lowercase(Locale.getDefault()).contains("test-keys") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                "google_sdk" == Build.PRODUCT
        if (checkProperty) return true

        // Operator Name Detection → Optimization: Added null value handling to avoid returning "android" misjudgment when there is no SIM (Core Fix 2)
        var operatorName = ""
        val tm = App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val name = tm.networkOperatorName
        if (name != null && name.isNotEmpty()) { // assignAValueOnlyIfTheNameIsNotEmpty
            operatorName = name
        }
        val checkOperatorName = operatorName.lowercase(Locale.getDefault()) == "android"
        if (checkOperatorName) return true

        // Dial-up function detection → Optimization: Replace 10086 with universal number 123 to adapt to overseas/non-mobile real machines (core fix 3)
        val url = "tel:123" // Universal test number, all Android real phone dialing apps can be parsed
        val intent = Intent()
        intent.data = url.toUri()
        intent.action = Intent.ACTION_DIAL
        // Added flags for package manager resolution to improve compatibility
        val checkDial = intent.resolveActivity(App.instance.packageManager) == null
        if (checkDial) return true

        return false
    }

    /**
     * Auxiliary method: Detect whether it is a real machine of Google Pixel series (exclusive feature matching, no missed judgment/misjudgment)
     * Matching rules: Brand is Google + device/product name starts with pixel (Google official fixed name)
     */
    private fun isGooglePixelDevice(): Boolean {
        return try {
            val brand = Build.BRAND.lowercase(Locale.getDefault()).trim()
            val product = Build.PRODUCT.lowercase(Locale.getDefault()).trim()
            val device = Build.DEVICE.lowercase(Locale.getDefault()).trim()

            // 1.Core premise: The brand must be google (Google Pixel exclusive, emulators will not have it)
            val isGoogleBrand = brand == "google"
            if (!isGoogleBrand) return false

            // 2. Google Pixel series official exclusive code list (including 6/7/8/9 series, continue to replenish)
            val pixelDeviceCodes = listOf(
                "oriole", "raven", "bluejay", "cheetah", "panther",
                "lynx", "felix", "husky", "shiba", "redfin", "bramble",
                "sunfish", "coral", "crosshatch", "blueline", "taimen"
            )

            // 3. Matching rules: The code is in the list || Starts with Pixel (covers all old and new Pixel models)
            val isPixelCode = pixelDeviceCodes.contains(product) || pixelDeviceCodes.contains(device)
            val isPixelPrefix = product.startsWith("pixel") || device.startsWith("pixel")

            isPixelCode || isPixelPrefix
        } catch (e: Exception) {
            false
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

    private fun getBatteryPercentage(mContext: Context): Int {
        try {
            val intent = mContext.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val level = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            return ((level / scale.toFloat()) * 100).toInt()
        } catch (e: Exception) {
            return 0
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getBluetoothMacAddress(): String {
        try {
            val adapter = BluetoothAdapter.getDefaultAdapter() ?: return ""
            if (!adapter.isEnabled) {
                return  ""
            }
            return adapter.address
        } catch (e: Exception) {
            return ""
        }
    }

    fun getSystemCameraConfig(mContext: Context): Int {
        try {
            val cameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraIds = cameraManager.cameraIdList
            var maxPixels = 0
            cameraIds.forEach {
                val characteristics =
                    cameraManager.getCameraCharacteristics(it)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    val sizes = map?.getOutputSizes(ImageFormat.JPEG) ?: return 0
                    if (sizes.size == 0) {
                        return 0
                    }
                    maxPixels = sizes[0].width * sizes[0].height
                }
            }
            return maxPixels
        } catch (e: Exception) {
            return 0
        }
    }

    fun getCurrentCountry(mContext: Context): String{
        try {
            return mContext.resources.configuration.locale.displayCountry
        } catch (e: Exception) {
            return ""
        }
    }

    fun getSysFreeStorage(): Long {
        try {
            val dire = Environment.getDataDirectory()
            val statFs = StatFs(dire.path)
            return statFs.availableBytes / (1024 * 1024)
        } catch (e: Exception) {
            return 0
        }
    }

    @SuppressLint("MissingPermission")
    fun getSysNetWorkType(mContext: Context): String {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        if (info?.isConnected == true) {
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                return "wifi"
            }
            if (info.type == ConnectivityManager.TYPE_MOBILE) {
                return "mobile"
            }

        }
        return "unknown"
    }

    fun getSysStorage(): Long {
        try {
            val dire = Environment.getDataDirectory()
            val statFs = StatFs(dire.path)
            return statFs.totalBytes / (1024 * 1024)
        } catch (e: Exception) {
            return 0
        }
    }

    fun getSsidInfo(context: Context, type: Int): String {
        return try {
            val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
            if (wifiManager.isWifiEnabled) {
                val connectionInfo = wifiManager.connectionInfo
                if (type == 0) {
                    connectionInfo.ssid
                } else if (type == 1) {
                    connectionInfo.bssid
                } else if (type == 2) {
                    connectionInfo.macAddress
                } else if (type == 3) {
                    connectionInfo.rssi.toString()
                } else if (type == 4) {
                    connectionInfo.networkId.toString()
                } else if (type == 5) {
                    connectionInfo.linkSpeed.toString()
                } else if (type == 6) {
                    connectionInfo.hiddenSSID.toString()
                } else if (type == 7) {
                    connectionInfo.supplicantState.toString()
                } else if (type == 8) {
                    connectionInfo.frequency.toString()
                } else if (type == 9) {
                    wifiManager.dhcpInfo.dns1.toString()
                } else if (type == 10) {
                    wifiManager.dhcpInfo.dns2.toString()
                } else if (type == 11) {
                    wifiManager.dhcpInfo.netmask.toString()
                } else if (type == 12) {
                    wifiManager.dhcpInfo.gateway.toString()
                } else if (type == 13) {
                    wifiManager.dhcpInfo.serverAddress.toString()
                } else if (type == 14) {
                    wifiManager.dhcpInfo.ipAddress.toString()
                } else {
                    ""
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun getSysKeyboard(): Int {
        val hasPermanentMenuKey = ViewConfiguration.get(App.instance).hasPermanentMenuKey()
        val hasBack = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        if (!hasPermanentMenuKey && !hasBack) {
            return 0
        }
        return 1
    }

    fun getMemberSpace(type: Int): Long {
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




}
