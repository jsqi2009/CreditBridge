package com.credit.bridge.base

import androidx.recyclerview.widget.RecyclerView

/**
 * author : Jason
 * desc   :
 */
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(
    protected val items: MutableList<T>
) : RecyclerView.Adapter<VH>() {

    override fun getItemCount(): Int = items.size

    fun setData(newData: List<T>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }


    fun getData(): List<T> {
        return items
    }

    fun getItem(position: Int): T = items[position]
}
