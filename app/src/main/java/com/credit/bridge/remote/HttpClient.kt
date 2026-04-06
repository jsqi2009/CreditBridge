package com.credit.bridge.remote

import RequestOrderUpdateBody
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.provider.Settings
import androidx.annotation.RequiresPermission
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.R
import com.credit.bridge.content.AndroidBus
import com.credit.bridge.content.Contants
import com.credit.bridge.remote.bean.BaseUserInfo
import com.credit.bridge.remote.body.RequestBankInfoBody
import com.credit.bridge.remote.body.RequestContactBody
import com.credit.bridge.remote.body.RequestFeedbackBody
import com.credit.bridge.remote.body.RequestHomeInfoBody
import com.credit.bridge.remote.body.RequestInstalledPackageBody
import com.credit.bridge.remote.body.RequestOcrPanBody
import com.credit.bridge.remote.body.RequestOcrPanNumberBody
import com.credit.bridge.remote.body.RequestOrderDetailsBody
import com.credit.bridge.remote.body.RequestOrderLinkBankBody
import com.credit.bridge.remote.body.RequestOrderListBody
import com.credit.bridge.remote.body.RequestPanInfoBody
import com.credit.bridge.remote.body.RequestSaveQuestionBody
import com.credit.bridge.remote.body.RequestSubmitOrderBody
import com.credit.bridge.remote.body.RequestVerifyCodeBody
import com.credit.bridge.remote.body.RequestVoiceCodeBody
import com.credit.bridge.remote.event.AllProductListResponseEvent
import com.credit.bridge.remote.event.BResponseEvent
import com.credit.bridge.remote.event.CheckCollectDataStatusResponseEvent
import com.credit.bridge.remote.event.CheckUploadStatusResponseEvent
import com.credit.bridge.remote.event.FeedbackResponseEvent
import com.credit.bridge.remote.event.FetchBankInfoResponseEvent
import com.credit.bridge.remote.event.FetchFeedbackConfigResponseEvent
import com.credit.bridge.remote.event.HomeInfoResponseEvent
import com.credit.bridge.remote.event.LoginResponseEvent
import com.credit.bridge.remote.event.LogoutResponseEvent
import com.credit.bridge.remote.event.OcrFaceNumberResponseEvent
import com.credit.bridge.remote.event.OcrPanNumberResponseEvent
import com.credit.bridge.remote.event.OcrPanResponseEvent
import com.credit.bridge.remote.event.OrderDetailsResponseEvent
import com.credit.bridge.remote.event.OrderLinkBankResponseEvent
import com.credit.bridge.remote.event.OrderListResponseEvent
import com.credit.bridge.remote.event.OrderUpdateResponseEvent
import com.credit.bridge.remote.event.OssInfoFaceResponseEvent
import com.credit.bridge.remote.event.OssInfoResponseEvent
import com.credit.bridge.remote.event.PaymentLinkDetailsResponseEvent
import com.credit.bridge.remote.event.PaymentLinkResponseEvent
import com.credit.bridge.remote.event.PolicyLinkResponseEvent
import com.credit.bridge.remote.event.PrivacyPolicyUrlResponseEvent
import com.credit.bridge.remote.event.QuestionByStep1ResponseEvent
import com.credit.bridge.remote.event.QuestionByStep2ResponseEvent
import com.credit.bridge.remote.event.QuestionByStep3ResponseEvent
import com.credit.bridge.remote.event.QuestionByStep4ResponseEvent
import com.credit.bridge.remote.event.QuestionByStep5ResponseEvent
import com.credit.bridge.remote.event.RequestZipDataBody
import com.credit.bridge.remote.event.SaveQuestion1ResponseEvent
import com.credit.bridge.remote.event.SaveQuestion2ResponseEvent
import com.credit.bridge.remote.event.SaveQuestion3ResponseEvent
import com.credit.bridge.remote.event.SaveQuestion4ResponseEvent
import com.credit.bridge.remote.event.SaveQuestion5ResponseEvent
import com.credit.bridge.remote.event.SubmitOrderResponseEvent
import com.credit.bridge.remote.event.UploadInstalledPackageListResponseEvent
import com.credit.bridge.remote.event.UploadSystemResponseEvent
import com.credit.bridge.remote.event.UserCreditResponseEvent
import com.credit.bridge.remote.event.VerifyBankInfoResponseEvent
import com.credit.bridge.remote.event.VerifyCodeResponseEvent
import com.credit.bridge.remote.event.VerifyBaseUserInfoResponseEvent
import com.credit.bridge.remote.event.VerifyContactInfoResponseEvent
import com.credit.bridge.remote.event.VerifyOcrFaceResponseEvent
import com.credit.bridge.remote.event.VerifyPanInfoResponseEvent
import com.credit.bridge.remote.event.VoiceCodeResponseEvent
import com.credit.bridge.remote.response.AllProductListResponse
import com.credit.bridge.remote.response.BResponse
import com.credit.bridge.remote.response.CheckCollectDataStatusResponse
import com.credit.bridge.remote.response.CommonBoolResponse
import com.credit.bridge.remote.response.CommonIntResponse
import com.credit.bridge.remote.response.CommonResponse
import com.credit.bridge.remote.response.FetchBankInfoResponse
import com.credit.bridge.remote.response.FetchFeedbackConfigResponse
import com.credit.bridge.remote.response.HomeInfoResponse
import com.credit.bridge.remote.response.LoginResponse
import com.credit.bridge.remote.response.OcrPanResponse
import com.credit.bridge.remote.response.OrderDetailsResponse
import com.credit.bridge.remote.response.OrderListResponse
import com.credit.bridge.remote.response.OrderUpdateResponse
import com.credit.bridge.remote.response.OssInfoResponse
import com.credit.bridge.remote.response.QuestionByStepResponse
import com.credit.bridge.ui.App
import com.credit.bridge.util.DeviceInfoUtil
import com.credit.bridge.util.SystemDataUtils
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import javax.net.ssl.TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import kotlin.jvm.java

object HttpClient {

    private val HTTP_RESPONSE_CACHE = 10485760L
    private val HTTP_TIMEOUT_MS = 60 * 1000
    private var mHttpApi: HttpApi? = null
    private var httpClient: OkHttpClient? = null
    private var mBus: AndroidBus? = null
    var authorization: String? = null
    var dispatchClient: DispatchClient? = null
    var severRootUrl: String? = null


    fun init(context: Context, bus: AndroidBus) {
        severRootUrl = Contants.BASE_SERVER_URL
        initOkHTTP(context)
        mBus = bus
        dispatchClient = DispatchClient(context, mBus!!)
    }


    private fun initOkHTTP(context: Context) {
        httpClient = provideOkHttpClient(context)
        initHttpClientApi()
    }

    private fun provideOkHttpClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.apply {
            connectTimeout(HTTP_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
            readTimeout(HTTP_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
            addInterceptor(loggingInterceptor)
            addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                chain.proceed(request.build())
            }
            createInsecureSslSocketFactory()
            builder.hostnameVerifier { hostname, session -> true }
        }
        return builder.build()
    }
    private fun createInsecureSslSocketFactory(): SSLSocketFactory {
        try {
            val context = SSLContext.getInstance("TLS")
            val permissive = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
                    checkServerTrusted(certs, authType)
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            context.init(null, arrayOf<TrustManager>(permissive), null)
            return context.socketFactory
        } catch (e: Exception) {
            throw AssertionError(e)
        }

    }

    private fun initHttpClientApi() {
        try {
            val restAdapter = Retrofit.Builder()
                .client(httpClient!!)
                .baseUrl(severRootUrl!!)
                //.baseUrl(mSession!!.baseServerURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            mHttpApi = restAdapter.create(HttpApi::class.java)
        } catch (E: Throwable) {

        }

    }

    private fun getHeaders(mContext: Context): HashMap<String, String> {

        val packageInfo: PackageInfo = mContext.packageManager.getPackageInfo(mContext.packageName, 0)
        val headerMap: HashMap<String, String> = HashMap<String, String>()

        headerMap["X-RRR-DZVRAKC-MMKJ"] = packageInfo.versionName.toString()
        headerMap["X-CON-VHHD"] = "ANDROID"
        headerMap["X-NHCKUD-VHQAORMXP"] = CacheManager.afChannel
        headerMap["X-XRS-QLAE"] = mContext.resources.getString(R.string.app_name)
        headerMap["X-ENWYHNRJ"] = ""
        headerMap["X-BHW-JVMM"] = CacheManager.smsCode
        headerMap["X-IYAOIEZT-IGYAM"] = ""   //Firebase token
        headerMap["X-CRFG-WWRIH"] = CacheManager.token
        headerMap["X-QSHIECIQ-QUJKZFAHFS"] = App.instance.googleAdIdResult?.gaid ?: ""
        headerMap["X-VTZCTGQW-WRR"] = CacheManager.afChannel
        headerMap["XX-QSNNRMS-XYL"] = "X-CAPTCHA-SID"
        headerMap["X-HKR-UQCLRVW"] = packageInfo.versionCode.toString()
        headerMap["X-YGEOCR-SYUU"] = AppsFlyerLib.getInstance().getAppsFlyerUID(App.instance) ?: ""
        headerMap["X-AQITBIX-JA"] = SystemDataUtils.getAndroidId()
        headerMap["X-SOH-TILKQCJ-YORU"] = "com.get.credit.full.fin.android"
        headerMap["X-UE-DU"] = "X-AF-ID"
        headerMap["X-ZTXHWJ-IXOA"] = ""    //device name
        headerMap["X-QXMQSXJMGCD"] = ""
        headerMap["X-UHV-CL"] = ""
        headerMap["X-ID-NB"] = App.instance.googleAdIdResult?.gaid ?: ""
        headerMap["X-GA-ID"] = App.instance.googleAdIdResult?.gaid ?: ""

        return headerMap
    }

    fun sendVerifyCode(mContext: Context, mobile: String, type: String) {
        val body = RequestVerifyCodeBody()
        body.awja = type
        body.phajox = mobile
        val call = mHttpApi!!.requestPostVerifyCode(getHeaders(mContext), Contants.URL_SEND_SMS, body)
        dispatchClient?.enqueue(call, CommonResponse::class.java, VerifyCodeResponseEvent::class.java)
    }

    fun getVoiceCode(mContext: Context, mobile: String) {

        val body = RequestVoiceCodeBody()
        body.sucbzl = mobile
        val call = mHttpApi!!.requestPostVoiceCode(getHeaders(mContext), Contants.URL_GET_VOICE, body)
        dispatchClient?.enqueue(call, CommonResponse::class.java, VoiceCodeResponseEvent::class.java)
    }

    fun login(mContext: Context, mobile: String) {
        val formMap: HashMap<String, Any> = HashMap()
        formMap[RequestParams.mobile_login] = mobile
        val call = mHttpApi!!.requestPost1(getHeaders(mContext), Contants.URL_LOGIN_SMS, formMap)
        dispatchClient!!.enqueue(call, LoginResponse::class.java, LoginResponseEvent::class.java)
    }

    fun checkCollectDataStatus(mContext: Context) {
        val call = mHttpApi!!.requestGetAuth(getHeaders(mContext), Contants.URL_COLLECT_DATA_INTEGRITY)
        dispatchClient!!.enqueue(call, CheckCollectDataStatusResponse::class.java,
            CheckCollectDataStatusResponseEvent::class.java)
    }

    fun eventReport(mContext: Context, tag: String, actionType: String, status: String, reportType: String = "user") {

        val eventType =  HashMap<String, Any>()
        eventType[tag] = ""
        AppsFlyerLib.getInstance().logEvent(mContext, tag, eventType)

        val formMap: HashMap<String, Any> = HashMap()
        formMap[RequestParams.actionType] = actionType
        formMap[RequestParams.status] = status
        formMap[RequestParams.reportType] = reportType
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_POINT_REPORT, formMap)
        dispatchClient!!.enqueue(call, BResponse::class.java, BResponseEvent::class.java)
    }

    fun getHomeInfo(mContext: Context, body: RequestHomeInfoBody) {

        val call = mHttpApi!!.requestPostHomeInfo(getHeaders(mContext), Contants.URL_HOME, body)
        dispatchClient!!.enqueue(call, HomeInfoResponse::class.java, HomeInfoResponseEvent::class.java)
    }

    @SuppressLint("HardwareIds")
    fun checkUploadStatus(mContext: Context) {
        val formMap: HashMap<String, Any> = HashMap()
        formMap[Contants.imei_param] = Settings.Secure.getString(App.instance.contentResolver, Settings.Secure.ANDROID_ID)
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_CHECK_UPLOAD_STATUS, formMap)
        dispatchClient!!.enqueue(call, CommonBoolResponse::class.java,
            CheckUploadStatusResponseEvent::class.java)
    }

    fun getPrivacyPolicyUrl(mContext: Context) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap["qxcndlgiiu"] = "declaration"
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_PRIVTE,formMap)
        dispatchClient?.enqueue(call, CommonResponse::class.java, PrivacyPolicyUrlResponseEvent::class.java)
    }

    @SuppressLint("HardwareIds")
    fun uploadInstalledPackageList(mContext: Context) {

        val installedPackageBody = RequestInstalledPackageBody(
            protocolName = "INSTALLED_APP",
            data = SystemDataUtils.getInstalledAppList(mContext)
        )
        val jsonList = Gson().toJson(installedPackageBody)
        val zipString = SystemDataUtils.getZipData(jsonList)
        val requestZipBody = RequestZipDataBody()
        requestZipBody.phajox = CacheManager.mobile
        requestZipBody.guawnoc = zipString
        requestZipBody.guxf = DeviceInfoUtil.getAndroidId()
        val call = mHttpApi!!.requestPostZipData(getHeaders(mContext), Contants.URL_GZIP,requestZipBody)
        dispatchClient!!.enqueue(call, CommonResponse::class.java,
            UploadInstalledPackageListResponseEvent::class.java)
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE])
    @SuppressLint("HardwareIds")
    fun uploadSystemInfo(mContext: Context) {
        var messageBody = RequestInstalledPackageBody(protocolName = "DEVICE_INFO" , data = SystemDataUtils.getDeviceInfo(mContext))
        val json = Gson().toJson(messageBody)
        val zipString = SystemDataUtils.getZipData(json)
        var requestZipBody = RequestZipDataBody()
        requestZipBody.phajox = CacheManager.mobile
        requestZipBody.guawnoc = zipString
        requestZipBody.guxf = Settings.Secure.getString(App.instance.contentResolver, Settings.Secure.ANDROID_ID)
        val call = mHttpApi!!.requestPostZipData(getHeaders(mContext), Contants.URL_GZIP,requestZipBody)
        dispatchClient!!.enqueue(call, CommonResponse::class.java, UploadSystemResponseEvent::class.java)
    }

    fun fetchOrderList(mContext: Context, type: String, flag: String) {

        val orderBody = RequestOrderListBody()
        orderBody.qfve = type

        val call = mHttpApi!!.requestPostOrderList(getHeaders(mContext), Contants.URL_ORDER_LIST, orderBody)
        dispatchClient!!.enqueue(call, OrderListResponse::class.java, OrderListResponseEvent::class.java,flag)
    }

    fun getPaymentLink(mContext: Context, extensionStatus : Boolean,orderId : String, type : Int) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap[Contants.extension] = extensionStatus
        formMap[Contants.loanAppId] = orderId
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_GET_DEPOSIT,formMap)
        if (type == 1) {
            dispatchClient!!.enqueue(call, CommonResponse::class.java, PaymentLinkResponseEvent::class.java)
        }else if (type == 2) {
            dispatchClient!!.enqueue(call, CommonResponse::class.java,
                PaymentLinkDetailsResponseEvent::class.java)
        }
    }

    fun getOrderDetails(mContext: Context, body: RequestOrderDetailsBody, flag: String) {

        val call = mHttpApi!!.requestPostOrderDetails(getHeaders(mContext), Contants.URL_ORDER_DETAIL, body)
        dispatchClient!!.enqueue(call, OrderDetailsResponse::class.java, OrderDetailsResponseEvent::class.java, flag)
    }


    fun getOrderUpdateInfo(mContext: Context, extensionPeriod: Int, loanAppId: Int) {

        val body = RequestOrderUpdateBody()
        body.nhtfrspjg = loanAppId
        body.tcxjwmpdlhudlcu = extensionPeriod

        val call = mHttpApi!!.requestPostOrderUpdate(getHeaders(mContext), Contants.URL_APPLY_DETAIL, body)
        dispatchClient!!.enqueue(call, OrderUpdateResponse::class.java, OrderUpdateResponseEvent::class.java)
    }

    fun getAllProductList(mContext: Context) {

        val call = mHttpApi!!.requestPost(getHeaders(mContext), Contants.URL_GET_PRODUCTION_INFO)
        dispatchClient!!.enqueue(call, AllProductListResponse::class.java,
            AllProductListResponseEvent::class.java)
    }

    fun submitOrder(mContext: Context, requestBody: ArrayList<RequestSubmitOrderBody>, flag: String) {

        val call = mHttpApi!!.requestPostSubmitOrder(getHeaders(mContext), Contants.URL_CREATE_ORDER, requestBody)
        dispatchClient!!.enqueue(call, BResponse::class.java, SubmitOrderResponseEvent::class.java, flag)
    }

    fun fetchBankInfo(mContext: Context) {

        val call = mHttpApi!!.requestGet(getHeaders(mContext), Contants.URL_GET_BANK_INFO)
        dispatchClient?.enqueue(call, FetchBankInfoResponse::class.java, FetchBankInfoResponseEvent::class.java)
    }

    fun getOrderLinkBank(mContext: Context, cardNo: String) {

        val orderBody = RequestOrderLinkBankBody()
        orderBody.aosqii = cardNo
        val call = mHttpApi!!.requestPostOrderLinkBank(getHeaders(mContext), Contants.URL_ORDER_BANK, orderBody)
        dispatchClient!!.enqueue(call, OrderListResponse::class.java, OrderLinkBankResponseEvent::class.java)
    }

    fun logout(mContext: Context) {

        val call = mHttpApi!!.requestPost(getHeaders(mContext), Contants.URL_LOGOUT)
        dispatchClient!!.enqueue(call, BResponse::class.java, LogoutResponseEvent::class.java)
    }

    fun fetchFeedbackConfig(mContext: Context) {
        val call = mHttpApi!!.requestGet(getHeaders(mContext), Contants.URL_FEEDBACK_CONFIG)
        dispatchClient!!.enqueue(call, FetchFeedbackConfigResponse::class.java, FetchFeedbackConfigResponseEvent::class.java)
    }

    fun submitFeedback(mContext: Context,body: RequestFeedbackBody) {

        val call = mHttpApi!!.requestPostFeedback(getHeaders(mContext), Contants.URL_FEEDBACK, body)
        dispatchClient!!.enqueue(call, BResponse::class.java, FeedbackResponseEvent::class.java)
    }

    fun getPolicyLink(mContext: Context) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap["alfekfdvov"] = "policy"
        val call = mHttpApi!!.requestGetQueryMap(getHeaders(mContext), Contants.URL_PRIVTE,formMap)
        dispatchClient?.enqueue(call, CommonResponse::class.java, PolicyLinkResponseEvent::class.java)
    }


    fun getQuestionByStep(mContext: Context, currentStep: Int) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap["pwtxmyagbetx"] = currentStep
        val call = mHttpApi!!.requestGetQueryMap(getHeaders(mContext), Contants.URL_QUESTION_BY_STEP,formMap)
        if (currentStep == 1) {
            dispatchClient?.enqueue(call, QuestionByStepResponse::class.java,
                QuestionByStep1ResponseEvent::class.java)
        }else if (currentStep == 2) {
            dispatchClient?.enqueue(call, QuestionByStepResponse::class.java,
                QuestionByStep2ResponseEvent::class.java)
        }else if (currentStep == 3) {
            dispatchClient?.enqueue(call, QuestionByStepResponse::class.java,
                QuestionByStep3ResponseEvent::class.java)
        }else if (currentStep == 4) {
            dispatchClient?.enqueue(call, QuestionByStepResponse::class.java,
                QuestionByStep4ResponseEvent::class.java)
        }else if (currentStep == 5) {
            dispatchClient?.enqueue(call, QuestionByStepResponse::class.java,
                QuestionByStep5ResponseEvent::class.java)
        }
    }

    fun saveQuestionInfo(mContext: Context, body: ArrayList<RequestSaveQuestionBody>, currentStep: Int) {

        val call = mHttpApi!!.requestPostSaveQuestionInfo(getHeaders(mContext), Contants.URL_SAVE_QUESTION_INFO,body)
        if (currentStep == 1) {
            dispatchClient?.enqueue(call, CommonResponse::class.java,
                SaveQuestion1ResponseEvent::class.java, currentStep.toString())
        }else if (currentStep == 2) {
            dispatchClient?.enqueue(call, CommonResponse::class.java,
                SaveQuestion2ResponseEvent::class.java, currentStep.toString())
        }else if (currentStep == 3) {
            dispatchClient?.enqueue(call, CommonResponse::class.java,
                SaveQuestion3ResponseEvent::class.java, currentStep.toString())
        }else if (currentStep == 4) {
            dispatchClient?.enqueue(call, CommonResponse::class.java,
                SaveQuestion4ResponseEvent::class.java, currentStep.toString())
        }else if (currentStep == 5) {
            dispatchClient?.enqueue(call, CommonResponse::class.java,
                SaveQuestion5ResponseEvent::class.java, currentStep.toString())
        }
    }


    fun verifyBaseUserInfo(mContext: Context, childrenNumber: String, email: String, employmentStatues: String, lastEducation: String, maritalStatus: String,
                        monthlyIcome: String, whatsAppAccount: String) {

        val personalInfo = BaseUserInfo()
        personalInfo.thyahowyzpcafg = childrenNumber
        personalInfo.rqfgp = email
        personalInfo.datocztfsqijqqqhd = employmentStatues
        personalInfo.fjvrxykbykpdy = lastEducation
        personalInfo.dxvqjligurqrp = maritalStatus
        personalInfo.ttemuejkkefd = monthlyIcome
        personalInfo.dkxbqzeriltnfoc = whatsAppAccount
        val call = mHttpApi!!.requestPutBaseUserInfo(getHeaders(mContext), Contants.URL_PRESONAL_INFO, personalInfo)

        dispatchClient?.enqueue(call, CommonResponse::class.java, VerifyBaseUserInfoResponseEvent::class.java)
    }

    fun verifyContactInfo(mContext: Context, body: ArrayList<RequestContactBody>) {

        val call = mHttpApi!!.requestPutContactInfo(getHeaders(mContext), Contants.URL_CONTACT, body)
        dispatchClient?.enqueue(call, CommonResponse::class.java, VerifyContactInfoResponseEvent::class.java)
    }

    fun verifyBankInfo(mContext: Context, bankName: String, cardNo: String,cardNoSecond: String ,bankCode: String,code: String) {
        val body = RequestBankInfoBody()
        body.aosqii = cardNo
        body.fikvylg = bankName
        body.wqumwrph = bankCode
        body.pxcvycbjwxnn = cardNoSecond
        body.kutc = code
        val call = mHttpApi!!.requestPostBankInfo(getHeaders(mContext), Contants.URL_CHANGE_BACK, body)
        dispatchClient?.enqueue(call, CommonResponse::class.java, VerifyBankInfoResponseEvent::class.java)
    }

    fun verifyPanInfo(mContext: Context, panNumber: String, fullName: String, birthday: String, gender: String) {

        val panBody = RequestPanInfoBody()
        panBody.bcikobrx = fullName
        panBody.gscxhjjfy = panNumber
        panBody.ticgsg = gender
        panBody.zcpoxpzb = birthday
        val call = mHttpApi!!.requestPutPanInfo(getHeaders(mContext), Contants.URL_SAVE_CARD_RESULT, panBody)

        dispatchClient?.enqueue(call, CommonResponse::class.java, VerifyPanInfoResponseEvent::class.java)
    }

    fun getOssInfo(mContext: Context, type: Int) {
        val call = mHttpApi!!.requestGet(getHeaders(mContext), Contants.URL_GET_OSS)
        if (type == 1) {
            dispatchClient!!.enqueue(call, OssInfoResponse::class.java, OssInfoResponseEvent::class.java)
        }else if (type == 2) {
            dispatchClient!!.enqueue(call, OssInfoResponse::class.java, OssInfoFaceResponseEvent::class.java)
        }

    }

    fun verifyOcrPan(mContext: Context, url: String) {

        val body = RequestOcrPanBody()
        body.fwadagpin = url
        val call = mHttpApi!!.requestPutOcrPan(getHeaders(mContext), Contants.URL_SAVE_PAN_RESULT, body)
        dispatchClient?.enqueue(call, OcrPanResponse::class.java, OcrPanResponseEvent::class.java)
    }

    fun getUserCredit(mContext: Context) {

        val call = mHttpApi!!.requestGet(getHeaders(mContext), Contants.URL_USER_CREDIT)
        dispatchClient?.enqueue(call, CommonResponse::class.java, UserCreditResponseEvent::class.java)
    }

    fun verifyOcrFace(mContext: Context, url: String) {

        val body = RequestOcrPanBody()
        body.fwadagpin = url
        val call = mHttpApi!!.requestPutOcrPan(getHeaders(mContext), Contants.URL_UPLOAD_FACE_IMAGE, body)
        dispatchClient?.enqueue(call, CommonResponse::class.java, VerifyOcrFaceResponseEvent::class.java)
    }

    fun verifyOcrFaceNumber(mContext: Context) {

        val body = RequestOcrPanNumberBody()
        body.awja = "FACE"
        val call = mHttpApi!!.requestPostOcrPanNumber(getHeaders(mContext), Contants.URL_OCR_NUMBER, body)
        dispatchClient?.enqueue(call, CommonIntResponse::class.java, OcrFaceNumberResponseEvent::class.java)
    }

    fun verifyOcrPanNumber(mContext: Context) {

        val body = RequestOcrPanNumberBody()
        body.awja = "PAN"
        val call = mHttpApi!!.requestPostOcrPanNumber(getHeaders(mContext), Contants.URL_OCR_NUMBER, body)
        dispatchClient?.enqueue(call, CommonIntResponse::class.java, OcrPanNumberResponseEvent::class.java)
    }




    /*


    fun getPayListBankUrl(mContext: Context,extension : Boolean,loanAppId : String) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap[Contants.extension] = extension
        formMap[Contants.loanAppId] = loanAppId
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_GET_DEPOSIT,formMap)
        dispatchClient!!.enqueue(call, StringResponse::class.java,
            UrlPayListBankResponseEvent::class.java)
    }


   */

}
