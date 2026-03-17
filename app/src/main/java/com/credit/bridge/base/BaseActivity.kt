package com.flex.line.balance.usage.control.base

import android.app.Activity
import android.app.ActivityManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewbinding.ViewBinding
import com.credit.bridge.content.AndroidBus
import com.credit.bridge.ui.App
import com.credit.bridge.util.AppActivityManager
import com.credit.bridge.widget.GlobalLoading


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var views: VB
    lateinit var myBus: AndroidBus
    //loading
    private var loadingDialog: GlobalLoading? = null

    private var isLoadingShowing = false
    private val contentLayout by lazy {
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            doBeforeSetContentView()
            views = getBinding()
            initNei()

            initAll()

            val titleRes = getTitleRes()
            if (titleRes != -1) {
                supportActionBar?.let {
                    it.setTitle(titleRes)
                } ?: run {
                    setTitle(titleRes)
                }
            }
            setContentView(views.root)

            myBus = App[this].myBus
            this.myBus.register(this)
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

    private fun initNei() {
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

    open fun initAll() = Unit

    @StringRes
    open fun getTitleRes() = -1

    @MenuRes
    open fun getMenuRes() = -1

    open fun getCoordinatorLayout(): CoordinatorLayout? = null



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuRes = getMenuRes()

        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
        } catch (e: Exception) {

        }
    }

    protected fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = GlobalLoading.newInstance()
        }
        loadingDialog?.safeShow(getSupportFragmentManager())
    }

    protected fun hideLoading() {
        if (loadingDialog != null) {
            loadingDialog?.safeDismiss()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        myBus.unregister(this)
        AppActivityManager.appManager.finishActivity(this);
    }


}