package com.credit.bridge.ui.order

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

class OrderDetailsActivity : BaseActivity<ActivityOrderDetailsBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityOrderDetailsBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()
    }

    override fun onClick(v: View?) {
    }
}