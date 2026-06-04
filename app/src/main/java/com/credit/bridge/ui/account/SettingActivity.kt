package com.credit.bridge.ui.account

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityAboutUsBinding
import com.credit.bridge.databinding.ActivitySettingBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.event.LogoutResponseEvent
import com.credit.bridge.ui.login.LoginActivity
import com.credit.bridge.ui.product.SubmitSuccessActivity
import com.credit.bridge.util.DialogUtil
import com.credit.bridge.util.NumberUtils
import com.squareup.otto.Subscribe

class SettingActivity : BaseActivity<ActivitySettingBinding>(), View.OnClickListener {
    override fun getBinding() = ActivitySettingBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    @SuppressLint("SetTextI18n")
    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.text = "Settings"
        bindViews.mobileTv.text = "+91" + NumberUtils.formatNumber(CacheManager.mobile, 3, 2)


        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backLl.setOnClickListener(this)
        bindViews.logoutTv.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> {
                finish()
            }
            R.id.logoutTv -> {
                showLogoutDialog()
            }
        }
    }

    private fun showLogoutDialog() {
        DialogUtil.showLogoutDialog(this, onConfirm = {

        }, onCancel = {
            confirmLogout()
        })
    }

    private fun confirmLogout() {
        showLoading()
        HttpClient.logout(this)
    }

    @Subscribe
    fun onLogoutEvent(event: LogoutResponseEvent) {
        if (isFinishing || isDestroyed) return
        hideLoading()
        if (!event.isSuccess) return

        CacheManager.isAuth = false
        HomeSessionState.clear()
        CacheManager.token = ""
        startActivity(
            Intent(applicationContext, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        )
    }
}