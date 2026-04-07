package com.credit.bridge.remote.bean

import java.io.Serializable

class HomeInfo: Serializable {
    var otytwlcq: HomeUserInfo = HomeUserInfo()  //user info
    var hahsraev: HomeOrderInfo = HomeOrderInfo()  //home page info
    var zydllhkuuvpqz: ArrayList<OrderInfo>? = null   //order info

}

class HomeUserInfo: Serializable {
    var dtwfaeaf: String = ""
    var gkdtfbvtbvquxbewhmn: Int = 0
}

class HomeOrderInfo: Serializable {
    var lwgdzqyuks: Boolean = false
    var hxwklbbxhcxyjbfhv: String? = ""
    var gdcuhe: String? = null
}