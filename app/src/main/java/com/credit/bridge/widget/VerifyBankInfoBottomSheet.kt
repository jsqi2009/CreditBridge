package com.credit.bridge.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.credit.bridge.R
import com.credit.bridge.adapter.CommonListAdapter
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetCommonBinding
import com.credit.bridge.databinding.BottomSheetPemissionBinding
import com.credit.bridge.databinding.BottomSheetVerifyBankBinding
import com.credit.bridge.databinding.BottomSheetVerifyBankInfoBinding
import com.credit.bridge.inter.OnClickListener
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.CommonBean
import com.credit.bridge.remote.event.VerifyCodeResponseEvent
import com.credit.bridge.remote.response.CommonResponse
import com.credit.bridge.util.ToastUtil
import com.google.android.gms.common.internal.service.Common
import com.google.gson.JsonObject
import com.squareup.otto.Subscribe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

class VerifyBankInfoBottomSheet(
    val mContext: Context,
    var title: String,
    var ifsc: String,
    var account: String,
    var onConfirm: (code: String) -> Unit
) : BaseBottomSheet<BottomSheetVerifyBankInfoBinding>(), View.OnClickListener {

    private var total = 60
    private var verifyCodeTimeRemain = total
    private var verifyVoiceTimeRemain = total

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetVerifyBankInfoBinding {
        return BottomSheetVerifyBankInfoBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showExpanded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bindViews.dismissIv.setOnClickListener(this)
        bindViews.cancelTv.setOnClickListener(this)
        bindViews.confirmTv.setOnClickListener(this)
        bindViews.sendTv.setOnClickListener(this)
        bindViews.verifyVoiceTv.setOnClickListener(this)

        initRes()
    }

    private fun initRes() {
        bindViews.sendTv.paint.isUnderlineText = true
        bindViews.verifyVoiceTv.paint.isUnderlineText = true

        bindViews.accountTv.text = formatValue(account)
        bindViews.ifscTv.text = formatValue(ifsc)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dismissIv -> {
                dismiss()
            }
            R.id.cancelTv -> {
                dismiss()
            }
            R.id.sendTv -> {
                sendCode()
            }
            R.id.verifyVoiceTv -> {
                sendVoiceCode()
            }
            R.id.confirmTv -> {
                if (bindViews.codeEt.toString().isEmpty()) {
                    ToastUtil.showLong(requireContext(), "Enter verification code")
                    return
                }
                onConfirm.invoke(bindViews.codeEt.toString())
                dismiss()
            }
        }
    }

    private fun sendCode() {
        showLoading()
        val call = HttpClient.sendVerifyCode2(mContext, CacheManager.mobile, "CHANGE_BANK", "2")
        call.enqueue(object : retrofit2.Callback<CommonResponse> {
            override fun onResponse(
                call: retrofit2.Call<CommonResponse?>,
                response: Response<CommonResponse?>
            ) {
                hideLoading()
                if (response.body()?.fzpn == 200) {
                    verifyCodeCountdown()
                }
            }

            override fun onFailure(
                call: retrofit2.Call<CommonResponse?>,
                t: Throwable
            ) {
                hideLoading()
            }

        })
    }

    private fun sendVoiceCode() {
        showLoading()
        val call = HttpClient.getVoiceCode2(mContext, CacheManager.mobile, "CHANGE_BANK")
        call.enqueue(object : retrofit2.Callback<CommonResponse> {
            override fun onResponse(
                call: retrofit2.Call<CommonResponse?>,
                response: Response<CommonResponse?>
            ) {
                hideLoading()
                if (response.body()?.fzpn == 200) {
                    verifyVoiceCountdown()
                }
            }

            override fun onFailure(
                call: retrofit2.Call<CommonResponse?>,
                t: Throwable
            ) {
                hideLoading()
            }

        })
    }

    private fun verifyCodeCountdown() {
        bindViews.sendTv.isClickable = false
        verifyCodeTimeRemain = total
        lifecycleScope.launch {
            try {
                repeat(verifyCodeTimeRemain) {
                    bindViews.sendTv.text = "${verifyCodeTimeRemain} S"
                    if (verifyCodeTimeRemain == 55) {
                        bindViews.verifyVoiceTv.visibility = View.VISIBLE
                    }
                    delay(1000)
                    verifyCodeTimeRemain--
                }
                bindViews.sendTv.text = "Send"
                bindViews.sendTv.isClickable = true
            } catch (e: Exception) {
                bindViews.sendTv.isClickable = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun verifyVoiceCountdown() {
        bindViews.verifyVoiceTv.isClickable = false
        verifyVoiceTimeRemain = total
        lifecycleScope.launch {
            try {
                repeat(verifyVoiceTimeRemain) {
                    bindViews.verifyVoiceTv.text = "Resend ($verifyVoiceTimeRemain) S"
                    delay(1000)
                    verifyVoiceTimeRemain--
                }
                bindViews.verifyVoiceTv.text = getString(R.string.login_verify_voice)
                bindViews.verifyVoiceTv.isClickable = true
            } catch (e: Exception) {
                bindViews.verifyVoiceTv.isClickable = true
            }
        }
    }

    private fun formatValue(text: String): String{
        val newValue = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
        return newValue
    }


}
