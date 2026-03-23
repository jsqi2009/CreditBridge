package com.credit.bridge.ui.order

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityEditCardBinding
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.util.ToastUtil

class EditCardActivity : BaseActivity<ActivityEditCardBinding>(), View.OnClickListener {


    override fun getBinding() = ActivityEditCardBinding.inflate(layoutInflater)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

override fun initRes() {
    super.initRes()

    bindViews.titleLayout.backIv.setOnClickListener(this)
    bindViews.titleLayout.titleTv.setOnClickListener(this)
    bindViews.titleLayout.titleTv.text = "Details"
}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.titleTv -> {
                ToastUtil.showShort(this, "Right")
            }
        }
    }
}