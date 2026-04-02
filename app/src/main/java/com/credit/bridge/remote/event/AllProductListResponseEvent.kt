package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.AllProductListResponse
import retrofit2.Response


class AllProductListResponseEvent: BResponseEvent<AllProductListResponse> {

    constructor(basicResponse: AllProductListResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}