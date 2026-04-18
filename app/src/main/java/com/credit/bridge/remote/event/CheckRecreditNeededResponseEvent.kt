package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.BResponse
import com.credit.bridge.remote.response.CommonBoolResponse
import retrofit2.Response


class CheckRecreditNeededResponseEvent: BResponseEvent<CommonBoolResponse> {

    constructor(basicResponse: CommonBoolResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}