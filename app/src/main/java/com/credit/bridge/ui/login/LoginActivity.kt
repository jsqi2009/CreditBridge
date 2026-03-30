package com.credit.bridge.ui.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.LeadingMarginSpan
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.ActivityLoginBinding
import com.credit.bridge.databinding.ActivitySplashBinding
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.event.LoginResponseEvent
import com.credit.bridge.ui.RootActivity
import com.credit.bridge.ui.account.PrivacyPolicyActivity
import com.credit.bridge.util.DialogUtil
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.widget.CommonBottomSheet
import com.credit.bridge.widget.PermissionBottomSheet
import com.credit.bridge.widget.VerifyBankBottomSheet
import com.squareup.otto.Subscribe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.get

class LoginActivity : BaseActivity<ActivityLoginBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityLoginBinding.inflate(layoutInflater)

    private var workTypeIndex =  -1;
    private var total = 60
    private var verifyCodeTimeRemain = total
    private var verifyVoiceTimeRemain = total
    private var isChecked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.sendTv.paint.isUnderlineText = true
        bindViews.verifyVoiceTv.paint.isUnderlineText = true

        bindViews.loginTv.setOnClickListener(this)
        bindViews.sendTv.setOnClickListener(this)
        bindViews.verifyVoiceTv.setOnClickListener(this)
        bindViews.checkPolicyIv.setOnClickListener(this)

        configPrivacyPolicy()

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.loginTv -> {
                handleLogin()
            }
            R.id.checkPolicyIv -> {
                if (isChecked) {
                    isChecked = false
                    bindViews.checkPolicyIv.setImageResource(R.mipmap.ic_unselected)
                } else {
                    isChecked = true
                    bindViews.checkPolicyIv.setImageResource(R.mipmap.ic_selected)
                }
            }
            R.id.sendTv -> {
                //showPermissionSheet()
                val phone = bindViews.phoneEt.text.toString().trim()
                if (phone.isEmpty()) {
                    ToastUtil.showLong(this,"Mobile number cannot be empty")
                    return
                }
                if(phone.length != 10){
                    ToastUtil.showLong(this,"Please enter a correct phone number")
                    return
                }
                showLoading()
                HttpClient.sendVerifyCode(this, phone, "login")
            }
            R.id.verifyVoiceTv -> {
                DialogUtil.showVoiceVerifyDialog(this, onConfirm = {

                }, onCancel = {

                })
            }
        }
    }

    private fun handleLogin() {
        val phone = bindViews.phoneEt.text.toString().trim()
        val code = bindViews.codeEt.text.toString().trim()

        if (phone.isEmpty()) {
            ToastUtil.showLong(this,"Mobile number cannot be empty")
            return
        }
        if (code.isEmpty()) {
            ToastUtil.showLong(this,"Verification code cannot be empty")
            return
        }
        if(phone.length != 10){
            ToastUtil.showLong(this,"Please enter a correct phone number")
            return
        }
        if(code.length != 4){
            ToastUtil.showLong(this,"Please enter a valid verification code")
            return
        }
        if(!isChecked){
            ToastUtil.showLong(this,"Please agree to the terms and conditions")
            return
        }
        CacheManager.smsCode = code

        showLoading()
        HttpClient.login(this, code)

    }

    @Subscribe
    fun onLoginEvent(event: LoginResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            event.model?.blvb?.let {
                CacheManager.token = it.bupcd
                CacheManager.mobile = bindViews.phoneEt.text.toString()
                CacheManager.isNewCustomer = it.rnouhbrwgawkf
                CacheManager.smsCode = bindViews.codeEt.text.toString()
                CacheManager.isAuth = true

                //upload event
                HttpClient.eventReport(this@LoginActivity,ConstConfig.POINT_REGISTER_COMPLETE,
                    ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_REGISTER_COMPLETE)

                val intent = Intent(this, RootActivity::class.java)
                startActivity(intent)
                finish()
            }
        }else{
            ToastUtil.showLong(this,event.networkError.toString())
        }
    }

    private fun configPrivacyPolicy() {
        val str = SpannableString(getString(R.string.login_privacy))
        str.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, PrivacyPolicyActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.text_FFD29F, null)
                ds.isUnderlineText = true
            }
        }, 30, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        str.setSpan(
            LeadingMarginSpan.Standard(0, 0),
            0,
            str.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE

        )

        bindViews.policyTv.movementMethod = LinkMovementMethod.getInstance()
        bindViews.policyTv.text = str
        bindViews.policyTv.highlightColor = resources.getColor(android.R.color.transparent)
        bindViews.policyTv.includeFontPadding = false
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

    fun showPermissionSheet() {
        val permissionSheet = PermissionBottomSheet(
            this,"Employment Status",VerifyInfoUtil.getWorkTypeList(),
            workTypeIndex, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    ToastUtil.showShort(this@LoginActivity, "Select: $index")
                }
            })
        permissionSheet.show(supportFragmentManager, "workTypeSheet")
    }



}