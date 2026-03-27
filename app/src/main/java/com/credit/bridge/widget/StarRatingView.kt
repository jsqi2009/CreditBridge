package com.credit.bridge.widget

/**
 * author : Jason
 * date   : 2026/3/27 22:30
 * desc   :
 */

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.credit.bridge.R

class StarRatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val stars = mutableListOf<ImageView>()
    private var rating = 0

    var onRatingChange: ((Int) -> Unit)? = null

    init {
        orientation = HORIZONTAL

        repeat(5) { index ->
            val star = ImageView(context).apply {
                layoutParams = LayoutParams(dp(40), dp(40)).apply {
                    marginStart = dp(8)
                    marginEnd = dp(8)
                }
                setImageResource(R.mipmap.ic_star)
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    val value = index + 1
                    setRating(value)
                    onRatingChange?.invoke(value)
                }
            }
            stars.add(star)
            addView(star)
        }
    }

    fun setRating(value: Int) {
        rating = value.coerceIn(0, 5)
        updateUI()
    }

    fun getRating(): Int = rating

    private fun updateUI() {
        stars.forEachIndexed { index, imageView ->
            if (index < rating) {
                imageView.setImageResource(R.mipmap.ic_star_selected)
            } else {
                imageView.setImageResource(R.mipmap.ic_star)
            }
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}