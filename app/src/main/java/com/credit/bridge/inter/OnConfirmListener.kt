package com.credit.bridge.inter

import com.credit.bridge.remote.bean.OrderInfo


interface OnConfirmListener {

    fun onClick(info: String)
}