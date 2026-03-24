package com.credit.bridge.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.R
import com.credit.bridge.base.BaseAdapter
import com.credit.bridge.databinding.ItemCommonListBinding
import com.credit.bridge.inter.OnClickListener
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.remote.bean.CommonBean


class CommonListAdapter(
    private val mContext: Context,
    var selectedIndex: Int,
    items: MutableList<CommonBean>,
    private val clickListener: OnClickListener,
) : BaseAdapter<CommonBean, CommonListAdapter.BillListViewHolder>(items) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillListViewHolder {
        val binding = ItemCommonListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BillListViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.bindView.nameTv.text = item.name
        if (selectedIndex == position) {
            holder.bindView.nameTv.setTextColor(mContext.getColor(R.color.sheet_item_select_color))
            holder.bindView.nameTv.setTypeface(null, Typeface.BOLD)
            holder.bindView.nameTv.textSize = 18f
        } else {
            holder.bindView.nameTv.setTextColor(mContext.getColor(R.color.sheet_item_color))
            holder.bindView.nameTv.setTypeface(null, Typeface.NORMAL)
            holder.bindView.nameTv.textSize = 15f
        }
        holder.bindView.nameTv.setOnClickListener {
            clickListener.onClick(position)
        }
    }

    class BillListViewHolder(val bindView: ItemCommonListBinding) :
        RecyclerView.ViewHolder(bindView.root)
}