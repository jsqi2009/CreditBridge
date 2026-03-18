package com.credit.bridge.util

import kotlin.collections.find
import kotlin.text.isNullOrBlank

/**
 * author : Jason
 * desc   :
 */
enum class OrderStatus(val desc: String) {
    REJECTED(""),
    OVERDUE(""),
    ISSUING(""),
    CLOSED(""),
    CURRENT(""),
    PAID_OFF(""),
    PRE_REVIEW(""),
    ISSUE_FAILED(""),
    READY_TO_ISSUE("");

    companion object {
        fun getStatusByValue(statusStr: String?): OrderStatus? {
            if (statusStr.isNullOrBlank()) return null
            return entries.find { it.name == statusStr }
        }
    }
}