package com.credit.bridge.util

import com.credit.bridge.remote.bean.CollectDataInfo
import com.credit.bridge.remote.bean.HomeInfo

object HomeSessionState {
    var isAuthed: Boolean = false
    var currentStep: Int = 0
    var homeInfo: HomeInfo? = null

    fun restoreFromCache() {
        if (CacheManager.isUserVerified) {
            isAuthed = true
        }
    }

    fun updateCollectInfo(info: CollectDataInfo) {
        currentStep = info.lrksnnsd
        setVerified(info.rvazxrtziwtcvrfrkzczx)
    }

    fun setVerified(verified: Boolean) {
        if (isAuthed && !verified) return
        isAuthed = verified
        CacheManager.isUserVerified = verified
    }

    fun clear() {
        isAuthed = false
        currentStep = 0
        homeInfo = null
        CacheManager.isUserVerified = false
    }
}
