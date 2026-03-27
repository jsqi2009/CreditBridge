package com.credit.bridge.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.credit.bridge.R
import com.credit.bridge.adapter.ProductListAdapter
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.databinding.ActivityProductListBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.ui.verify.VerifyInfoActivity
import com.credit.bridge.util.ToastUtil

class ProductListActivity : BaseActivity<ActivityProductListBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityProductListBinding.inflate(layoutInflater)


    private var mAdapter: ProductListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initAdapter()
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backIv.setOnClickListener(this)

        bindViews.amountSlider.apply {
            min = 1000f
            max = 2000f
            step = 100f
            setUnit("₹")
            value = 1200f

            setOnValueChangeListener {
                ToastUtil.showShort(this@ProductListActivity, it.toString())
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.titleTv -> {
                startActivity(Intent(this, ConfirmProductActivity::class.java))
            }
        }
    }

    private fun initAdapter() {

        bindViews.productRv.layoutManager = LinearLayoutManager(this)
        mAdapter = ProductListAdapter(this, items = arrayListOf(OrderInfo(),OrderInfo(),OrderInfo(),OrderInfo(),OrderInfo(),OrderInfo()),object : OnItemClickListener{
            override fun onItemClick(info: OrderInfo) {
            }

        })
        bindViews.productRv.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

}