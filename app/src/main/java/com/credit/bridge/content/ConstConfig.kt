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
    const val EVENT_REQUEST_SMS_CODE = "requestSmsCode"	//
    const val EVENT_SMS_OPT_UNAVAILABLE = "smsOtpUnavailable"	//
    const val EVENT_REQUEST_VOICE_CODE = "requestVoiceCode"	//
    const val EVENT_VOICE_OPT_UNAVAILABLE = "voiceOtpUnavailable"	//
    const val EVENT_START_SIGNUP = "startSignup"	//

    const val EVENT_REQUEST_VERIFICATION_START = "requestVerificationStart"	//
    const val EVENT_REGISTER_COMPLETE = "signupFinished"	//
    const val EVENT_HOME_SCREEN = "home_screen"	//
    const val EVENT_IDCARD_INPUT = "openVerificationGuide"	//
    const val EVENT_IDCARD_SUNMIT = "submitIdDocument"	//
    const val EVENT_INTO_INFO = "openBasicForm"	//
    const val EVENT_INFO_SUBMIT = "saveBasicForm"	//
    const val EVENT_CONTACT_INPUT = "openContactForm"	//
    const val EVENT_CONTACT_SUBMIT = "saveContactForm"	//
    const val EVENT_BANKCARD_INPUT = "openBankForm"	//
    const val EVENT_BANKCARD_SUBMIT = "saveBankForm"	//

    const val EVENT_APPLY_LIST = "submitCreditAmount"	//
    const val EVENT_APPLY_DETAIL = "submitCreditAmountdetails"	//


    const val EVENT_TAP_VERIFY_IDENTITY = "tapVerifyIdentity"	//
    const val EVENT_START_DOCUMENT_REVIEW = "startDocumentReview"	//
    const val EVENT_DOCUMENT_REVIEW_FAIL = "documentReviewUnavailable"	//
    const val EVENT_UPLOAD_ID_DOCUMENT = "uploadIdDocument"	//
    const val EVENT_UPLOAD_ID_DOCUMENT_SUCCESS = "idDocumentUploaded"	//
    const val EVENT_UPLOAD_ID_DOCUMENT_FAIL = "idDocumentUploadUnavailable"	//

    const val EVENT_START_LIVENESS = "startFaceCheck"	//
    const val EVENT_UPLOAD_FACE_CHECK = "uploadFaceCheck"	//
    const val EVENT_UPLOAD_FACE_SUCCESS = "faceCheckUploaded"	//
    const val EVENT_UPLOAD_FACE_FAIL = "faceCheckUploadUnavailable"	//
    const val EVENT_SUBMIT_FACE = "submitFaceCheck"	//
    const val EVENT_SELECT_CREDIT_AMOUNT = "selectCreditAmount"	//


    const val EVENT_FAIL_LIVENESS = "faceCheckUploadUnavailable"	//
    const val EVENT_IDCARD_FAIL = "idDocumentUploadUnavailable"	//
    const val EVENT_ACTION_CLICK = "click"
    const val EVENT_ACTION_HOLD = "hold"

}