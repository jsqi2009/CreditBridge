package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.OrderListResponse
import retrofit2.Response


class OrderLinkBankResponseEvent: BResponseEvent<OrderListResponse> {

    constructor(basicResponse: OrderListResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}