package com.credit.bridge.content

object ConstConfig {


    const val ORDER_STATUS_REJECTED = "REJECTED"
    const val ORDER_STATUS_OVERDUE = "OVERDUE"
    const val ORDER_STATUS_ISSUING = "ISSUING"
    const val ORDER_STATUS_CLOSED = "CLOSED"
    const val ORDER_STATUS_CURRENT = "CURRENT"
    const val ORDER_STATUS_PAID_OFF = "PAID_OFF"
    const val ORDER_STATUS_PRE_REVIEW = "PRE_REVIEW"
    const val ORDER_STATUS_ISSUE_FAILED = "ISSUE_FAILED"
    const val ORDER_STATUS_READY_TO_ISSUE = "READY_TO_ISSUE"



    //order type
    const val ORDER_CURRENT = "CURRENT"
    const val ORDER_HISTORY = "HISTORY"



    //order details page index
    const val ORDER_DETAIL_COMMON = "common"


    //point page
    const val EVENT_REGISTER_COMPLETE = "register_complete"	//
    const val EVENT_HOME_SCREEN = "home_screen"	//
    const val EVENT_IDCARD_INPUT = "idcard_input"	//
    const val EVENT_IDCARD_SUNMIT = "idcard_submit"	//
    const val EVENT_INTO_INFO = "personal_info_input"	//
    const val EVENT_INFO_SUBMIT = "personal_info_submit"	//
    const val EVENT_CONTACT_INPUT = "contact_input"	//
    const val EVENT_CONTACT_SUBMIT = "contact_submit"	//
    const val EVENT_BANKCARD_INPUT = "bankcard_input"	//
    const val EVENT_BANKCARD_SUBMIT = "bankcard_submit"	//
    const val POINT_START_LIVENESS = "liveness_start"	//

    const val POINT_INPUT_LIVENESS = "liveness_input"	//

    const val POINT_TOUCH_LIVENESS = "liveness_touch"	//

    const val POINT_FAIL_LIVENESS = "liveness_fail"	//

    const val POINT_IDCARD_FAIL = "idcard_fail"	//
    const val POINT_INTO_LOANAMOUNT = "loanamount_info"	//
    const val POINT_LOAN_SUBMIT = "loan_info_submit"	//
    const val POINT_CLICK_CAMERA = "click_camera"	//


    const val POINT_ACTION_TYPE_CLICK="click"
    const val POINT_ACTION_TYPE_HOLD="hold"

}