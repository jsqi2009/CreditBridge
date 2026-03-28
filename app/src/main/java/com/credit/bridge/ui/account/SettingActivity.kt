package com.credit.bridge.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityAboutUsBinding
import com.credit.bridge.databinding.ActivitySettingBinding
import com.credit.bridge.ui.product.SubmitSuccessActivity

class SettingActivity : BaseActivity<ActivitySettingBinding>(), View.OnClickListener {
    override fun getBinding() = ActivitySettingBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    override fun initRes() {
        super.initRes()
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backIv.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.titleTv -> {
                startActivity(Intent(this, SubmitSuccessActivity::class.java))
            }
        }
    }
}