package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.CommonIntResponse
import retrofit2.Response


class OcrFaceNumberResponseEvent: BResponseEvent<CommonIntResponse> {

    constructor(basicResponse: CommonIntResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}