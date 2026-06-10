package com.credit.bridge.ui.account

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.credit.bridge.R
import com.credit.bridge.adapter.OrderListAdapter
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.ActivityConfirmProductBinding
import com.credit.bridge.databinding.ActivityPaymentAccountBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.inter.OnOrderItemClickListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.remote.event.FetchBankInfoResponseEvent
import com.credit.bridge.remote.event.OrderLinkBankResponseEvent
import com.credit.bridge.remote.event.OrderListResponseEvent
import com.credit.bridge.remote.event.PaymentLinkResponseEvent
import com.credit.bridge.remote.event.ViewPaymentOptionsEvent
import com.credit.bridge.ui.order.EditCardActivity
import com.credit.bridge.ui.order.OrderDetailsActivity
import com.credit.bridge.ui.product.SubmitSuccessActivity
import com.credit.bridge.util.NumberUtils
import com.credit.bridge.util.OrderStatus
import com.credit.bridge.util.ToastUtil
import com.google.gson.Gson
import com.squareup.otto.Subscribe

class PaymentAccountActivity : BaseActivity<ActivityPaymentAccountBinding>(), View.OnClickListener ,OnOrderItemClickListener{
    override fun getBinding() = ActivityPaymentAccountBinding.inflate(layoutInflater)

    private var orderList: ArrayList<OrderInfo> =  ArrayList()
    private var mAdapter: OrderListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    override fun onResume() {
        super.onResume()
        fetchCardInfo()
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.text = "Payment account"
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backLl.setOnClickListener(this)
        bindViews.editIv.setOnClickListener(this)

        initListAdapter()
    }

    private fun fetchCardInfo() {
        showLoading()
        HttpClient.fetchBankInfo(this)
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onFetchBankInfoResponseEvent(event: FetchBankInfoResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            event.model?.mtaw?.let {
                Log.e("Bank Info", Gson().toJson(it))
                if (!it.rcpqzqrn.isNullOrEmpty()) {
                    bindViews.ifscTv.text = getString(R.string.product_ifsc) + " " +  NumberUtils.formatNumber(it.rcpqzqrn,3,2)
                }
                if (!it.qmtddx.isNullOrEmpty()) {
                    bindViews.accountTv.text = getString(R.string.product_account) + " " +  NumberUtils.formatNumber(it.qmtddx,3,2)
                }

                fetchOrderLinkBank(it.qmtddx.toString())
            }
        }else{
            ToastUtil.showLong(this,event.retMsg)
        }
    }

    private fun fetchOrderLinkBank(account: String) {
        showLoading()
        HttpClient.getOrderLinkBank(this,account)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun onOrderLinkBankResponseEvent(event: OrderLinkBankResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            orderList = event.model?.mtaw ?: ArrayList()
            mAdapter?.setData(orderList!!)
            mAdapter?.notifyDataSetChanged()

            if (mAdapter?.getData()?.isEmpty() == true) {
                bindViews.defaultView.visibility = View.VISIBLE
            } else {
                bindViews.defaultView.visibility = View.GONE
            }
        } else {
            ToastUtil.showLong(this,event.retMsg)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> {
                finish()
            }
            R.id.editIv -> {
                startActivity(Intent(this, EditCardActivity::class.java))
            }
        }
    }

    private fun initListAdapter() {

        bindViews.orderRv.layoutManager = LinearLayoutManager(this)
        mAdapter = OrderListAdapter(this, items = orderList,this)
        bindViews.orderRv.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }


    override fun onOrderItemClick(info: OrderInfo) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("info", info)
        this.startActivity(intent)
    }

    override fun onViewPaymentOptionsClick(info: OrderInfo) {
        paymentOptionsAction(info)
    }

    override fun onPaymentClick(info: OrderInfo) {
        showLoading()
        HttpClient.getPaymentLink(this, false,info.kcyrbnp.toString(), 1)
    }

    @Subscribe
    fun onPaymentLinkResponseEvent(event: PaymentLinkResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            try {
                val link = event.model?.mtaw ?: return
                val uri = link.toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: Exception) {
            }
        }else{
            ToastUtil.showLong(this,event.networkError.toString())
        }
    }

    @Subscribe
    fun onViewPaymentOptionsEvent(event: ViewPaymentOptionsEvent) {
        if (event.info != null) {
            paymentOptionsAction(event.info!!)
        }
    }

    private fun paymentOptionsAction(info: OrderInfo) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("info", info)
        intent.putExtra("isExtend", true)
        startActivity(intent)
    }
}