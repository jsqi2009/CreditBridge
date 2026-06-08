package com.credit.bridge.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import com.credit.bridge.R
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetVerifyBankInfoBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.response.CommonResponse
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.CommonCountdown
import retrofit2.Response

class VerifyBankInfoBottomSheet(
    val mContext: Context,
    var title: String,
    var ifsc: String,
    var account: String,
    var onConfirm: (code: String) -> Unit
) : BaseBottomSheet<BottomSheetVerifyBankInfoBinding>(), View.OnClickListener {

    private val total = 60
    private var codeCountdown: CommonCountdown? = null
    private var voiceCountdown: CommonCountdown? = null

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
        initCountdowns()
        restoreCountdownState(savedInstanceState)
        enableKeyboardScroll()
    }

    override fun onResume() {
        super.onResume()
        codeCountdown?.refresh()
        voiceCountdown?.refresh()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        codeCountdown?.saveState(outState, KEY_CODE_COUNTDOWN_END)
        voiceCountdown?.saveState(outState, KEY_VOICE_COUNTDOWN_END)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard()
        super.onDismiss(dialog)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        dialog?.window?.decorView?.windowToken?.let { imm.hideSoftInputFromWindow(it, 0) }
        activity?.let { host ->
            host.currentFocus?.clearFocus()
            imm.hideSoftInputFromWindow(host.window.decorView.windowToken, 0)
        }
    }

    private fun enableKeyboardScroll() {
        bindViews.codeEt.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                scrollFieldIntoView(v)
            }
        }
    }

    private fun scrollFieldIntoView(focused: View) {
        bindViews.verifyBankScrollView.postDelayed({
            val scrollView = bindViews.verifyBankScrollView
            scrollView.post {
                val content = scrollView.getChildAt(0) ?: return@post
                val rect = Rect()
                focused.getDrawingRect(rect)
                scrollView.offsetDescendantRectToMyCoords(focused, rect)
                rect.bottom += resources.getDimensionPixelSize(R.dimen.margin_20)
                scrollView.requestChildRectangleOnScreen(content, rect, true)
            }
        }, 80)
    }

    private fun initRes() {
        bindViews.sendTv.paint.isUnderlineText = true
        bindViews.verifyVoiceTv.paint.isUnderlineText = true

        bindViews.accountTv.text = formatValue(account)
        bindViews.ifscTv.text = formatValue(ifsc)

    }

    private fun initCountdowns() {
        codeCountdown = CommonCountdown(
            viewLifecycleOwner.lifecycleScope,
            onTick = { remain ->
                if (isAdded) {
                    bindViews.sendTv.text = "$remain S"
                    if (remain <= 55) {
                        bindViews.verifyVoiceTv.visibility = View.VISIBLE
                    }
                }
            },
            onFinish = {
                if (isAdded) {
                    bindViews.sendTv.text = "Send"
                    bindViews.sendTv.isClickable = true
                }
            }
        )
        voiceCountdown = CommonCountdown(
            viewLifecycleOwner.lifecycleScope,
            onTick = { remain ->
                if (!isAdded) return@CommonCountdown
                bindViews.verifyVoiceTv.text = "Resend ($remain) S"
            },
            onFinish = {
                if (!isAdded) return@CommonCountdown
                bindViews.verifyVoiceTv.text = getString(R.string.login_verify_voice)
                bindViews.verifyVoiceTv.isClickable = true
            }
        )
    }

    private fun restoreCountdownState(savedInstanceState: Bundle?) {
        if (codeCountdown?.restoreState(savedInstanceState, KEY_CODE_COUNTDOWN_END) == true) {
            bindViews.sendTv.isClickable = false
        }
        if (voiceCountdown?.restoreState(savedInstanceState, KEY_VOICE_COUNTDOWN_END) == true) {
            bindViews.verifyVoiceTv.isClickable = false
        }
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
                    ToastUtil.showLong(requireContext(), "Verification code is required")
                    return
                }
                onConfirm.invoke(bindViews.codeEt.text.toString())
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
                try {
                    hideLoading()
                    if (response.body()?.fzpn == 200) {
                        verifyCodeCountdown()
                    } else {
                        ToastUtil.showLong(requireContext(), response.body()?.dvusonb)
                    }
                } catch (e: Exception) {
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
                try {
                    hideLoading()
                    if (response.body()?.fzpn == 200) {
                        verifyVoiceCountdown()
                    }else{
                        ToastUtil.showLong(requireContext(), response.body()?.dvusonb)

                    }
                } catch (e: Exception) {
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
        if (!isAdded) return
        bindViews.sendTv.isClickable = false
        codeCountdown?.start(total)
    }

    @SuppressLint("SetTextI18n")
    private fun verifyVoiceCountdown() {
        if (!isAdded) return
        bindViews.verifyVoiceTv.isClickable = false
        voiceCountdown?.start(total)
    }

    private fun formatValue(text: String): String{
        val newValue = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
        return newValue
    }

    companion object {
        private const val KEY_CODE_COUNTDOWN_END = "verify_bank_info_code_countdown_end"
        private const val KEY_VOICE_COUNTDOWN_END = "verify_bank_info_voice_countdown_end"
    }
}
