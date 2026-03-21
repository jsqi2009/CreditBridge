package com.credit.bridge.inter

import com.credit.bridge.remote.bean.OrderInfo


interface OrderItemClickListener {

    fun onItemClick(info: OrderInfo)
}