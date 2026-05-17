package com.credit.bridge.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.credit.bridge.R
import com.credit.bridge.adapter.AppInfoListAdapter
import com.credit.bridge.adapter.OrderListAdapter
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityAboutUsBinding
import com.credit.bridge.databinding.ActivityCustomerServicesBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.AppItemInfo
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.remote.event.AppInfoResponseEvent
import com.credit.bridge.ui.product.SubmitSuccessActivity
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe

class CustomerServicesActivity : BaseActivity<ActivityCustomerServicesBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityCustomerServicesBinding.inflate(layoutInflater)

    private var itemList: ArrayList<AppItemInfo> =  ArrayList()
    private var mAdapter: AppInfoListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        getAppInfo()
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.text = "Customer Services"

        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backLl.setOnClickListener(this)
    }


    private fun getAppInfo() {
        showLoading()
        HttpClient.getAppInfo(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> {
                finish()
            }
            R.id.titleTv -> {
                startActivity(Intent(this, SubmitSuccessActivity::class.java))
            }
        }
    }

    @Subscribe
    fun onAppInfoResponseEvent(event: AppInfoResponseEvent) {
        try {
            hideLoading()
            if (event.isSuccess) {
                itemList = event.model?.mtaw ?: ArrayList()
                mAdapter?.setData(itemList)
            } else {
                ToastUtil.showLong(this,event.retMsg)
            }
        } catch (e: Exception) {
        }
    }

    private fun initAdapter() {

        bindViews.recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = AppInfoListAdapter(this, items = itemList)
        bindViews.recyclerView.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }
}