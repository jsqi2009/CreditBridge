package com.credit.bridge.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityLoginBinding
import com.credit.bridge.databinding.ActivitySplashBinding
import com.credit.bridge.ui.RootActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityLoginBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.sendTv.paint.isUnderlineText = true
        bindViews.verifyVoiceTv.paint.isUnderlineText = true

        bindViews.loginTv.setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.loginTv -> {
                startActivity(Intent(this@LoginActivity, RootActivity::class.java))
            }
        }
    }
}