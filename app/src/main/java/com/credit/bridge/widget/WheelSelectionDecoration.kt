package com.credit.bridge.widget

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.credit.bridge.R

class WheelSelectionDecoration(
    private val lineColorRes: Int = R.color.line_color,
    private val horizontalMarginPx: Int = 0,
    private val lineOffsetPx: Int = 0,
) : RecyclerView.ItemDecoration() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 1f
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.height <= 0) return
        paint.color = ContextCompat.getColor(parent.context, lineColorRes)
        val centerY = parent.height / 2f
        val left = parent.paddingLeft.toFloat() + horizontalMarginPx
        val right = parent.width - parent.paddingRight.toFloat() - horizontalMarginPx
        c.drawLine(left, centerY - lineOffsetPx, right, centerY - lineOffsetPx, paint)
        c.drawLine(left, centerY + lineOffsetPx, right, centerY + lineOffsetPx, paint)
    }
}
