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
import com.credit.bridge.databinding.ItemOrderListBinding
import com.credit.bridge.inter.OnItemClickListener
import com.credit.bridge.inter.OnOrderItemClickListener
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.util.DataFormatUtils
import com.credit.bridge.util.OrderStatus


class OrderListAdapter(
    private val mContext: Context,
    items: MutableList<OrderInfo>,
    private val listener: OnOrderItemClickListener,
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

        holder.bindView.statusTv.text = itemInfo.xjywdrtdxzt
        holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                " " + DataFormatUtils.float2Str(itemInfo.otjjqwdpupp)
        holder.bindView.descTv.text = "Created At"
        holder.bindView.dateDescTv.text = "Created At"
        holder.bindView.dateTv.text = itemInfo.dhqprsdsv
        holder.bindView.paymentOptionsTv.visibility = View.GONE
        holder.bindView.continuePaymentTv.visibility = View.GONE

        val orderStatus = itemInfo.xjywdrtdxzt

        when(orderStatus){
            ConstConfig.ORDER_STATUS_PRE_REVIEW, ConstConfig.ORDER_STATUS_ISSUING -> {
                holder.bindView.statusTv.text = itemInfo.xjywdrtdxzt
                holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                        " " + DataFormatUtils.float2Str(itemInfo.otjjqwdpupp)
                holder.bindView.descTv.text = mContext.getString(R.string.order_desc_usage_amount)
                holder.bindView.dateDescTv.text = mContext.getString(R.string.order_date_desc_created_on) + " "
                holder.bindView.statusTv.text = mContext.getString(R.string.order_status_processing)
                holder.bindView.statusTv.setBackgroundResource(R.drawable.shape_order_status_processing)
                holder.bindView.statusTv.setTextColor(mContext.getColor(R.color.order_processing))
                holder.bindView.dateTv.text = itemInfo.dhqprsdsv
                holder.bindView.paymentOptionsTv.visibility = View.GONE
                holder.bindView.rightArrowIv.visibility = View.GONE
                holder.bindView.continuePaymentTv.visibility = View.GONE
            }
            ConstConfig.ORDER_STATUS_OVERDUE -> {
                holder.bindView.statusTv.text = itemInfo.xjywdrtdxzt
                holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                        " " + DataFormatUtils.float2Str(itemInfo.kmlwyjhlacigctavsolh)
                holder.bindView.descTv.text = mContext.getString(R.string.order_desc_amount_due)
                holder.bindView.dateDescTv.text = mContext.getString(R.string.order_date_desc_due_date)+ " "
                holder.bindView.statusTv.text = mContext.getString(R.string.order_status_overdue)
                holder.bindView.statusTv.setBackgroundResource(R.drawable.shape_order_status_due)
                holder.bindView.statusTv.setTextColor(mContext.getColor(R.color.order_overdue))
                holder.bindView.dateTv.text = itemInfo.vzlwrta
                holder.bindView.paymentOptionsTv.visibility = View.VISIBLE
                holder.bindView.rightArrowIv.visibility = View.VISIBLE
                holder.bindView.continuePaymentTv.visibility = View.VISIBLE

                if (itemInfo.umoatyothkt) {
                    holder.bindView.paymentOptionsTv.visibility = View.VISIBLE
                    holder.bindView.rightArrowIv.visibility = View.VISIBLE
                }else{
                    holder.bindView.paymentOptionsTv.visibility = View.GONE
                    holder.bindView.rightArrowIv.visibility = View.GONE
                }

                itemInfo.qpruccpdjot?.let {
                    if (it.toInt() > 7) {
                        holder.bindView.paymentOptionsTv.visibility = View.GONE
                        holder.bindView.rightArrowIv.visibility = View.GONE
                    }
                }
            }
            ConstConfig.ORDER_STATUS_CURRENT -> {
                holder.bindView.statusTv.text = itemInfo.xjywdrtdxzt
                holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                        " " + DataFormatUtils.float2Str(itemInfo.kgchobzqirjuftermzzgajda)
                holder.bindView.descTv.text = mContext.getString(R.string.order_desc_amount_due)
                holder.bindView.dateDescTv.text = mContext.getString(R.string.order_date_desc_due_date)+ " "
                holder.bindView.statusTv.text = mContext.getString(R.string.order_status_due)
                holder.bindView.statusTv.setBackgroundResource(R.drawable.shape_order_status_due)
                holder.bindView.statusTv.setTextColor(mContext.getColor(R.color.order_due))
                holder.bindView.dateTv.text = itemInfo.vzlwrta
                holder.bindView.paymentOptionsTv.visibility = View.VISIBLE
                holder.bindView.rightArrowIv.visibility = View.VISIBLE
                holder.bindView.continuePaymentTv.visibility = View.VISIBLE

                if (itemInfo.umoatyothkt) {
                    holder.bindView.paymentOptionsTv.visibility = View.VISIBLE
                    holder.bindView.rightArrowIv.visibility = View.VISIBLE
                }else{
                    holder.bindView.paymentOptionsTv.visibility = View.GONE
                    holder.bindView.rightArrowIv.visibility = View.GONE
                }
            }
            ConstConfig.ORDER_STATUS_PAID_OFF -> {
                holder.bindView.statusTv.text = itemInfo.xjywdrtdxzt
                holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                        " " + DataFormatUtils.float2Str(itemInfo.otjjqwdpupp)
                holder.bindView.descTv.text = mContext.getString(R.string.order_desc_amount_paid)
                holder.bindView.dateDescTv.text = mContext.getString(R.string.order_date_desc_payment_date)+ " "
                holder.bindView.statusTv.text = mContext.getString(R.string.order_status_paid)
                holder.bindView.statusTv.setBackgroundResource(R.drawable.shape_order_status_paid)
                holder.bindView.statusTv.setTextColor(mContext.getColor(R.color.order_paid))
                holder.bindView.dateTv.text = itemInfo.dhqprsdsv
                holder.bindView.paymentOptionsTv.visibility = View.GONE
                holder.bindView.rightArrowIv.visibility = View.GONE
                holder.bindView.continuePaymentTv.visibility = View.GONE
            }
            ConstConfig.ORDER_STATUS_ISSUE_FAILED -> {
                holder.bindView.statusTv.text = itemInfo.xjywdrtdxzt
                holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                        " " + DataFormatUtils.float2Str(itemInfo.otjjqwdpupp)
                holder.bindView.descTv.text = mContext.getString(R.string.order_desc_usage_amount)
                holder.bindView.dateDescTv.text = mContext.getString(R.string.order_date_desc_created_on)+ " "
                holder.bindView.statusTv.text = mContext.getString(R.string.order_status_closed)
                holder.bindView.statusTv.setBackgroundResource(R.drawable.shape_order_status_closed)
                holder.bindView.statusTv.setTextColor(mContext.getColor(R.color.order_closed))
                holder.bindView.dateTv.text = itemInfo.dhqprsdsv
                holder.bindView.paymentOptionsTv.visibility = View.GONE
                holder.bindView.rightArrowIv.visibility = View.GONE
                holder.bindView.continuePaymentTv.visibility = View.GONE
            }
            ConstConfig.ORDER_STATUS_CLOSED -> {
                holder.bindView.statusTv.text = itemInfo.xjywdrtdxzt
                holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                        " " + DataFormatUtils.float2Str(itemInfo.otjjqwdpupp)
                holder.bindView.descTv.text = mContext.getString(R.string.order_desc_usage_amount)
                holder.bindView.dateDescTv.text = mContext.getString(R.string.order_date_desc_created_on)+ " "
                holder.bindView.statusTv.text = mContext.getString(R.string.order_status_closed)
                holder.bindView.statusTv.setBackgroundResource(R.drawable.shape_order_status_closed)
                holder.bindView.statusTv.setTextColor(mContext.getColor(R.color.order_closed))
                holder.bindView.dateTv.text = itemInfo.dhqprsdsv
                holder.bindView.paymentOptionsTv.visibility = View.GONE
                holder.bindView.rightArrowIv.visibility = View.GONE
                holder.bindView.continuePaymentTv.visibility = View.GONE
            }
            else -> {
                holder.bindView.statusTv.text = itemInfo.xjywdrtdxzt
                holder.bindView.amountTv.text = mContext.getString(R.string.money_symbol) +
                        " " + DataFormatUtils.float2Str(itemInfo.otjjqwdpupp)
                holder.bindView.descTv.text = mContext.getString(R.string.order_desc_usage_amount)
                holder.bindView.dateDescTv.text = mContext.getString(R.string.order_date_desc_created_on)+ " "
                holder.bindView.statusTv.text = mContext.getString(R.string.order_status_closed)
                holder.bindView.statusTv.setBackgroundResource(R.drawable.shape_order_status_closed)
                holder.bindView.statusTv.setTextColor(mContext.getColor(R.color.order_closed))
                holder.bindView.dateTv.text = itemInfo.dhqprsdsv
                holder.bindView.paymentOptionsTv.visibility = View.GONE
                holder.bindView.rightArrowIv.visibility = View.GONE
                holder.bindView.continuePaymentTv.visibility = View.GONE
            }
        }

        holder.bindView.root.setOnClickListener {
            listener.onOrderItemClick(itemInfo)
        }
        holder.bindView.paymentOptionsTv.setOnClickListener {
            listener.onViewPaymentOptionsClick(itemInfo)
        }
        holder.bindView.continuePaymentTv.setOnClickListener {
            listener.onPaymentClick(itemInfo)
        }
    }

    class BillListViewHolder(val bindView: ItemOrderListBinding) :
        RecyclerView.ViewHolder(bindView.root)
}