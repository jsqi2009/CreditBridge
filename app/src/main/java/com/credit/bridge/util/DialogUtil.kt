package com.credit.bridge.util

import android.app.Dialog
import android.content.Context
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
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.response.BResponse
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

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
        val descTv = customPopup.findViewById<TextView>(R.id.descTv)
        val tvConfirm = customPopup.findViewById<TextView>(R.id.tvConfirm)
        val tvCancel = customPopup.findViewById<TextView>(R.id.tvCancel)
        val ivGif = customPopup.findViewById<ImageView>(R.id.ivGif)
        Glide.with(mContext)
            .asGif()
            .load(R.raw.gif_process)
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

        val timer = object : CountDownTimer(6000,1000) {

            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                countdownTv.text = sec.toString()
            }

            override fun onFinish() {
                Glide.with(mContext)
                    .asGif()
                    .load(R.raw.gif_success)
                    .into(ivGif)
                countdownTv.visibility = View.GONE
                descTv.text = "Credit limit refreshed. It will be available in a moment."
                // customPopup.dismiss()
                // onConfirm()

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        val call = HttpClient.executeRecredit2(mContext)
                        call.enqueue(object : retrofit2.Callback<BResponse> {
                            override fun onResponse(
                                call: retrofit2.Call<BResponse?>,
                                response: Response<BResponse?>
                            ) {
                                if (response.body()?.fzpn == 200) {
                                    customPopup.dismiss()
                                    onConfirm.invoke()
                                }
                            }

                            override fun onFailure(
                                call: retrofit2.Call<BResponse?>,
                                t: Throwable
                            ) {
                            }

                        })
                    }
                }, 3000)

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

    fun showRequestPermissionDialog(mContext: Context, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) {
        val customPopup = Dialog(mContext)
        customPopup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customPopup.setContentView(R.layout.dialog_request_permission)
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