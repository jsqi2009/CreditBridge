package com.credit.bridge.ui.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityPrivacyPolicyBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.event.PolicyLinkResponseEvent
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe

class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityPrivacyPolicyBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.text = "Privacy Policy"
        bindViews.titleLayout.backLl.setOnClickListener(this)

        setupWebView()
        getPolicyLink()
    }

    private fun getPolicyLink() {
        showLoading()
        HttpClient.getPolicyLink(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        bindViews.webView.apply {
            setBackgroundColor(Color.WHITE)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    hideLoading()
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        hideLoading()
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> finish()
        }
    }

    @Subscribe
    fun onPolicyLinkResponseEvent(event: PolicyLinkResponseEvent) {
        if (event.isSuccess) {
            val url = event.model?.mtaw?.trim().orEmpty()
            if (url.isNotEmpty()) {
                bindViews.webView.loadUrl(url)
            } else {
                hideLoading()
                ToastUtil.showLong(this, event.retMsg)
            }
        } else {
            hideLoading()
            ToastUtil.showLong(this, event.retMsg)
        }
    }

    override fun onDestroy() {
        bindViews.webView.stopLoading()
        super.onDestroy()
    }
}
