package com.credit.bridge.widget

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.credit.bridge.R
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetVerifyBankBinding
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.CommonCountdown

class VerifyBankBottomSheet (val mContext: Context, val confirmListener: OnConfirmListener, var name : String, var cardNo : String)
    : BaseBottomSheet<BottomSheetVerifyBankBinding>(), View.OnClickListener {
    private val totalTime = 60
    private var smsCountdown: CommonCountdown? = null

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
        initCountdown()
    }

    override fun onResume() {
        super.onResume()
        smsCountdown?.refresh()
    }

    private fun initCountdown() {
        smsCountdown = CommonCountdown(
            viewLifecycleOwner.lifecycleScope,
            onTick = { remain ->
                if (!isAdded) return@CommonCountdown
                bindViews.tvGetOpt.text = "$remain S"
            },
            onFinish = {
                if (!isAdded) return@CommonCountdown
                bindViews.tvGetOpt.text = "Get"
                bindViews.tvGetOpt.isClickable = true
            }
        )
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
        smsCountdown?.start(totalTime)
    }
}