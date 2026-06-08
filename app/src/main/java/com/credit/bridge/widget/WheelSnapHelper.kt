package com.credit.bridge.widget

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView


class WheelSnapHelper(
    private val getDataCount: () -> Int,
) : LinearSnapHelper() {

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager !is LinearLayoutManager) return null
        val dataCount = getDataCount()
        if (dataCount <= 0) return null

        val centerY = layoutManager.height / 2f
        var closestView: View? = null
        var closestDistance = Float.MAX_VALUE

        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i) ?: continue
            val position = layoutManager.getPosition(child)
            if (position < 0 || position >= dataCount) continue

            val childCenter = (child.top + child.bottom) / 2f
            val distance = kotlin.math.abs(childCenter - centerY)
            if (distance < closestDistance) {
                closestDistance = distance
                closestView = child
            }
        }

        if (closestView != null) return closestView

        val first = layoutManager.findViewByPosition(0)
        val last = layoutManager.findViewByPosition(dataCount - 1)
        return when {
            first != null && last != null -> {
                val firstDist = kotlin.math.abs((first.top + first.bottom) / 2f - centerY)
                val lastDist = kotlin.math.abs((last.top + last.bottom) / 2f - centerY)
                if (firstDist <= lastDist) first else last
            }
            first != null -> first
            else -> last
        }
    }
}
