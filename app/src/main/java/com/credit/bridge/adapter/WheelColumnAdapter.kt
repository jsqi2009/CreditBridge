package com.credit.bridge.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.R

class WheelColumnAdapter(
    private val mContext: Context,
    private var labels: List<String>,
    var selectedIndex: Int,
) : RecyclerView.Adapter<WheelColumnAdapter.Holder>() {

    class Holder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    val dataCount: Int
        get() = labels.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wheel_date, parent, false) as TextView
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (position < 0 || position >= labels.size) {
            holder.textView.text = ""
            return
        }
        holder.textView.text = labels[position]
        if (selectedIndex == position) {
            holder.textView.setTextColor(mContext.getColor(R.color.sheet_item_select_color))
            holder.textView.setTypeface(null, Typeface.BOLD)
            holder.textView.textSize = 18f
        } else {
            holder.textView.setTextColor(mContext.getColor(R.color.sheet_item_color))
            holder.textView.setTypeface(null, Typeface.NORMAL)
            holder.textView.textSize = 15f
        }
    }

    override fun getItemCount(): Int = labels.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateLabels(newLabels: List<String>, newSelectedIndex: Int) {
        labels = newLabels
        selectedIndex = newSelectedIndex.coerceIn(0, (labels.size - 1).coerceAtLeast(0))
        notifyDataSetChanged()
    }
}
