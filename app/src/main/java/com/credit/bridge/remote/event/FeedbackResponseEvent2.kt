package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.BResponse
import retrofit2.Response


class FeedbackResponseEvent2: BResponseEvent<BResponse> {

    constructor(basicResponse: BResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}