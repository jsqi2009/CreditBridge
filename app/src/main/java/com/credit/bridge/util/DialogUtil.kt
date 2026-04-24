package com.credit.bridge.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.credit.bridge.R
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide

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

    fun showVerifyTipsDialog(mContext: Context, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) {
        val customPopup = Dialog(mContext)
        customPopup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customPopup.setContentView(R.layout.dialog_verify_tips)
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

    fun showVerifySuccessDialog(mContext: Context, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) {
        val customPopup = Dialog(mContext)
        customPopup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customPopup.setContentView(R.layout.dialog_verify_success)
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

    fun showLogoutDialog(mContext: Context, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) {
        val customPopup = Dialog(mContext)
        customPopup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customPopup.setContentView(R.layout.dialog_logout)
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

    fun showRecreditNeededDialog(mContext: Context, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) {
        val customPopup = Dialog(mContext)
        customPopup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customPopup.setContentView(R.layout.dialog_recredit_needed)
        val countdownTv = customPopup.findViewById<TextView>(R.id.countdownTv)
        val tvConfirm = customPopup.findViewById<TextView>(R.id.tvConfirm)
        val tvCancel = customPopup.findViewById<TextView>(R.id.tvCancel)
        val ivGif = customPopup.findViewById<ImageView>(R.id.ivGif)
        Glide.with(mContext)
            .asGif()
            .load(R.raw.ic_puroair)
            .into(ivGif)
        customPopup.window?.apply {
            decorView.setBackgroundResource(android.R.color.transparent)
            setBackgroundDrawable(mContext.resources.getColor(R.color.dialog_bg_black, null).toDrawable())
            val params = attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.gravity = Gravity.CENTER
            attributes = params
        }

        val timer = object : CountDownTimer(5000,1000) {

            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                countdownTv.text = sec.toString()
            }

            override fun onFinish() {
                tvConfirm.text = "Continue"
                tvConfirm.isEnabled = true
                // customPopup.dismiss()
                // onConfirm()
            }
        }

        timer.start()

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