package com.credit.bridge.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.credit.bridge.R
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetPemissionBinding
import com.credit.bridge.util.ToastUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class PermissionBottomSheet(
    val mContext: Context,
    val privacyUrl: String,
    val onRefuseListener: () -> Unit,
    val onAgreeListener: () -> Unit
) : BaseBottomSheet<BottomSheetPemissionBinding>(), View.OnClickListener {

    private var contentLoaded = false

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetPemissionBinding {
        return BottomSheetPemissionBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showExpanded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews.dismissIv.setOnClickListener(this)
        bindViews.refuseTv.setOnClickListener(this)
        bindViews.agreeTv.setOnClickListener(this)
        setupWebView()
    }

    override fun onStart() {
        super.onStart()
        expandBottomSheet()
        bindViews.contentWebView.post {
            if (!contentLoaded) {
                loadContentUrl()
            }
        }
    }

    private fun expandBottomSheet() {
        forceExpandState()
        val bottomSheetDialog = dialog as? BottomSheetDialog ?: return
        val sheet = bottomSheetDialog.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return
        // Same as text version: match_parent sheet so middle area (weight=1) gets real height.
        sheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bindViews.root.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheetDialog.behavior.apply {
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        bindViews.contentWebView.apply {
            setBackgroundColor(Color.WHITE)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.builtInZoomControls = false
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = false
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
                        ToastUtil.showLong(mContext, "Failed to load content")
                    }
                }
            }
        }
    }

    private fun loadContentUrl() {
        val url = privacyUrl.trim()
        if (url.isEmpty()) {
            ToastUtil.showLong(mContext, "Content unavailable")
            return
        }
        contentLoaded = true
        Log.d(TAG, "loadContentUrl: $url")
        showLoading()
        bindViews.contentWebView.loadUrl(url)
    }

    override fun onDestroyView() {
        val webView = bindViews.contentWebView
        (webView.parent as? ViewGroup)?.removeView(webView)
        webView.stopLoading()
        webView.destroy()
        hideLoading()
        super.onDestroyView()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dismissIv -> {
                dismiss()
                onRefuseListener.invoke()
            }
            R.id.refuseTv -> {
                dismiss()
                onRefuseListener.invoke()
            }
            R.id.agreeTv -> {
                dismiss()
                onAgreeListener.invoke()
            }
        }
    }

    companion object {
        private const val TAG = "PermissionBottomSheet"
    }
}
