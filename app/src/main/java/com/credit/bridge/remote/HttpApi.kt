package com.credit.bridge.remote

import RequestOrderUpdateBody
import com.credit.bridge.remote.bean.BaseUserInfo
import com.credit.bridge.remote.body.RequestBankInfoBody
import com.credit.bridge.remote.body.RequestContactBody
import com.credit.bridge.remote.body.RequestFeedbackBody
import com.credit.bridge.remote.body.RequestHomeInfoBody
import com.credit.bridge.remote.body.RequestOcrPanBody
import com.credit.bridge.remote.body.RequestOrderDetailsBody
import com.credit.bridge.remote.body.RequestOrderLinkBankBody
import com.credit.bridge.remote.body.RequestOrderListBody
import com.credit.bridge.remote.body.RequestPanInfoBody
import com.credit.bridge.remote.body.RequestSubmitOrderBody
import com.credit.bridge.remote.body.RequestVerifyCodeBody
import com.credit.bridge.remote.body.RequestVoiceCodeBody
import com.credit.bridge.remote.event.RequestZipDataBody
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap
import retrofit2.http.Url


interface HttpApi {

    @POST
    fun requestPostVerifyCode(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestVerifyCodeBody): Call<JsonObject>

    @POST
    fun requestPostVoiceCode(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestVoiceCodeBody): Call<JsonObject>

    @POST
    fun requestPostHomeInfo(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestHomeInfoBody): Call<JsonObject>

    @POST
    fun requestPostZipData(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String,@Body body: RequestZipDataBody): Call<JsonObject>

    @POST
    fun requestPostOrderList(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOrderListBody): Call<JsonObject>

    @POST
    fun requestPostOrderDetails(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOrderDetailsBody): Call<JsonObject>

    @POST
    fun requestPostOrderUpdate(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOrderUpdateBody): Call<JsonObject>

    @POST
    fun requestPost(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    @POST
    fun requestPostSubmitOrder(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String,
                              @Body body: ArrayList<RequestSubmitOrderBody>): Call<JsonObject>

    @POST
    fun requestPostOrderLinkBank(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOrderLinkBankBody): Call<JsonObject>

    @POST
    fun requestPostFeedback(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestFeedbackBody): Call<JsonObject>

    @GET
    fun requestGet(@HeaderMap map: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    @GET
    fun requestGetAuth(@HeaderMap map: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    @GET
    fun requestGetQueryMap(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @QueryMap map: MutableMap<String, Any>): Call<JsonObject>


    @PUT
    fun requestPutBaseUserInfo(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: BaseUserInfo): Call<JsonObject>


    @PUT
    fun requestPutContactInfo(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: ArrayList<RequestContactBody>): Call<JsonObject>

    @POST
    fun requestPostBankInfo(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestBankInfoBody): Call<JsonObject>

    @PUT
    fun requestPutPanInfo(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestPanInfoBody): Call<JsonObject>

    @PUT
    fun requestPutOcrPan(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOcrPanBody): Call<JsonObject>









    /*@POST
    fun requestPostSms(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestSmsBody): Call<JsonObject>







*/
    @GET
    fun requestGetNoAuth(@Url url: String): Call<JsonObject>



    @GET
    fun requestGetAuth1(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @QueryMap map: MutableMap<String, Any>): Call<JsonObject>

    @POST
    @FormUrlEncoded
    fun requestPost(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @FieldMap map: MutableMap<String, Any>): Call<JsonObject>

    @POST
    @FormUrlEncoded
    fun requestPost1(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @FieldMap map: MutableMap<String, Any>): Call<JsonObject>




    @PUT
    fun requestPut(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    /*
    @POST
    fun ocrPanNumber(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOcrNumberBody): Call<JsonObject>





*/
    @HTTP(method = "DELETE", path = "{key}", hasBody = false)
    fun requestDelete(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    @GET
    fun requestGetAuth(@HeaderMap map: MutableMap<String, String>, @Url url: String, @Path("key1") key1: String): Call<JsonObject>



    @GET
    fun requestAuth3(@HeaderMap map: MutableMap<String, String>, @Url url: String?): Call<JsonObject>

    @DELETE
    fun requestDelete(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @QueryMap map: MutableMap<String, Any>): Call<JsonObject>

    @DELETE
    fun requestDeleteId(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String): Call<JsonObject>

/*


*/


    /*


*/

}
