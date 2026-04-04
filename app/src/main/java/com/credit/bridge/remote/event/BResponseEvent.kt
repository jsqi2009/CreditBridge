package com.credit.bridge.remote.event


import android.util.Log
import com.credit.bridge.remote.response.BResponse


import retrofit2.Response
import kotlin.text.toInt
import kotlin.toString

open class BResponseEvent<T : BResponse> {
    var networkError: Throwable? = null
    var response: Response<*>? = null
    var model: T? = null
    internal var errorMessage: String = ""
    var pageIndex: Int? = -1
    var flagContent: String? = null

    val retMsg: String
        get() = if (model != null) {
            this.model!!.dvusonb!!
        } else {
            ""
        }

    val statusCode: Int
        get() = if (this.response != null) this.response!!.code() else 0

    val isSuccess: Boolean
        get() = this.networkError == null && (model!!.fzpn == 200)

    constructor(t: T, response: Response<*>) {
        this.model = t
        this.response = response
    }

    constructor(t: T, response: Response<*>, errorMessage: String) {
        this.model = t
        this.response = response
        this.errorMessage = errorMessage
    }

    constructor(error: Throwable) {
        this.networkError = error
    }

    constructor(error: Throwable, errorMessage: String) {
        this.networkError = error
        this.errorMessage = errorMessage
    }

    constructor(error: Throwable, position: Any?, flag: Any?) {
        //TimberUtil.logE("", )
        this.networkError = error
        if (position != null) {
            this.pageIndex = position.toString().toInt()
        }
        this.flagContent = flag.toString()
    }

    fun getErrorMessage(): String {
        if (model != null) {
            if (model!!.fzpn == 200 || model!!.fzpn == 201) {
                Log.e("ResponseEvent", "model.getErrorMessage():" + model!!.dvusonb)
                return this.model!!.dvusonb!!
            } else {
                return this.model!!.dvusonb!!
            }
        }

        return "Network connection failed, please check network settings"

    }

    fun hasNetworkError(): Boolean {
        return this.networkError != null
    }

    /*fun hasAccountError(): Boolean {
        return model != null && model!!.errNum > 0
    }*/
}