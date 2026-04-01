package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.OrderUpdateResponse
import retrofit2.Response


class OrderUpdateResponseEvent: BResponseEvent<OrderUpdateResponse> {

    constructor(basicResponse: OrderUpdateResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}