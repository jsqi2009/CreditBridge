package com.credit.bridge.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.adapter.WheelDateAdapter
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetBirthdayPickerBinding
import com.credit.bridge.inter.OnBirthdaySelectListener
import com.credit.bridge.inter.OnClickListener
import com.credit.bridge.util.BirthdayDateHelper

class BirthdayPickerBottomSheet(
    private val mContext: Context,
    private val initialFormValue: String?,
    private val onBirthdaySelectListener: OnBirthdaySelectListener
) : BaseBottomSheet<BottomSheetBirthdayPickerBinding>(), View.OnClickListener {

    private val dayCount = BirthdayDateHelper.getDayCount()
    private lateinit var adapter: WheelDateAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val snapHelper = LinearSnapHelper()
    private var selectedDataIndex = 0
    private var edgePadCount = 0

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetBirthdayPickerBinding {
        return BottomSheetBirthdayPickerBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showExpanded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews.titleTv.text = mContext.getString(com.credit.bridge.R.string.hint_select)
        bindViews.dismissIv.setOnClickListener(this)

        selectedDataIndex = BirthdayDateHelper.resolveInitialIndex(initialFormValue)

        val itemHeight = resources.getDimensionPixelSize(com.credit.bridge.R.dimen.wheel_date_item_height)
        val wheelHeight = resources.getDimensionPixelSize(com.credit.bridge.R.dimen.wheel_date_picker_height)
        edgePadCount = (wheelHeight / itemHeight / 2).coerceAtLeast(3) + 2

        adapter = WheelDateAdapter(mContext, edgePadCount, dayCount, selectedDataIndex, object : OnClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClick(index: Int) {
                selectedDataIndex = index
                adapter.selectedDataIndex = index
                adapter.notifyDataSetChanged()
                val item = BirthdayDateHelper.getItem(index)
                onBirthdaySelectListener.onSelect(item.formValue, item.displayText)
                Handler(Looper.getMainLooper()).postDelayed({
                    dismiss()
                }, 300)
            }
        })

        layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        bindViews.dateRecyclerView.layoutManager = layoutManager
        bindViews.dateRecyclerView.adapter = adapter
        bindViews.dateRecyclerView.setHasFixedSize(true)
        bindViews.dateRecyclerView.setItemViewCacheSize(12)
        snapHelper.attachToRecyclerView(bindViews.dateRecyclerView)

        val verticalPadding = (wheelHeight - itemHeight) / 2
        bindViews.dateRecyclerView.setPadding(0, verticalPadding, 0, verticalPadding)

        bindViews.dateRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (bindViews.dateRecyclerView.height <= 0) return
                    bindViews.dateRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    centerOnDataIndex(selectedDataIndex)
                }
            }
        )
    }

    private fun centerOnDataIndex(dataIndex: Int) {
        val safeIndex = dataIndex.coerceIn(0, dayCount - 1)
        val adapterPosition = adapter.adapterPositionForDataIndex(safeIndex)
        layoutManager.scrollToPosition(adapterPosition)
        alignAdapterPositionToCenter(adapterPosition, retry = 0)
    }

    private fun alignAdapterPositionToCenter(adapterPosition: Int, retry: Int) {
        if (retry > 8) return
        val rv = bindViews.dateRecyclerView
        rv.post {
            val child = layoutManager.findViewByPosition(adapterPosition)
            if (child == null) {
                layoutManager.scrollToPosition(adapterPosition)
                alignAdapterPositionToCenter(adapterPosition, retry + 1)
                return@post
            }
            val rvCenter = rv.height / 2f
            val childCenter = (child.top + child.bottom) / 2f
            val dy = (childCenter - rvCenter).toInt()
            if (dy != 0) {
                rv.scrollBy(0, dy)
            }
            rv.post {
                val aligned = layoutManager.findViewByPosition(adapterPosition) ?: return@post
                val remain = ((aligned.top + aligned.bottom) / 2f - rv.height / 2f).toInt()
                if (kotlin.math.abs(remain) > 1) {
                    rv.scrollBy(0, remain)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            com.credit.bridge.R.id.dismissIv -> dismiss()
        }
    }
}
