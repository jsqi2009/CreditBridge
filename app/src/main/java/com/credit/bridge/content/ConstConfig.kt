package com.credit.bridge.content

/**
 * author : Jason
 * desc   :
 */
object ConstConfig {


    //bill status
    const val BILL_STATUS_PROCESSING = "Processing"
    const val BILL_STATUS_PAST_DUE = "Past Due"
    const val BILL_STATUS_PENDING = "Pending"
    const val BILL_STATUS_REPAID = "Repaid"
    const val BILL_STATUS_PAID = "Paid"
    const val BILL_STATUS_CANCELED = "Canceled"
    const val BILL_STATUS_CLOSED = "Closed"
    const val BILL_STATUS_DUE = "Due"
    const val BILL_STATUS_ISSUE_FAILED = "Issue Failed"
    const val BILL_STATUS_NOT_COMPLETED = "Not Completed"



    //order type
    const val ORDER_TYPE_CURRENT = "CURRENT"
    const val ORDER_TYPE_HISTORY = "HISTORY"

    const val ORDER_TYPE_FAILED = "FAILED"

    //order details page index
    const val ORDER_DETAIL_COMMON = "common"
    const val ORDER_DETAIL_TRANSFER = "transfer"


    const val POINT_REGISTER_COMPLETE = "register_complete"	//
    const val POINT_HOME_SCREEN = "home_screen"	//
    const val POINT_IDCARD_INPUT = "idcard_input"	//
    const val POINT_IDCARD_SUNMIT = "idcard_submit"	//
    const val POINT_INTO_INFO = "personal_info_input"	//
    const val POINT_INFO_SUBMIT = "personal_info_submit"	//
    const val POINT_CONTACT_INPUT = "contact_input"	//
    const val POINT_CONTACT_SUBMIT = "contact_submit"	//
    const val POINT_BANKCARD_INPUT = "bankcard_input"	//
    const val POINT_BANKCARD_SUBMIT = "bankcard_submit"	//
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