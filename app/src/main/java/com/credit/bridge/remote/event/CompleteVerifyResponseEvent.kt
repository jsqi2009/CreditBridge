package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.BResponse
import com.credit.bridge.remote.response.LoginResponse
import retrofit2.Response


class CompleteVerifyResponseEvent: BResponseEvent<BResponse> {

    constructor(basicResponse: BResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}