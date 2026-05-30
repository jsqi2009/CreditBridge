package com.credit.bridge.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.R
import com.credit.bridge.inter.OnClickListener
import com.credit.bridge.util.BirthdayDateHelper

class WheelDateAdapter(
    private val mContext: Context,
    private val edgePadCount: Int,
    private val dayCount: Int,
    var selectedDataIndex: Int,
    private val clickListener: OnClickListener
) : RecyclerView.Adapter<WheelDateAdapter.Holder>() {

    class Holder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wheel_date, parent, false) as TextView
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val dataIndex = position - edgePadCount
        if (dataIndex < 0 || dataIndex >= dayCount) {
            holder.textView.text = ""
            holder.textView.isClickable = false
            holder.textView.setOnClickListener(null)
            return
        }
        val item = BirthdayDateHelper.getItem(dataIndex)
        holder.textView.isClickable = true
        holder.textView.text = item.displayText
        if (selectedDataIndex == dataIndex) {
            holder.textView.setTextColor(mContext.getColor(R.color.sheet_item_select_color))
            holder.textView.setTypeface(null, Typeface.BOLD)
            holder.textView.textSize = 18f
        } else {
            holder.textView.setTextColor(mContext.getColor(R.color.sheet_item_color))
            holder.textView.setTypeface(null, Typeface.NORMAL)
            holder.textView.textSize = 15f
        }
        holder.textView.setOnClickListener {
            clickListener.onClick(dataIndex)
        }
    }

    override fun getItemCount(): Int = dayCount + edgePadCount * 2

    fun adapterPositionForDataIndex(dataIndex: Int): Int = dataIndex + edgePadCount
}
