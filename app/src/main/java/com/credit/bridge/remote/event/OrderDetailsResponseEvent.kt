package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.OrderDetailsResponse
import retrofit2.Response


class OrderDetailsResponseEvent: BResponseEvent<OrderDetailsResponse> {

    constructor(basicResponse: OrderDetailsResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}