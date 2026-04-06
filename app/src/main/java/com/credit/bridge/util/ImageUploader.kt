package com.credit.bridge.util

import android.util.Log
import com.credit.bridge.remote.bean.OSSUploadInfo
import com.credit.bridge.remote.bean.OssInfo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.text.endsWith

object ImageUploader {

    private const val TIMEOUT_MS = 30 * 1000
    private var TAG = "ImageUploader"
    fun uploadImage(
        imagePath: String,
        ossInfo: OssInfo,
        callback: Callback
    ) {

        val client = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
            .build()

        val file = File(imagePath)
        if (!file.exists()) {
            val call = client.newCall(Request.Builder().url("").build())
            callback.onFailure(call, IOException("File not exist: $imagePath"))
            return
        }
        if (file.length() == 0L) {
            val call = client.newCall(Request.Builder().url("").build())
            callback.onFailure(call, IOException("File is empty: $imagePath"))
            return
        }

        val fileName = file.name
        val key = "${ossInfo.fev}$fileName"

        val mimeType = when {
            imagePath.endsWith(".png", true) -> "image/png"
            imagePath.endsWith(".jpg", true) || imagePath.endsWith(".jpeg", true) -> "image/jpeg"
            imagePath.endsWith(".webp", true) -> "image/webp"
            else -> "application/octet-stream"
        }
        val mediaType = mimeType.toMediaTypeOrNull() ?: "application/octet-stream".toMediaTypeOrNull()


        val builder = try {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", key)
                .addFormDataPart("OSSAccessKeyId", ossInfo.nydkskicvbp)
                .addFormDataPart("policy", ossInfo.rlnjfs)
                .addFormDataPart("signature", ossInfo.jsxuxumqy)
                .addFormDataPart("success_action_status", "200")
                .addFormDataPart(
                    "file",
                    fileName,
                    file.asRequestBody(mediaType)
                )
        } catch (e: Exception){
            val emptyCall = client.newCall(Request.Builder().url("").build())
            callback.onFailure(emptyCall, IOException("builder build error：${e.message}"))
            return
        }

        val request = try {
             Request.Builder()
                .url(ossInfo.rmvk)
                .post(builder.build())
                .build()
        } catch (e: Exception) {
            val call = client.newCall(Request.Builder().url("").build())
            callback.onFailure(call, IOException("error：${e.message}"))
            return
        }

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseCode = response.code
                val responseBody = response.body?.string() ?: "no body"

                if (responseCode == 200) {
                    callback.onResponse(call, response)
                } else {
                    callback.onFailure(call, IOException("upload fail: $responseCode, body: $responseBody"))
                }
                response.close()
            }
        })
    }

}