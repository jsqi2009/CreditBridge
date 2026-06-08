package com.credit.bridge.widget

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetBirthdayPickerBinding
import com.credit.bridge.inter.OnBirthdaySelectListener
import com.credit.bridge.util.BirthdayDateHelper
import com.credit.bridge.util.ToastUtil
import com.google.android.material.bottomsheet.BottomSheetDialog

class BirthdayPickerBottomSheet(
    private val mContext: Context,
    private val initialFormValue: String?,
    private val onBirthdaySelectListener: OnBirthdaySelectListener
) : BaseBottomSheet<BottomSheetBirthdayPickerBinding>(), View.OnClickListener {

    private var years: List<Int> = emptyList()
    private var months: List<Int> = emptyList()
    private var days: List<Int> = emptyList()

    private var selectedYearIndex = 0
    private var selectedMonthIndex = 0
    private var selectedDayIndex = 0

    private lateinit var dayWheel: WheelColumnController
    private lateinit var monthWheel: WheelColumnController
    private lateinit var yearWheel: WheelColumnController

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

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.isDraggable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews.titleTv.text = mContext.getString(com.credit.bridge.R.string.hint_select)
        bindViews.dismissIv.setOnClickListener(this)
        bindViews.confirmTv.setOnClickListener(this)

        val itemHeight = resources.getDimensionPixelSize(com.credit.bridge.R.dimen.wheel_date_item_height)
        val wheelHeight = resources.getDimensionPixelSize(com.credit.bridge.R.dimen.wheel_birthday_picker_height)

        val (initialDay, initialMonth, initialYear) = BirthdayDateHelper.parseInitialParts(initialFormValue)
        years = BirthdayDateHelper.getYears()
        selectedYearIndex = years.indexOf(initialYear).coerceAtLeast(0)
        refreshMonthList(initialMonth)
        refreshDayList(initialDay)

        dayWheel = WheelColumnController(mContext, bindViews.dayRecyclerView) { index ->
            selectedDayIndex = index
        }
        monthWheel = WheelColumnController(mContext, bindViews.monthRecyclerView) { index ->
            selectedMonthIndex = index
            refreshDayList(days.getOrElse(selectedDayIndex) { days.lastOrNull() ?: 1 })
        }
        yearWheel = WheelColumnController(mContext, bindViews.yearRecyclerView) { index ->
            selectedYearIndex = index
            refreshMonthList(months.getOrElse(selectedMonthIndex) { months.lastOrNull() ?: 1 })
            refreshDayList(days.getOrElse(selectedDayIndex) { days.lastOrNull() ?: 1 })
        }

        val lineOffset = resources.getDimensionPixelSize(com.credit.bridge.R.dimen.wheel_date_selection_offset_positive)
        val lineMargin = resources.getDimensionPixelSize(com.credit.bridge.R.dimen.margin_15)
        val selectionDecoration = WheelSelectionDecoration(
            horizontalMarginPx = lineMargin,
            lineOffsetPx = lineOffset,
        )
        bindViews.dayRecyclerView.addItemDecoration(selectionDecoration)
        bindViews.monthRecyclerView.addItemDecoration(selectionDecoration)
        bindViews.yearRecyclerView.addItemDecoration(selectionDecoration)

        dayWheel.setup(dayLabels(), selectedDayIndex, wheelHeight, itemHeight)
        monthWheel.setup(monthLabels(), selectedMonthIndex, wheelHeight, itemHeight)
        yearWheel.setup(yearLabels(), selectedYearIndex, wheelHeight, itemHeight)

        bindViews.wheelContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (bindViews.wheelContainer.height <= 0) return
                    bindViews.wheelContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    dayWheel.scrollToIndex(selectedDayIndex)
                    monthWheel.scrollToIndex(selectedMonthIndex)
                    yearWheel.scrollToIndex(selectedYearIndex)
                }
            }
        )
    }

    private fun refreshMonthList(preferredMonth: Int) {
        months = BirthdayDateHelper.getMonths()
        selectedMonthIndex = months.indexOf(preferredMonth).coerceIn(0, (months.size - 1).coerceAtLeast(0))
        if (::monthWheel.isInitialized) {
            monthWheel.updateLabels(monthLabels(), selectedMonthIndex)
        }
    }

    private fun refreshDayList(preferredDay: Int) {
        val year = years[selectedYearIndex]
        val month = months[selectedMonthIndex]
        days = BirthdayDateHelper.getDays(year, month)
        selectedDayIndex = days.indexOf(preferredDay).coerceIn(0, (days.size - 1).coerceAtLeast(0))
        if (::dayWheel.isInitialized) {
            dayWheel.updateLabels(dayLabels(), selectedDayIndex)
        }
    }

    private fun dayLabels() = days.map { BirthdayDateHelper.formatColumnLabel(it) }
    private fun monthLabels() = months.map { BirthdayDateHelper.formatColumnLabel(it) }
    private fun yearLabels() = years.map { it.toString() }

    private fun confirmSelection() {
        val year = years.getOrNull(selectedYearIndex) ?: return
        val month = months.getOrNull(selectedMonthIndex) ?: return
        val day = days.getOrNull(selectedDayIndex) ?: return
        val item = BirthdayDateHelper.compose(day, month, year)
        if (item == null) {
            ToastUtil.showLong(mContext, "Invalid date")
            return
        }
        onBirthdaySelectListener.onSelect(item.formValue, item.displayText)
        dismiss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            com.credit.bridge.R.id.dismissIv -> dismiss()
            com.credit.bridge.R.id.confirmTv -> confirmSelection()
        }
    }
}
