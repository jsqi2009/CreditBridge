package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.FetchBankInfoResponse
import retrofit2.Response


class FetchBankInfoResponseEvent: BResponseEvent<FetchBankInfoResponse> {

    constructor(basicResponse: FetchBankInfoResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}