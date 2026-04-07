package com.credit.bridge.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.R
import com.credit.bridge.base.BaseAdapter
import com.credit.bridge.databinding.ItemOrderListBinding
import com.credit.bridge.databinding.ItemProductListBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.inter.OnProductItemClickListener
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.remote.bean.ProductInfo
import com.credit.bridge.util.OrderStatus


class ProductListAdapter(
    private val mContext: Context,
    items: MutableList<ProductInfo>,
    private val listener: OnProductItemClickListener,
) : BaseAdapter<ProductInfo, ProductListAdapter.BillListViewHolder>(items) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillListViewHolder {
        val binding = ItemProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BillListViewHolder,
        position: Int
    ) {
        val itemInfo = items[position]

        holder.bindView.amountPaidTv.text = mContext.getString(R.string.money_symbol) + " " + itemInfo.vaezrwyawufghpo.toString()
        holder.bindView.daysTv.text = mContext.getString(R.string.product_up_to) +
                " " + itemInfo.tkjgeq.toString() + " " + itemInfo.wnvelqecci

        holder.bindView.root.setOnClickListener {
            listener.onItemClick(itemInfo)
        }
    }

    class BillListViewHolder(val bindView: ItemProductListBinding) :
        RecyclerView.ViewHolder(bindView.root)
}