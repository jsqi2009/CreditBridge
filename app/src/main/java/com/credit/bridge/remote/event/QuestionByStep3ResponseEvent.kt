package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.HomeInfoResponse
import com.credit.bridge.remote.response.QuestionByStepResponse
import retrofit2.Response


class QuestionByStep3ResponseEvent: BResponseEvent<QuestionByStepResponse> {

    constructor(basicResponse: QuestionByStepResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}