package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.AppInfoResponse
import com.credit.bridge.remote.response.LoginResponse
import retrofit2.Response


class AppInfoResponseEvent: BResponseEvent<AppInfoResponse> {

    constructor(basicResponse: AppInfoResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}