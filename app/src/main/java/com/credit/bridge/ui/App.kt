package com.credit.bridge.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.content.AndroidBus
import com.credit.bridge.util.GoogleAdUtils
import com.credit.bridge.util.SPUtil
import com.liveness.dflivenesslibrary.DFProductResult
import com.liveness.dflivenesslibrary.DFTransferResultInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * author : Jason
 * date   : 2026/3/17 23:03
 * desc   :
 */
class App : Application() , DFTransferResultInterface {

    lateinit var eventBus: AndroidBus

    var googleAdIdResult: GoogleAdUtils.GoogleAdIDResult? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        SPUtil.init(this)
        eventBus = AndroidBus()
        //HttpClient.init(this, myBus)
        eventBus.register(this)

        initFlyer()
        getGoogleAd()
    }

    private fun initFlyer() {
        AppsFlyerLib.getInstance().init("CjXpBDqDEEDA2TthYp7HgV", object :
            AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: Map<String, Any>) {
                val afChannel = data["af_channel"]?.toString() ?: ""
                CacheManager.afChannel = afChannel
                Log.d(TAG, "onConversionDataSuccess===$data")
            }

            override fun onConversionDataFail(p0: String?) {
                Log.d(TAG, "onConversionDataFail====")
            }

            override fun onAppOpenAttribution(p0: Map<String?, String?>?) {
                Log.d(TAG, "onAppOpenAttribution===$p0")
            }

            override fun onAttributionFailure(p0: String?) {
                Log.d(TAG, "onAttributionFailure===$p0")
            }

        }, this)
        AppsFlyerLib.getInstance().start(this)
    }

    private fun getGoogleAd() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                googleAdIdResult = GoogleAdUtils.getGoogleAdId(applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig);
    }

    private lateinit var mResult: DFProductResult

    public override fun setResult(result: DFProductResult) {
        mResult = result
    }

    public override fun getResult(): DFProductResult {
        return mResult
    }

    companion object {

        var TAG = "App"

        @Volatile
        lateinit var instance: App
            private set

        operator fun get(content: Context): App {
            return content.applicationContext as App
        }

        val context: Context
            get() = instance.applicationContext
    }


}