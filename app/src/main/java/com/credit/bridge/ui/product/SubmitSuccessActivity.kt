package com.credit.bridge.ui.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityConfirmProductBinding
import com.credit.bridge.databinding.ActivitySubmitSuccessBinding

class SubmitSuccessActivity : BaseActivity<ActivitySubmitSuccessBinding>(), View.OnClickListener {

    override fun getBinding() = ActivitySubmitSuccessBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    override fun initRes() {
        super.initRes()
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backIv.setOnClickListener(this)

        bindViews.starView.setRating(3)
        bindViews.starView.onRatingChange = { rating ->
            Log.d("Star", "当前评分: $rating")
        }
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