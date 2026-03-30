package com.credit.bridge.remote.event

import com.credit.bridge.remote.response.LoginResponse
import retrofit2.Response


class LoginResponseEvent: BResponseEvent<LoginResponse> {

    constructor(basicResponse: LoginResponse, response: Response<*>) : super(basicResponse, response) {}

    constructor(paramRetrofitError: Throwable) : super(paramRetrofitError) {}
}