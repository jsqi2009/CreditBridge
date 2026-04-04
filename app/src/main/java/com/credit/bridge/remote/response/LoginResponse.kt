package com.credit.bridge.remote.response


class LoginResponse: BResponse() {

    var mtaw: LoginBean? = null

}
class LoginBean{
    var igfid: String = ""   //token
    var gtejmcvokzutw: Boolean = false   // is new user
}


