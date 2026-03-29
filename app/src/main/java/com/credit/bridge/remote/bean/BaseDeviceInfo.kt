package com.credit.bridge.remote.bean

import android.app.Service
import java.io.Serializable

/**
 * author : Jason
 * desc   :
 */
class BaseDeviceInfo: Serializable {

    var appName: String? = null
    var firstInstallTime: String? = null
    var isGameApp: Boolean? = null
    var isSystemApp: Boolean? = null
    var uninstalled: Boolean? = null
    var lastUpdateTime: String? = null
    var packageName: String? = null
    var versionCode: String? = null
    var versionName: String? = null
    var requestedPermissions: MutableList<String>? = null
}