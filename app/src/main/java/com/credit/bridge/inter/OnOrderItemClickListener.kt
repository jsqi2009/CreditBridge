package com.credit.bridge.inter

import com.credit.bridge.remote.bean.OrderInfo


interface OnOrderItemClickListener {

    fun onOrderItemClick(info: OrderInfo)
    fun onViewPaymentOptionsClick(info: OrderInfo)
    fun onPaymentClick(info: OrderInfo)
}