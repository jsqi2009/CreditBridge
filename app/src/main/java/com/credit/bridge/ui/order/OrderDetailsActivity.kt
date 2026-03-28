package com.credit.bridge.ui.order

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
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.ui.RootActivity
import com.credit.bridge.util.ToastUtil

class OrderDetailsActivity : BaseActivity<ActivityOrderDetailsBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityOrderDetailsBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.text = "Details"

        bindViews.titleLayout.backIv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.editBankTv.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.titleTv -> {
                ToastUtil.showShort(this, "Right")
            }
            R.id.editBankTv -> {
                startActivity(Intent(this@OrderDetailsActivity, EditCardActivity::class.java))
            }
        }
    }
}