package com.credit.bridge.inter

import com.credit.bridge.remote.bean.OrderInfo


interface OnItemClickListener {

    fun onItemClick(info: OrderInfo)
}