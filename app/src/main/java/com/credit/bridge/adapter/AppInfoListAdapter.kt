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
import com.credit.bridge.databinding.ItemAppInfoListBinding
import com.credit.bridge.databinding.ItemOrderListBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.inter.OnOrderItemClickListener
import com.credit.bridge.remote.bean.AppItemInfo
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.util.DataFormatUtils
import com.credit.bridge.util.OrderStatus


class AppInfoListAdapter(
    private val mContext: Context,
    items: MutableList<AppItemInfo>,
) : BaseAdapter<AppItemInfo, AppInfoListAdapter.BillListViewHolder>(items) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillListViewHolder {
        val binding = ItemAppInfoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BillListViewHolder,
        position: Int
    ) {
        val itemInfo = items[position]

        holder.bindView.appInfoTv.text = itemInfo.ntwf + ": " + itemInfo.qnhoa

    }

    class BillListViewHolder(val bindView: ItemAppInfoListBinding) :
        RecyclerView.ViewHolder(bindView.root)
}