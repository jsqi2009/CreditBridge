package com.credit.bridge.content

import android.os.Handler
import android.os.Looper

import com.squareup.otto.Bus


class AndroidBus : Bus() {

    private val mHandlerMain = Handler(Looper.getMainLooper())

    override fun post(event: Any) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event)
        } else {
            this.mHandlerMain.post { post(event) }
        }
    }
}