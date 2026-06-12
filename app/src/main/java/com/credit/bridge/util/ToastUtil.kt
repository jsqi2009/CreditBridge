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
 */
class ToastUtil private constructor() {

    init {
        throw kotlin.UnsupportedOperationException("no body")
    }

    companion object {
        private const val MSG_NETWORK_UNAVAILABLE =
            "Network connection failed, please check network settings"
        private const val MSG_NETWORK_TIMEOUT =
            "Request timed out, please try again"

        private var isShow = true

        private var mToast: Toast? = null

        private var toastView: View? = null

        private fun sanitizeMessage(message: CharSequence?): CharSequence? {
            if (message.isNullOrBlank()) return message

            val text = message.toString().trim()
            if (!looksLikeTechnicalError(text)) return message

            return when {
                text.contains("SocketTimeoutException", ignoreCase = true)
                    || text.contains("timeout", ignoreCase = true) ->
                    MSG_NETWORK_TIMEOUT

                text.contains("ConnectException", ignoreCase = true)
                    || text.contains("UnknownHostException", ignoreCase = true)
                    || text.contains("Failed to connect", ignoreCase = true)
                    || text.contains("Unable to resolve host", ignoreCase = true)
                    || text.startsWith("java.net.")
                    || text.startsWith("java.io.") ->
                    MSG_NETWORK_UNAVAILABLE

                else -> MSG_NETWORK_UNAVAILABLE
            }
        }

        private fun looksLikeTechnicalError(text: String): Boolean {
            return text.startsWith("java.")
                || text.startsWith("android.")
                || text.contains("Exception", ignoreCase = true)
        }

        fun controlShow(isShowToast: Boolean) {
            isShow = isShowToast
        }

        fun cancelToast() {
            if (isShow && mToast != null) {
                mToast!!.cancel()
            }
        }

        fun showShort(context: Context, message: CharSequence?) {
            if (message?.isEmpty() == true) {
                return
            }
            if (isShow) {
                val displayMessage = sanitizeMessage(message)
                if (mToast == null) {
                    mToast =
                        Toast.makeText(context.applicationContext, displayMessage, Toast.LENGTH_SHORT)
                    mToast!!.setGravity(Gravity.CENTER, 0, 0)
                } else {
                    mToast =
                        Toast.makeText(context.applicationContext, displayMessage, Toast.LENGTH_SHORT)
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
            if (message?.isEmpty() == true) {
                return
            }
            if (isShow) {
                val displayMessage = sanitizeMessage(message)
                if (mToast == null) {
                    mToast =
                        Toast.makeText(context.applicationContext, displayMessage, Toast.LENGTH_LONG)
                } else {
                    mToast =
                        Toast.makeText(context.applicationContext, displayMessage, Toast.LENGTH_LONG)
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
                val displayMessage = sanitizeMessage(message)
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, displayMessage, duration)
                } else {
                    mToast!!.setText(displayMessage)
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

        fun customToastView(context: Context, message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
            if (message?.isEmpty() == true) {
                return
            }
            if (isShow) {
                val displayMessage = sanitizeMessage(message)
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, displayMessage, duration)
                }
                if (toastView == null) {
                    toastView =
                        LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null)
                }
                (toastView!!.findViewById<View?>(R.id.tvToast) as TextView).text = displayMessage
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
                val displayMessage = sanitizeMessage(message)
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, displayMessage, duration)
                } else {
                    mToast!!.setText(displayMessage)
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
                val displayMessage = sanitizeMessage(message)
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, displayMessage, duration)
                } else {
                    mToast!!.setText(displayMessage)
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
                val displayMessage = sanitizeMessage(message)
                if (mToast == null) {
                    mToast = Toast.makeText(context.applicationContext, displayMessage, duration)
                } else {
                    mToast!!.setText(displayMessage)
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