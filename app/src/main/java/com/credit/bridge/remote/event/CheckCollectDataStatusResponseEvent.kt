package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.CheckCollectDataStatusResponse
import retrofit2.Response


class CheckCollectDataStatusResponseEvent: BResponseEvent<CheckCollectDataStatusResponse> {

    constructor(basicResponse: CheckCollectDataStatusResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}