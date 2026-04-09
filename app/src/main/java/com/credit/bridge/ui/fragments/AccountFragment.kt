package com.credit.bridge.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.credit.bridge.R
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.databinding.FragmentAccountBinding
import com.credit.bridge.databinding.FragmentHomeBinding
import com.credit.bridge.databinding.FragmentOrderBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.event.FetchBankInfoResponseEvent
import com.credit.bridge.ui.account.AboutUsActivity
import com.credit.bridge.ui.account.PaymentAccountActivity
import com.credit.bridge.ui.account.PrivacyPolicyActivity
import com.credit.bridge.ui.account.SettingActivity
import com.credit.bridge.ui.product.SubmitSuccessActivity
import com.credit.bridge.util.NumberUtils
import com.credit.bridge.util.ToastUtil
import com.google.gson.Gson
import com.squareup.otto.Subscribe

class AccountFragment : BaseFragment<FragmentAccountBinding>(),View.OnClickListener{

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            fetchCardInfo()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initRes() {
        super.initRes()
        bindViews.paymentAccountLl.setOnClickListener(this)
        bindViews.privacyPolicyLl.setOnClickListener(this)
        bindViews.aboutUsLl.setOnClickListener(this)
        bindViews.settingLl.setOnClickListener(this)
        bindViews.paymentAccountIv.setOnClickListener(this)

        if (CacheManager.isAuth) {
            bindViews.loginTv.visibility = View.GONE
            bindViews.signInTv.text = "+91" + NumberUtils.formatNumber(CacheManager.mobile, 3, 2)
        } else {
            bindViews.loginTv.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.paymentAccountLl -> {
                startActivity(Intent(requireActivity(), PaymentAccountActivity::class.java))
            }
            R.id.paymentAccountIv -> {
                startActivity(Intent(requireActivity(), PaymentAccountActivity::class.java))
            }
            R.id.privacyPolicyLl -> {
                startActivity(Intent(requireActivity(), PrivacyPolicyActivity::class.java))
            }
            R.id.aboutUsLl -> {
                startActivity(Intent(requireActivity(), AboutUsActivity::class.java))
            }
            R.id.settingLl -> {
                startActivity(Intent(requireActivity(), SettingActivity::class.java))
            }
        }
    }

    private fun fetchCardInfo() {
        showLoading()
        HttpClient.fetchBankInfo(requireActivity())
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onFetchBankInfoResponseEvent(event: FetchBankInfoResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            event.model?.mtaw?.let {
                if (!it.rcpqzqrn.isNullOrEmpty()) {
                    bindViews.ifscTv.text = getString(R.string.product_ifsc) + " " +  NumberUtils.formatNumber(it.rcpqzqrn,3,2)
                }
                if (!it.qmtddx.isNullOrEmpty()) {
                    bindViews.accountTv.text = getString(R.string.product_account) + " " +  NumberUtils.formatNumber(it.qmtddx,3,2)
                }
            }
        }else{
            ToastUtil.showLong(requireActivity(),event.retMsg)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AccountFragment {
            val args = Bundle()
            val fragment = AccountFragment()
            fragment.arguments = args
            return fragment
        }
    }
}