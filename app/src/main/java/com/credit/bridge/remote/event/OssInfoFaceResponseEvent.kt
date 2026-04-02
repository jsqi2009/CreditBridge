package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.OssInfoResponse
import retrofit2.Response


class OssInfoFaceResponseEvent: BResponseEvent<OssInfoResponse> {

    constructor(basicResponse: OssInfoResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}