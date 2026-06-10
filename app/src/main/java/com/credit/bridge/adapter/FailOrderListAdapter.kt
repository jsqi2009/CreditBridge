package com.credit.bridge.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.R
import com.credit.bridge.base.BaseAdapter
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.ItemFailOrderListBinding
import com.credit.bridge.databinding.ItemOrderListBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.inter.OnOrderItemClickListener
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.util.DataFormatUtils
import com.credit.bridge.util.OrderStatus
import kotlin.toString


class FailOrderListAdapter(
    private val mContext: Context,
    items: MutableList<OrderInfo>,
    private val listener: OnOrderItemClickListener,
) : BaseAdapter<OrderInfo, FailOrderListAdapter.BillListViewHolder>(items) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillListViewHolder {
        val binding = ItemFailOrderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BillListViewHolder,
        position: Int
    ) {
        val itemInfo = items[position]

        holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                " " + DataFormatUtils.float2Str(itemInfo.otjjqwdpupp)
        holder.bindView.dateTv.text = itemInfo.dhqprsdsv
        holder.bindView.usageIdTv.text = itemInfo.kcyrbnp.toString()
        holder.bindView.productName.text = itemInfo.jerftvqtjkg.toString()

        holder.bindView.root.setOnClickListener {
            listener.onOrderItemClick(itemInfo)
        }
    }

    class BillListViewHolder(val bindView: ItemFailOrderListBinding) :
        RecyclerView.ViewHolder(bindView.root)
}