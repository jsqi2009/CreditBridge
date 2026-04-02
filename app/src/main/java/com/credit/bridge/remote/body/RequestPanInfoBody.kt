package com.credit.bridge.remote.body
import java.io.Serializable

class RequestPanInfoBody: Serializable {
    var bcikobrx: String = "" //fullName
    var gscxhjjfy: String = ""//panNumber
    var ticgsg: String = ""//gender
    var zcpoxpzb: String = ""//birthday
}