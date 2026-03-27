package com.credit.bridge.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.credit.bridge.R
import androidx.core.graphics.drawable.toDrawable

/**
 * author : Jason
 * desc   :
 */
object DialogUtil {

    fun showVoiceVerifyDialog(mContext: Context, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) {
        val customPopup = Dialog(mContext)
        customPopup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customPopup.setContentView(R.layout.dialog_voice_verify)
        val tvConfirm = customPopup.findViewById<TextView>(R.id.tvConfirm)
        val tvCancel = customPopup.findViewById<TextView>(R.id.tvCancel)
        customPopup.window?.apply {
            decorView.setBackgroundResource(android.R.color.transparent)
            setBackgroundDrawable(mContext.resources.getColor(R.color.dialog_bg_black, null).toDrawable())
            val params = attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.gravity = Gravity.CENTER
            attributes = params
        }

        tvCancel.setOnClickListener {
            customPopup.dismiss()
            onCancel?.invoke()
        }
        tvConfirm.setOnClickListener {
            customPopup.dismiss()
            onConfirm.invoke()
        }
        customPopup.setCancelable(false)
        customPopup.setCanceledOnTouchOutside(false)
        customPopup.show()
    }
}