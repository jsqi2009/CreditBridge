package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.CommonResponse
import com.credit.bridge.remote.response.LoginResponse
import retrofit2.Response


class SaveQuestion3ResponseEvent: BResponseEvent<CommonResponse> {

    constructor(basicResponse: CommonResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}