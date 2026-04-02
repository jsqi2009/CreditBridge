package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.FetchFeedbackConfigResponse
import retrofit2.Response


class FetchFeedbackConfigResponseEvent: BResponseEvent<FetchFeedbackConfigResponse> {

    constructor(basicResponse: FetchFeedbackConfigResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}