package com.credit.bridge.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.R
import com.credit.bridge.base.BaseAdapter
import com.credit.bridge.databinding.ItemOrderListBinding
import com.credit.bridge.inter.OrderItemClickListener
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.util.DataFormatUtils
import com.credit.bridge.util.OrderStatus


class OrderListAdapter(
    private val mContext: Context,
    items: MutableList<OrderInfo>,
    private val listener: OrderItemClickListener,
) : BaseAdapter<OrderInfo, OrderListAdapter.BillListViewHolder>(items) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillListViewHolder {
        val binding = ItemOrderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BillListViewHolder,
        position: Int
    ) {
        val itemInfo = items[position]
        /*holder.bindView.tvStatusDesc.text = itemInfo.fwwluzpnudp
        holder.bindView.tvStatus.text = mContext.getString(R.string.bill_status_process)*/
        holder.bindView.tvAmount.text = mContext.getString(R.string.home_symbol) + " " + DataFormatUtils.float2Str(itemInfo.tznvtuengiwnrnieyulnqsao)
        holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips2)
        holder.bindView.tvDate.text = itemInfo.suibvbw
        holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_date_tips1)

        val orderStatus = OrderStatus.getStatusByValue(itemInfo.ufzqlyyxash)

        when (orderStatus) {
            OrderStatus.PRE_REVIEW, OrderStatus.ISSUING -> {
                //holder.bindView.tvStatus.text = mContext.getString(R.string.bill_status_process)
                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips2)
                holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_date_tips1)
                //holder.bindView.tvStatus.setTextColor(mContext.resources.getColor(R.color.text_gray, null))
                holder.bindView.tvDate.setTextColor(mContext.resources.getColor(R.color.primary_text, null))
                holder.bindView.llBtn.visibility = View.GONE

                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips3)
                holder.bindView.tvAmount.text = mContext.getString(R.string.home_symbol) + " " +  DataFormatUtils.float2Str(itemInfo.oalkejegtgf)
                holder.bindView.tvDate.text = itemInfo.cofllshoi
            }
            OrderStatus.OVERDUE -> {
                //holder.bindView.tvStatus.text = mContext.getString(R.string.bill_status_past_due)
                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips2)
                holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_date_tips2)
                //holder.bindView.tvStatus.setTextColor(mContext.resources.getColor(R.color.text_red, null))
                holder.bindView.tvDate.setTextColor(mContext.resources.getColor(R.color.text_red, null))
                holder.bindView.llBtn.visibility = View.VISIBLE
                holder.bindView.tvExtend.visibility = View.GONE
                holder.bindView.tvPay.visibility = View.VISIBLE

                holder.bindView.tvDate.text = itemInfo.suibvbw
            }
            OrderStatus.CURRENT -> {
                //holder.bindView.tvStatus.text = mContext.getString(R.string.bill_status_due)
                //holder.bindView.tvStatus.setTextColor(mContext.resources.getColor(R.color.text_gold, null))
                holder.bindView.tvDate.setTextColor(mContext.resources.getColor(R.color.primary_text, null))
                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips2)
                holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_date_tips2)
                holder.bindView.llBtn.visibility = View.VISIBLE
                holder.bindView.tvExtend.visibility = View.VISIBLE
                holder.bindView.tvPay.visibility = View.VISIBLE
            }
            OrderStatus.PAID_OFF -> {
                //holder.bindView.tvStatus.text = mContext.getString(R.string.bill_status_paid)
                //holder.bindView.tvStatus.setTextColor(mContext.resources.getColor(R.color.text_gray, null))
                holder.bindView.tvDate.setTextColor(mContext.resources.getColor(R.color.primary_text, null))
                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips1)
                holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_date_tips3)
                holder.bindView.llBtn.visibility = View.GONE

                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_details_paid_off_amount)
                holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_details_paid_off_date)
                holder.bindView.tvAmount.text = mContext.getString(R.string.home_symbol) + " " +  itemInfo.oalkejegtgf
            }
            OrderStatus.ISSUE_FAILED-> {
                //holder.bindView.tvStatus.text = mContext.getString(R.string.bill_status_cancel)
                //holder.bindView.tvStatus.setTextColor(mContext.resources.getColor(R.color.text_gray, null))
                holder.bindView.tvDate.setTextColor(mContext.resources.getColor(R.color.primary_text, null))
                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips2)
                holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_date_tips2)
                holder.bindView.llBtn.visibility = View.GONE

                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips3)
                holder.bindView.tvAmount.text = mContext.getString(R.string.home_symbol) + " " +  DataFormatUtils.float2Str(itemInfo.oalkejegtgf)
            }
            OrderStatus.CLOSED-> {
                //holder.bindView.tvStatus.text = mContext.getString(R.string.bill_status_closed)
                //holder.bindView.tvStatus.setTextColor(mContext.resources.getColor(R.color.text_gray, null))
                holder.bindView.tvDate.setTextColor(mContext.resources.getColor(R.color.primary_text, null))
                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips2)
                holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_date_tips2)
                holder.bindView.llBtn.visibility = View.GONE

                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips3)
                holder.bindView.tvAmount.text = mContext.getString(R.string.home_symbol) + " " +  DataFormatUtils.float2Str(itemInfo.oalkejegtgf)
            }
            else -> {
                //holder.bindView.tvStatus.text = mContext.getString(R.string.bill_status_cancel)
                //holder.bindView.tvStatus.setTextColor(mContext.resources.getColor(R.color.text_gray, null))
                holder.bindView.tvDate.setTextColor(mContext.resources.getColor(R.color.primary_text, null))
                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips2)
                holder.bindView.tvDateDesc.text = mContext.getString(R.string.bill_date_tips1)
                holder.bindView.llBtn.visibility = View.GONE

                holder.bindView.tvAmountDesc.text = mContext.getString(R.string.bill_dollar_tips3)
                holder.bindView.tvAmount.text = mContext.getString(R.string.home_symbol) + " " +  DataFormatUtils.float2Str(itemInfo.oalkejegtgf)
            }
        }

        holder.bindView.root.setOnClickListener {
            listener.onItemClick(itemInfo)
        }
    }

    class BillListViewHolder(val bindView: ItemOrderListBinding) :
        RecyclerView.ViewHolder(bindView.root)
}