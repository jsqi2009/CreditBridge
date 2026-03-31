package com.credit.bridge.remote.body

class RequestInstalledPackageBody <T>(
    var protocolVersion: String = "",
    var protocolName: String = "",
    var totalNumber: Long = 0L,
    var latestTime: Long = 0L,
    var earliestTime: Long = 0L,
    var updateTime: Long = 0L,
    var createTime: String = "",
    var latestDate: String = "",
    var earliestDate: String = "",
    var readNumber: Long = 0L,
    var data: Array<T>


)

