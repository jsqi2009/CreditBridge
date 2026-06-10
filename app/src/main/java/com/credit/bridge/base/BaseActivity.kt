package com.credit.bridge.base

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.credit.bridge.content.AndroidBus
import com.credit.bridge.ui.App
import com.credit.bridge.util.AppActivityManager
import com.credit.bridge.widget.GlobalLoading


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var bindViews: VB
    lateinit var eventBus: AndroidBus
    //loading
    private var loadingDialog: GlobalLoading? = null

    private val contentLayout by lazy {
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            doBeforeSetContentView()
            bindViews = getBinding()
            initStatus()

            initRes()

            val titleRes = getTitleRes()
            if (titleRes != -1) {
                supportActionBar?.let {
                    it.setTitle(titleRes)
                } ?: run {
                    setTitle(titleRes)
                }
            }
            setContentView(bindViews.root)

            eventBus = App[this].eventBus
            this.eventBus.register(this)
            AppActivityManager.appManager.addActivity(this)
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun setContentView(layoutResID: Int) {
        layoutInflater.inflate(layoutResID, contentLayout)
    }

    private fun initStatus() {
        val option: Int = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
        supportActionBar?.hide()
    }

    fun Activity.restart() {
        finish()
        startActivity(intent)
    }

    abstract fun getBinding(): VB

    open fun doBeforeSetContentView() = Unit

    open fun initRes() = Unit

    @StringRes
    open fun getTitleRes() = -1

    @MenuRes
    open fun getMenuRes() = -1


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuRes = getMenuRes()

        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }

    protected fun showLoading() {
        if (isFinishing || isDestroyed) return
        val fm = supportFragmentManager
        if (fm.findFragmentByTag(GlobalLoading.TAG) != null) return
        if (loadingDialog?.isAdded == true) return
        if (loadingDialog != null) return

        loadingDialog = GlobalLoading.newInstance()
        loadingDialog?.safeShow(fm)
    }

    protected fun hideLoading() {
        loadingDialog?.safeDismiss()
        loadingDialog = null
    }


    override fun onDestroy() {
        eventBus.unregister(this)
        AppActivityManager.appManager.removeActivity(this)
        super.onDestroy()
    }


}