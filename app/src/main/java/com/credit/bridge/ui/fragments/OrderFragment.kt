package com.credit.bridge.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.credit.bridge.R
import com.credit.bridge.adapter.OrderListAdapter
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.FragmentOrderBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.inter.OnOrderItemClickListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.remote.event.OrderListResponseEvent
import com.credit.bridge.remote.event.PaymentLinkResponseEvent
import com.credit.bridge.remote.event.ViewPaymentOptionsEvent
import com.credit.bridge.ui.order.OrderDetailsActivity
import com.credit.bridge.util.OrderStatus
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe

class OrderFragment : BaseFragment<FragmentOrderBinding>(), View.OnClickListener,
    OnOrderItemClickListener {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOrderBinding.inflate(inflater, container, false)


    private var orderType = ConstConfig.ORDER_TYPE_CURRENT
    private var orderList: ArrayList<OrderInfo> =  ArrayList()
    private var mAdapter: OrderListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAllRes()
    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            fetchOrderList()
        }
    }

    override fun initRes() {
        super.initRes()

        bindViews.currentLayout.setOnClickListener(this)
        bindViews.historyLayout.setOnClickListener(this)

        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_PRE_REVIEW))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_ISSUING))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_OVERDUE))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_CURRENT))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_PAID_OFF))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_REJECTED))
        orderList.add(OrderInfo(fwwluzpnudp = ConstConfig.ORDER_STATUS_CLOSED))

        initListAdapter()
    }

    private fun initAllRes() {

    }

    private fun fetchOrderList() {
        HttpClient.fetchOrderList(requireContext(), orderType,"bill")
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun onFetchOrderListEvent(event: OrderListResponseEvent) {
        if (event.isSuccess) {
            if (event.model?.flag == "bill") {

                orderList = event.model?.mtaw ?: ArrayList()
                mAdapter?.setData(orderList!!)
                mAdapter?.notifyDataSetChanged()

                if (mAdapter?.getData()?.isEmpty() == true) {
                    bindViews.defaultView.visibility = View.VISIBLE
                } else {
                    bindViews.defaultView.visibility = View.GONE
                }
            }
        } else {
            ToastUtil.showLong(requireActivity(),event.retMsg)
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.currentLayout -> {
                bindViews.currentTab.setTextColor(resources.getColor(R.color.text_selected, null))
                bindViews.historyTab.setTextColor(resources.getColor(R.color.text_unselected, null))
                bindViews.currentTab.textSize = 20f
                bindViews.historyTab.textSize = 13f
                bindViews.historyIv.visibility = View.GONE
                bindViews.currentIv.visibility = View.VISIBLE

                orderType = ConstConfig.ORDER_TYPE_CURRENT
                fetchOrderList()
            }
            R.id.historyLayout -> {
                bindViews.historyTab.setTextColor(resources.getColor(R.color.text_selected, null))
                bindViews.currentTab.setTextColor(resources.getColor(R.color.text_unselected, null))
                bindViews.historyTab.textSize = 20f
                bindViews.currentTab.textSize = 13f
                /*bindViews.historyTab.textSize = ScreenUtil.sp2px(requireActivity(), 20f).toFloat()
                bindViews.currentTab.textSize = ScreenUtil.sp2px(requireActivity(), 13f).toFloat()*/
                bindViews.currentIv.visibility = View.GONE
                bindViews.historyIv.visibility = View.VISIBLE

                orderType = ConstConfig.ORDER_TYPE_HISTORY
                fetchOrderList()
            }
        }
    }

    private fun initListAdapter() {

        bindViews.orderRv.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = OrderListAdapter(requireActivity(), items = orderList,this)
        bindViews.orderRv.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

    override fun onOrderItemClick(info: OrderInfo) {
        if (OrderStatus.getStatusByValue(info.ufzqlyyxash) == OrderStatus.ISSUE_FAILED) {
            //val intent = Intent(requireContext(), BillTransferFailActivity::class.java)
            val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
            intent.putExtra("info", info)
            requireContext().startActivity(intent)
        } else {
            val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
            intent.putExtra("info", info)
            requireContext().startActivity(intent)
        }
    }

    override fun onViewPaymentOptionsClick(info: OrderInfo) {
        paymentOptionsAction(info)
    }

    override fun onPaymentClick(info: OrderInfo) {
        showLoading()
        HttpClient.getPaymentLink(requireContext(), false,info.qyfqljd.toString(), 1)
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
            ToastUtil.showLong(requireContext(),event.networkError.toString())
        }
    }

    @Subscribe
    fun onViewPaymentOptionsEvent(event: ViewPaymentOptionsEvent) {
        if (event.info != null) {
            paymentOptionsAction(event.info!!)
        }
    }

    private fun paymentOptionsAction(info: OrderInfo) {
        val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
        intent.putExtra("info", info)
        intent.putExtra("isExtend", true)
        requireContext().startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(): OrderFragment {
            val args = Bundle()
            val fragment = OrderFragment()
            fragment.arguments = args
            return fragment
        }
    }
}