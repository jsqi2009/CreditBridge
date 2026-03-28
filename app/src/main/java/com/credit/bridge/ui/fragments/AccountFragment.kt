package com.credit.bridge.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.credit.bridge.R
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.databinding.FragmentAccountBinding
import com.credit.bridge.databinding.FragmentHomeBinding
import com.credit.bridge.databinding.FragmentOrderBinding
import com.credit.bridge.ui.account.AboutUsActivity
import com.credit.bridge.ui.account.PaymentAccountActivity
import com.credit.bridge.ui.account.PrivacyPolicyActivity
import com.credit.bridge.ui.account.SettingActivity
import com.credit.bridge.ui.product.SubmitSuccessActivity

class AccountFragment : BaseFragment<FragmentAccountBinding>(),View.OnClickListener{

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initRes() {
        super.initRes()
        bindViews.paymentAccountLl.setOnClickListener(this)
        bindViews.privacyPolicyLl.setOnClickListener(this)
        bindViews.aboutUsLl.setOnClickListener(this)
        bindViews.settingLl.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.paymentAccountLl -> {
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