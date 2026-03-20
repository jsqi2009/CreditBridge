package com.credit.bridge.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.credit.bridge.R

/**
 * @author wangwentao
 * @date 2017/1/25
 */
class ToastUtil private constructor() {

    init {
        throw kotlin.UnsupportedOperationException("no body")
    }

    companion object {
        private var isShow = true

        private var mToast: Toast? = null

        private var toastView: View? = null

        fun controlShow(isShowToast: Boolean) {
            isShow = isShowToast
        }

        fun cancelToast() {
            if (isShow && mToast != null) {
                mToast!!.cancel()
            }
        }

        fun showShort(context: Context, message: CharSequence?) {
            if (isShow) {
                if (mToast == null) {
                    mToast =
                        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
                    mToast!!.setGravity(Gravity.CENTER, 0, 0)
                } else {
                    mToast =
                        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
                    mToast!!.setGravity(Gravity.CENTER, 0, 0)
                }
                mToast!!.show()
            }
        }

        fun showShort(context: Context, resId: Int) {
            if (isShow) {
                if (mToast == null) {
                    mToast =
                        Toast.makeText(context.applicationContext, resId, Toast.LENGTH_SHORT)
                    mToast!!.setGravity(Gravity.CENTER, 0, 0)
                } else {
                    mToast =
                        Toast.makeText(context.applicationContext, resId, Toast.LENGTH_SHORT)
                    mToast!!.setGravity(Gravity.CENTER, 0, 0)
                }
                mToast!!.show()
            }
        }

        fun showLong(context: Context, message: CharSequence?) {
            if (isShow) {
                if (mToast == null) {
                    mToast =
                        Toast.makeText(context.applicationContext, message, Toast.LENGTH_LONG)
                } else {
                    mToast =
                        Toast.makeText(context.applicationContext, message, Toast.LENGTH_LONG)
                }
                mToast!!.setGravity(Gravity.CENTER, 0, 0)
                mToast!!.show()
            }
        }

        fun showLong(context: Context, resId: Int) {
            if (isShow) {
                mToast = Toast.makeText(context.applicationContext, resId, Toast.LENGTH_LONG)
                mToast!!.setGravity(Gravity.CENTER, 0, 0)
                mToast!!.show()
            }
        }

        fun show(context: Context, message: CharSequence?, duration: Int) {
            if (isShow) {
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, message, duration)
                } else {
                    mToast!!.setText(message)
                }
                mToast!!.setGravity(Gravity.CENTER, 0, 0)
                mToast!!.show()
            }
        }

        fun show(context: Context, resId: Int, duration: Int) {
            if (isShow) {
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, resId, duration)
                } else {
                    mToast!!.setText(resId)
                }
                mToast!!.setGravity(Gravity.CENTER, 0, 0)
                mToast!!.show()
            }
        }

        fun customToastView(context: Context, message: CharSequence?, duration: Int) {
            if (isShow) {
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, message, duration)
                }
                if (toastView == null) {
                    toastView =
                        LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null)
                }
                (toastView!!.findViewById<View?>(R.id.tvToast) as TextView).text = message
                if (toastView != null) {
                    mToast!!.setView(toastView)
                }
                mToast!!.setGravity(Gravity.CENTER, 0, 0)
                mToast!!.show()
            }
        }

        fun customToastGravity(
            context: Context,
            message: CharSequence?,
            duration: Int,
            gravity: Int,
            xOffset: Int,
            yOffset: Int
        ) {
            if (isShow) {
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, message, duration)
                } else {
                    mToast!!.setText(message)
                }
                mToast!!.setGravity(gravity, xOffset, yOffset)
                mToast!!.show()
            }
        }

        fun showToastWithImageAndText(
            context: Context,
            message: CharSequence?,
            iconResId: Int,
            duration: Int,
            gravity: Int,
            xOffset: Int,
            yOffset: Int
        ) {
            if (isShow) {
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, message, duration)
                } else {
                    mToast!!.setText(message)
                }
                mToast!!.setGravity(gravity, xOffset, yOffset)
                val toastView = mToast!!.getView() as LinearLayout?
                val imageView = ImageView(context)
                imageView.setImageResource(iconResId)
                toastView!!.addView(imageView, 0)
                mToast!!.show()
            }
        }

        fun customToastAll(
            context: Context,
            message: CharSequence?,
            duration: Int,
            view: View?,
            isGravity: Boolean,
            gravity: Int,
            xOffset: Int,
            yOffset: Int,
            isMargin: Boolean,
            horizontalMargin: Float,
            verticalMargin: Float
        ) {
            if (isShow) {
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, message, duration)
                } else {
                    mToast!!.setText(message)
                }
                if (view != null) {
                    mToast!!.setView(view)
                }
                if (isMargin) {
                    mToast!!.setMargin(horizontalMargin, verticalMargin)
                }
                if (isGravity) {
                    mToast!!.setGravity(gravity, xOffset, yOffset)
                }
                mToast!!.show()
            }
        }

        fun customToastAll(
            context: Context,
            resId: Int,
            duration: Int,
            view: View?,
            isGravity: Boolean,
            gravity: Int,
            xOffset: Int,
            yOffset: Int,
            isMargin: Boolean,
            horizontalMargin: Float,
            verticalMargin: Float
        ) {
            if (isShow) {
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, resId, duration)
                } else {
                    mToast!!.setText(resId)
                }
                if (view != null) {
                    mToast!!.setView(view)
                }
                if (isMargin) {
                    mToast!!.setMargin(horizontalMargin, verticalMargin)
                }
                if (isGravity) {
                    mToast!!.setGravity(gravity, xOffset, yOffset)
                }
                mToast!!.show()
            }
        }
    }
}