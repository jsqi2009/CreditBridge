package com.credit.bridge.inter

import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.remote.bean.ProductInfo


interface OnProductItemClickListener {

    fun onItemClick(info: ProductInfo)
}