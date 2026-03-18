package com.credit.bridge.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.coroutineScope
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivitySplashBinding
import com.credit.bridge.ui.RootActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun getBinding() = ActivitySplashBinding.inflate(layoutInflater)

    private val Default_Time: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }

    override fun initRes() {
        super.initRes()
        lifecycle.coroutineScope.launch {
            delay(Default_Time)
            checkWhetherLogin()
        }
    }

    private fun checkWhetherLogin() {
        val isAuth: Boolean = CacheManager.isAuth
        val intent = if (isAuth) {
            Intent(this@SplashActivity, RootActivity::class.java)
        } else {
            Intent(this@SplashActivity, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}