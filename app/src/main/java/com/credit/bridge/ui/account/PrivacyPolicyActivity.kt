package com.credit.bridge.ui.account

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityPrivacyPolicyBinding
import com.credit.bridge.databinding.ActivityProductListBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.event.PolicyLinkResponseEvent
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe

class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding>(), View.OnClickListener  {

    override fun getBinding() = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.text = "Privacy Policy"

        bindViews.titleLayout.backLl.setOnClickListener(this)

        getPolicyLink()
    }

    private fun getPolicyLink(){
        showLoading()
        HttpClient.getPolicyLink(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> {
                finish()
            }
        }
    }

    @Subscribe
    fun onPolicyLinkResponseEvent(event: PolicyLinkResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            event.model?.mtaw?.let {
                bindViews.webView.loadUrl(it)
            }
        }else{
            ToastUtil.showLong(this,event.retMsg)
        }
    }


}