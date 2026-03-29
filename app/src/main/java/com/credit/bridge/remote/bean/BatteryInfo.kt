package com.credit.bridge.remote.bean

class BatteryInfo {
    var customerInfo: String? = null

    var customerId: String? = null

    var androidId: String? = null

    var createTime: String? = null

    var updateTime: String? = System.currentTimeMillis().toString()

    var batteryStatus: String? = null


    var batteryHealth: String? = null

    var batteryPresent: String? = null

    var batteryLevel: String? = null

    var batteryScale: String? = null

    var batteryPlugged: String? = null

    var batteryIconSmall: String? = null

    var batteryVoltage: String? = null

    var batteryTemperature: String? = null

    var batteryTechnology: String? = null
}