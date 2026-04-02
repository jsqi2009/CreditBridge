package com.credit.bridge.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.R
import com.credit.bridge.adapter.ProductListAdapter
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.databinding.ActivityProductListBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.inter.OnProductItemClickListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.AllProductInfo
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.remote.bean.ProductInfo
import com.credit.bridge.remote.body.RequestSubmitOrderBody
import com.credit.bridge.remote.event.AllProductListResponseEvent
import com.credit.bridge.remote.event.SubmitOrderResponseEvent
import com.credit.bridge.ui.verify.VerifyInfoActivity
import com.credit.bridge.util.AppUtil
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe
import kotlin.compareTo

class ProductListActivity : BaseActivity<ActivityProductListBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityProductListBinding.inflate(layoutInflater)


    private var mAdapter: ProductListAdapter? = null
    var productList: ArrayList<ProductInfo> = ArrayList()
    var allList: ArrayList<AllProductInfo>? = ArrayList()
    private var totalLimit = 0
    private var targetValue: Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        getAllProductList()
    }

    override fun initRes() {
        super.initRes()

        totalLimit = intent.getIntExtra("amountLimit", 0)

        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backIv.setOnClickListener(this)
        bindViews.viewDetailsTv.setOnClickListener(this)
        bindViews.continueTv.setOnClickListener(this)

        bindViews.amountSlider.apply {
            min = 1000F
            max = totalLimit.toFloat()
            step = 100F
            setUnit("₹")
            value = totalLimit.toFloat()

            setOnValueChangeListener {
                ToastUtil.showShort(this@ProductListActivity, it.toString())
                targetValue = it
                mAdapter?.setData(arrayListOf())
                filterProductList(targetValue.toInt())
            }
        }
    }

    private fun getAllProductList() {
        showLoading()
        HttpClient.getAllProductList(this)
    }

    @Subscribe
    fun onAllProductListResponseEvent(event: AllProductListResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            val allProduct: ArrayList<AllProductInfo>? = event.model?.blvb
            allList = allProduct
            allList?.sortByDescending { it.dsmcbogvwzsgpvkszc }
            if (allProduct != null && allProduct.isNotEmpty()) {
                productList = allProduct[0].vlwauwwzbwoagx
                mAdapter?.setData(productList)
            }
            filterProductList(targetValue.toInt())
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.viewDetailsTv -> {
                viewDetails()
            }
            R.id.continueTv -> {
                submitOrder()
            }
        }
    }

    private fun initAdapter() {

        bindViews.productRv.layoutManager = LinearLayoutManager(this)
        mAdapter = ProductListAdapter(this, items = productList,object : OnProductItemClickListener{
            override fun onItemClick(info: ProductInfo) {
            }

        })
        bindViews.productRv.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

    private fun filterProductList(targetValue: Int) {
        for (i in 0 until allList!!.size) {
            if (allList!![i].dsmcbogvwzsgpvkszc <= targetValue) {
                productList = allList!![i].vlwauwwzbwoagx
                mAdapter?.setData(productList)
                break
            }
        }

    }

    private fun viewDetails() {
        mAdapter?.getData()?.size?.let { it1 ->
            if (it1 > 0) {
                val intent = Intent(this, ConfirmProductActivity::class.java)
                intent.putExtra("productInfo", productList[0])
                intent.putIntegerArrayListExtra("productIdList", AppUtil.formatProductId(productList))
                intent.putIntegerArrayListExtra("productAmountList", AppUtil.formatAmount(productList))
                intent.putExtra("amount", AppUtil.formatTotalAmount(productList))
                intent.putExtra("fee",AppUtil.formatTotalFee(productList))
                startActivity(intent)
            }else{
                ToastUtil.showLong(this@ProductListActivity,"Please select a suitable limit.")
            }
        }
    }

    private fun submitOrder() {

        val bodyList: ArrayList<RequestSubmitOrderBody> = ArrayList()
        productList.forEach {
            val item: RequestSubmitOrderBody = RequestSubmitOrderBody()
            item.ypwqmbzol = it.ifbivuyws
            item.bbxpqv = it.auaxapvecxrhb
            bodyList.add(item)
        }
        showLoading()
        HttpClient.submitOrder(this, bodyList, "1")

        /*val eventValue =  HashMap<String, Any>()
        eventValue[ConstConfig.POINT_LOAN_SUBMIT] = ""
        AppsFlyerLib.getInstance().logEvent(mContext, ConstConfig.POINT_LOAN_SUBMIT, eventValue)
        PointUploadUtils.uploadEvent(mContext as AppCompatActivity,ConstConfig.POINT_ACTION_TYPE_CLICK,ConstConfig.POINT_LOAN_SUBMIT)*/
    }

    @Subscribe
    fun onSubmitOrderResponseEvent(event: SubmitOrderResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            if (event.model?.flag == "1") {
                val intent = Intent(this, SubmitSuccessActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }

}