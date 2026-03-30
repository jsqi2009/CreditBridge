package com.credit.bridge.remote

import com.credit.bridge.remote.body.RequestVerifyCodeBody
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

    /*@POST
    fun requestPostSms(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestSmsBody): Call<JsonObject>

    @POST
    fun postBankInfo(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestBankBody): Call<JsonObject>

    @POST
    fun postOrderBankList(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOrderBankBody): Call<JsonObject>

    @POST
    fun feedback(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestFeedbackBody): Call<JsonObject>

    @POST
    fun postZip(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String,@Body body: RequestZipBody): Call<JsonObject>
*/
    @GET
    fun requestGetNoAuth(@Url url: String): Call<JsonObject>

    @GET
    fun requestGet(@HeaderMap map: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    @GET
    fun requestGetAuth1(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @QueryMap map: MutableMap<String, Any>): Call<JsonObject>

    @POST
    @FormUrlEncoded
    fun requestPost(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @FieldMap map: MutableMap<String, Any>): Call<JsonObject>

    @POST
    @FormUrlEncoded
    fun requestPost1(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @FieldMap map: MutableMap<String, Any>): Call<JsonObject>

    @POST
    fun requestPost1(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    @PUT
    fun requestPut(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    /*@PUT
    fun ocrPan(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOcrBody): Call<JsonObject>
    @POST
    fun ocrPanNumber(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestOcrNumberBody): Call<JsonObject>

    @PUT
    fun requestPutPersonalInfoOne(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: PersonalInfo): Call<JsonObject>

    @PUT
    fun requestPutPan(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: RequestPanBody): Call<JsonObject>

    @PUT
    fun requestPutPersonalInfoTwo(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: ArrayList<RequestContactBody>): Call<JsonObject>
*/
    @HTTP(method = "DELETE", path = "{key}", hasBody = false)
    fun requestDelete(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    @GET
    fun requestGetAuth(@HeaderMap map: MutableMap<String, String>, @Url url: String, @Path("key1") key1: String): Call<JsonObject>

    @GET
    fun requestGetAuth1(@HeaderMap map: MutableMap<String, String>, @Url url: String): Call<JsonObject>

    @GET
    fun requestAuth3(@HeaderMap map: MutableMap<String, String>, @Url url: String?): Call<JsonObject>

    @DELETE
    fun requestDelete(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @QueryMap map: MutableMap<String, Any>): Call<JsonObject>

    @DELETE
    fun requestDeleteId(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String): Call<JsonObject>

/*
    @POST
    fun requestPostVoiceCode(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: VoiceCodeRequestBody): Call<JsonObject>

    @POST
    fun requestPostOrderList(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: OrderRequestBody): Call<JsonObject>

    @POST
    fun requestGetHomeInfo(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: HomeInfoRequestBody): Call<JsonObject>
*/


    /*@POST
    fun requestPostApplyOrder(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String,
                             @Body body: ArrayList<CreateOrderRequestBody>): Call<JsonObject>

    @POST
    fun requestPostOrderDetails(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String,
                              @Body body: OrderDetailsRequestBody): Call<JsonObject>

    @POST
    fun requestExtensionApplyDetail(@HeaderMap headerMap: MutableMap<String, String>, @Url url: String, @Body body: ExtensionApplyDetailRequest): Call<JsonObject>
*/

}
