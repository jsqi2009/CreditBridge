package com.credit.bridge.ui.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityConfirmProductBinding
import com.credit.bridge.databinding.ActivityProductListBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.ProductInfo
import com.credit.bridge.remote.body.RequestSubmitOrderBody
import com.credit.bridge.remote.event.FetchBankInfoResponseEvent
import com.credit.bridge.remote.event.SubmitOrderResponseEvent
import com.credit.bridge.util.NumberUtils
import com.credit.bridge.util.ToastUtil
import com.google.gson.Gson
import com.squareup.otto.Subscribe

class ConfirmProductActivity : BaseActivity<ActivityConfirmProductBinding>(), View.OnClickListener  {

    override fun getBinding() = ActivityConfirmProductBinding.inflate(layoutInflater)

    var productIdList: ArrayList<Int> = ArrayList<Int>()
    var amountList: ArrayList<Int> = ArrayList<Int>()
    private var totalAmount: Any = 0
    private var totalFee: Double = 0.0
    private var productInfo: ProductInfo? = null
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

        try {
            bindViews.titleLayout.titleTv.text = "Usage details"

            productIdList = intent.getIntegerArrayListExtra("productIdList")!!
            amountList = intent.getIntegerArrayListExtra("productAmountList")!!
            totalAmount = intent.getIntExtra("amount", 0)
            totalFee = intent.getDoubleExtra("fee", 0.0)
            productInfo = intent.getSerializableExtra("productInfo") as ProductInfo

            bindViews.amountPaidTv.text =  getString(R.string.money_symbol) + " "+ totalAmount.toString()
            bindViews.daysTv.text = getString(R.string.product_up_to) + " " + productInfo?.tkjgeq.toString() +
                    " " + productInfo?.wnvelqecci
            bindViews.rateTv.text = productInfo?.rpstrzkyuypj.toString() + "%"
            bindViews.serviceChargeTv.text = getString(R.string.money_symbol) + " " + totalFee.toString()
        } catch (e: Exception) {

        }

        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backIv.setOnClickListener(this)
        bindViews.confirmUseTv.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.confirmUseTv -> {
                submitOrder()
            }
        }
    }

    private fun fetchCardInfo() {
        showLoading()
        HttpClient.fetchBankInfo(this)
    }

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

                //HttpClient.getOrderLinkBank(this,it.twnkgc)
            }
        }else{
            ToastUtil.showLong(this,event.retMsg)
        }
    }

    private fun submitOrder() {

        val bodyList: ArrayList<RequestSubmitOrderBody> = ArrayList()

        if (productIdList.isNotEmpty() && amountList.isNotEmpty() && productIdList.size == amountList.size) {
            productIdList.forEachIndexed { index, productId ->

                val item = RequestSubmitOrderBody()
                item.ypwqmbzol = productId
                item.bbxpqv = amountList[index]

                bodyList.add(item)
            }
        }

        showLoading()
        HttpClient.submitOrder(this, bodyList, "2")

        /*val eventValue =  HashMap<String, Any>()
        eventValue[ConstConfig.POINT_LOAN_SUBMIT] = ""
        AppsFlyerLib.getInstance().logEvent(mContext, ConstConfig.POINT_LOAN_SUBMIT, eventValue)
        PointUploadUtils.uploadEvent(mContext as AppCompatActivity,ConstConfig.POINT_ACTION_TYPE_CLICK,ConstConfig.POINT_LOAN_SUBMIT)*/
    }

    @Subscribe
    fun onSubmitOrderResponseEvent(event: SubmitOrderResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            if (event.model?.flag == "2") {
                val intent = Intent(this, SubmitSuccessActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }




}