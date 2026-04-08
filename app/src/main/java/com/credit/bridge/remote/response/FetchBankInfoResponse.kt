package com.credit.bridge.remote.response

class FetchBankInfoResponse: BResponse (){
    var mtaw: BankInfo? = null
}

class BankInfo{
    var qmtddx: String? = ""  //account
    var rcpqzqrn: String? = ""  //ifsc
    var xzafqxn: String? = ""  //card name
    var viqicnncqg : String? = ""

}