package com.credit.bridge.widget

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.coroutineScope
import com.credit.bridge.R
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetVerifyBankBinding
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.util.ToastUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyBankBottomSheet (val mContext: Context, val confirmListener: OnConfirmListener, var name : String, var cardNo : String)
    : BaseBottomSheet<BottomSheetVerifyBankBinding>(), View.OnClickListener {
    private val totalTime = 60
    private var smsTime = totalTime
    private var voiceTime = totalTime

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetVerifyBankBinding {
        return BottomSheetVerifyBankBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showExpanded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews.ivDismiss.setOnClickListener(this)
        bindViews.tvConfirm.setOnClickListener(this)
        bindViews.tvGetOpt.setOnClickListener(this)
        bindViews.etName.text = name
        bindViews.etNumber.text = cardNo
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_dismiss -> {
                dismiss()
            }
            R.id.tv_confirm -> {
                if (bindViews.etCode.text.toString().isEmpty()) {
                    ToastUtil.showLong(requireContext(), "Please enter the OTP code")
                    return
                }
                confirmListener.onClick(bindViews.etCode.text.toString())
                dismiss()
            }
            R.id.tv_get_opt -> {
                showLoading()
                //HttpClient.sendSms(requireContext(),CacheConfig.mobile,"CHANGE_BANK")
            }

            else -> {}
        }
    }

    fun showError( msg : String){
        ToastUtil.showLong(requireContext(),msg)
    }
    /**
     * dao jishi 60
     */
    fun startCountdown() {
        bindViews.tvGetOpt.isClickable = false
        smsTime = totalTime
        lifecycle.coroutineScope.launch {
            while (smsTime > 0) {
                bindViews.tvGetOpt.text = "$smsTime S"
                delay(1000)
                smsTime--

            }
            bindViews.tvGetOpt.text = "Get"
            bindViews.tvGetOpt.isClickable = true
        }
    }
}