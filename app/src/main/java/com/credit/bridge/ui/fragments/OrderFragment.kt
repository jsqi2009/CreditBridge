package com.credit.bridge.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.credit.bridge.R
import com.credit.bridge.adapter.OrderListAdapter
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.FragmentHomeBinding
import com.credit.bridge.databinding.FragmentOrderBinding
import com.credit.bridge.inter.OrderItemClickListener
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.ui.order.OrderDetailsActivity
import com.credit.bridge.util.OrderStatus
import com.credit.bridge.util.ScreenUtil

class OrderFragment : BaseFragment<FragmentOrderBinding>(), View.OnClickListener, OrderItemClickListener {
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
        initListAdapter()
    }

    private fun initAllRes() {
        bindViews.currentLayout.setOnClickListener(this)
        bindViews.historyLayout.setOnClickListener(this)

        orderList.add(OrderInfo())
        orderList.add(OrderInfo())
        orderList.add(OrderInfo())
        orderList.add(OrderInfo())
    }

    private fun fetchBillOrderList() {

        //HttpClient.getOrderList(requireContext(), selectedType,"bill")
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
                fetchBillOrderList()
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
                fetchBillOrderList()
            }
        }
    }

    private fun initListAdapter() {

        bindViews.orderRv.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = OrderListAdapter(requireActivity(), items = orderList,this)
        bindViews.orderRv.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

    override fun onItemClick(info: OrderInfo) {
        if (OrderStatus.getStatusByValue(info.ufzqlyyxash) == OrderStatus.ISSUE_FAILED) {
            //val intent = Intent(requireContext(), BillTransferFailActivity::class.java)
            val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
            intent.putExtra("orderInfo", info)
            requireContext().startActivity(intent)
        } else {
            val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
            intent.putExtra("orderInfo", info)
            requireContext().startActivity(intent)
        }
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