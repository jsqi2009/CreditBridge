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
import com.credit.bridge.adapter.OrderListAdapter
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.ActivityConfirmProductBinding
import com.credit.bridge.databinding.ActivityPaymentAccountBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.inter.OnOrderItemClickListener
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.ui.order.OrderDetailsActivity
import com.credit.bridge.ui.product.SubmitSuccessActivity
import com.credit.bridge.util.OrderStatus

class PaymentAccountActivity : BaseActivity<ActivityPaymentAccountBinding>(), View.OnClickListener ,OnOrderItemClickListener{
    override fun getBinding() = ActivityPaymentAccountBinding.inflate(layoutInflater)

    private var orderList: ArrayList<OrderInfo> =  ArrayList()
    private var mAdapter: OrderListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    override fun initRes() {
        super.initRes()
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backIv.setOnClickListener(this)

        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_PRE_REVIEW))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_ISSUING))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_OVERDUE))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_CURRENT))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_PAID_OFF))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_REJECTED))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_CLOSED))
        initListAdapter()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.titleTv -> {
                startActivity(Intent(this, SubmitSuccessActivity::class.java))
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
        if (info.xjywdrtdxzt == ConstConfig.ORDER_STATUS_ISSUE_FAILED) {
            //val intent = Intent(requireContext(), BillTransferFailActivity::class.java)
            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtra("orderInfo", info)
            this.startActivity(intent)
        } else {
            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtra("orderInfo", info)
            this.startActivity(intent)
        }
    }

    override fun onViewPaymentOptionsClick(info: OrderInfo) {
    }

    override fun onPaymentClick(info: OrderInfo) {
    }
}