package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.HomeInfoResponse
import retrofit2.Response


class HomeInfoResponseEvent: BResponseEvent<HomeInfoResponse> {

    constructor(basicResponse: HomeInfoResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}