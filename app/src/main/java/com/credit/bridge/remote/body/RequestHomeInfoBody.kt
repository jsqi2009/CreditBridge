package com.credit.bridge.remote.body

import com.credit.bridge.remote.bean.DeviceTypeInfo
import java.io.Serializable

class RequestHomeInfoBody: Serializable {
    var rxplnymc: DeviceTypeInfo = DeviceTypeInfo ()
}
