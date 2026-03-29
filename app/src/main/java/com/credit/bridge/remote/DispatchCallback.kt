package com.credit.bridge.remote


import com.credit.bridge.remote.response.BResponse
import retrofit2.Response


interface DispatchCallback<T : BResponse> {

    fun onDispatchError(paramT: T, paramResponse: Response<*>)

    fun onDispatchNetworkError(paramRetrofitError: Throwable)

    fun onDispatchNetworkError(paramRetrofitError: Throwable, index: Any, flag: Any)

    fun onDispatchSuccess(paramT: T, paramResponse: Response<*>)

    fun onDispatchLogout()
}