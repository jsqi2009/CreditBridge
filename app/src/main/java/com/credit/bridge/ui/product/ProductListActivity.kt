package com.credit.bridge.ui.product

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.databinding.ActivityProductListBinding
import com.credit.bridge.util.ToastUtil

class ProductListActivity : BaseActivity<ActivityProductListBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityProductListBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.amountSlider.apply {
            min = 1000f
            max = 2000f
            step = 100f
            setUnit("₹")
            value = 1200f

            setOnValueChangeListener {
                ToastUtil.showShort(this@ProductListActivity, it.toString())
            }
        }
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