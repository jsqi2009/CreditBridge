package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.CommonResponse
import retrofit2.Response


class VoiceCodeResponseEvent: BResponseEvent<CommonResponse> {

    constructor(basicResponse: CommonResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}