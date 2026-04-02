package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.OcrPanResponse
import retrofit2.Response


class OcrPanResponseEvent: BResponseEvent<OcrPanResponse> {

    constructor(basicResponse: OcrPanResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}