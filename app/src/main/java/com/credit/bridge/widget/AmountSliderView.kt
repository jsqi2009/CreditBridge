package com.credit.bridge.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import com.credit.bridge.databinding.ViewAmountSliderBinding
import com.google.android.material.slider.Slider

class AmountSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding =
        ViewAmountSliderBinding.inflate(LayoutInflater.from(context), this, true)

    private var unit: String = "₹"

    var min: Float = 1000f
        set(value) {
            field = value
            binding.slider.valueFrom = value
            binding.tvMin.text = format(value)
        }

    var max: Float = 5000f
        set(value) {
            field = value
            binding.slider.valueTo = value
            binding.tvMax.text = format(value)
        }

    var step: Float = 100f
        set(value) {
            field = value
            binding.slider.stepSize = value
        }

    var value: Float
        get() = binding.slider.value
        set(v) {
            binding.slider.value = v
            binding.tvValue.text = format(v)
            binding.tvValue.post { updateBubblePosition(v) }
        }

    private var onValueChanged: ((Float) -> Unit)? = null

    init {
        binding.slider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                binding.tvValue.text = format(value)
                binding.tvValue.post { updateBubblePosition(value) }
                onValueChanged?.invoke(value)
            }
        }

        binding.slider.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {

            override fun onStartTrackingTouch(slider: Slider) {
                binding.tvValue.visibility = VISIBLE
            }

            override fun onStopTrackingTouch(slider: Slider) {
                binding.tvValue.text = format(slider.value)
                binding.tvValue.post { updateBubblePosition(slider.value) }
            }
        })

        binding.slider.doOnLayout {
            updateBubblePosition(binding.slider.value)
        }
    }

    fun setUnit(unit: String) {
        this.unit = unit
        binding.tvMin.text = format(min)
        binding.tvMax.text = format(max)
        binding.tvValue.text = format(binding.slider.value)
    }

    fun setOnValueChangeListener(block: (Float) -> Unit) {
        onValueChanged = block
    }

    private fun updateBubblePosition(value: Float) {
        val slider = binding.slider
        val bubble = binding.tvValue

        val location = IntArray(2)
        slider.getLocationOnScreen(location)

        val sliderLeft = location[0]
        val sliderTop = location[1]

        val percent = (value - slider.valueFrom) /
                (slider.valueTo - slider.valueFrom)

        val trackWidth = slider.width - slider.paddingStart - slider.paddingEnd

        val thumbX = sliderLeft + slider.paddingStart + trackWidth * percent

        val bubbleX = thumbX - bubble.width / 2f
        val bubbleY = sliderTop - bubble.height - slider.thumbRadius

        bubble.x = bubbleX - sliderLeft
        bubble.y = (bubbleY - sliderTop).toFloat()
    }
    private fun format(v: Float): String {
        return "$unit${v.toInt()}"
    }
}