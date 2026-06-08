package com.credit.bridge.widget

import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.adapter.WheelColumnAdapter

class WheelColumnController(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val onSelectionChanged: (index: Int) -> Unit,
) {
    private val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    private lateinit var adapter: WheelColumnAdapter
    private lateinit var snapHelper: WheelSnapHelper
    private var suppressSelectionCallback = false

    fun setup(initialLabels: List<String>, initialSelectedIndex: Int, wheelHeight: Int, itemHeight: Int) {
        adapter = WheelColumnAdapter(context, initialLabels, initialSelectedIndex)
        snapHelper = WheelSnapHelper { adapter.dataCount }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)
        recyclerView.setItemViewCacheSize(12)
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        recyclerView.isNestedScrollingEnabled = true
        snapHelper.attachToRecyclerView(recyclerView)

        val verticalPadding = (wheelHeight - itemHeight) / 2
        recyclerView.setPadding(0, verticalPadding, 0, verticalPadding)
        recyclerView.clipToPadding = false

        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.parent?.requestDisallowInterceptTouchEvent(true)
            }
            false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE || suppressSelectionCallback) return
                var index = getCenterDataIndex()
                if (index == null) {
                    val bound = if (layoutManager.findFirstVisibleItemPosition() <= 0) {
                        0
                    } else {
                        adapter.dataCount - 1
                    }
                    scrollToIndex(bound)
                    return
                }
                if (index != adapter.selectedIndex) {
                    adapter.selectedIndex = index
                    adapter.notifyItemRangeChanged(0, adapter.itemCount)
                    onSelectionChanged(index)
                }
            }
        })
    }

    fun scrollToIndex(dataIndex: Int, notifySelection: Boolean = false) {
        if (adapter.dataCount <= 0) return
        val safeIndex = dataIndex.coerceIn(0, adapter.dataCount - 1)
        adapter.selectedIndex = safeIndex
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
        suppressSelectionCallback = true
        layoutManager.scrollToPosition(safeIndex)
        alignPositionToCenter(safeIndex, retry = 0) {
            suppressSelectionCallback = false
            if (notifySelection) {
                onSelectionChanged(safeIndex)
            }
        }
    }

    fun updateLabels(newLabels: List<String>, newSelectedIndex: Int) {
        suppressSelectionCallback = true
        adapter.updateLabels(newLabels, newSelectedIndex)
        scrollToIndex(adapter.selectedIndex)
    }

    fun getCenterDataIndex(): Int? {
        val centerView = snapHelper.findSnapView(layoutManager) ?: return null
        val index = layoutManager.getPosition(centerView)
        if (index < 0 || index >= adapter.dataCount) return null
        return index
    }

    private fun alignPositionToCenter(adapterPosition: Int, retry: Int, onAligned: (() -> Unit)? = null) {
        if (retry > 8) {
            onAligned?.invoke()
            return
        }
        recyclerView.post {
            val child = layoutManager.findViewByPosition(adapterPosition)
            if (child == null) {
                layoutManager.scrollToPosition(adapterPosition)
                alignPositionToCenter(adapterPosition, retry + 1, onAligned)
                return@post
            }
            val rvCenter = recyclerView.height / 2f
            val childCenter = (child.top + child.bottom) / 2f
            val dy = (childCenter - rvCenter).toInt()
            if (dy != 0) {
                recyclerView.scrollBy(0, dy)
            }
            recyclerView.post {
                val aligned = layoutManager.findViewByPosition(adapterPosition) ?: run {
                    onAligned?.invoke()
                    return@post
                }
                val remain = ((aligned.top + aligned.bottom) / 2f - recyclerView.height / 2f).toInt()
                if (kotlin.math.abs(remain) > 1) {
                    recyclerView.scrollBy(0, remain)
                }
                onAligned?.invoke()
            }
        }
    }
}
