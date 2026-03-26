package com.credit.bridge.ui.verify

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.databinding.ActivityVerifyInfoBinding
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.widget.CommonBottomSheet
import com.credit.bridge.widget.StartVerifyBottomSheet
import com.credit.bridge.widget.VerifyBankBottomSheet

class VerifyInfoActivity : BaseActivity<ActivityVerifyInfoBinding>(), View.OnClickListener {


    override fun getBinding() = ActivityVerifyInfoBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.backIv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.continueTv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.text = "Details"

        bindViews.retryTv.paint.isUnderlineText = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.titleTv -> {
                ToastUtil.showShort(this, "Right")
            }
            R.id.continueTv -> {
               showStartVerifySheet()
            }
        }
    }


    private fun showStartVerifySheet() {
       /* val bankVerifyBottomSheet = VerifyBankBottomSheet(
            this, object : OnConfirmListener {
                override fun onClick(info: String) {

                }
            }, "123", "7777777"
        )
        bankVerifyBottomSheet?.show(supportFragmentManager, "")*/

        val workTypeSheet = StartVerifyBottomSheet(
            this,"Employment Status",VerifyInfoUtil.getWorkTypeList(),
            -1, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    ToastUtil.showShort(this@VerifyInfoActivity, "Select: $index")
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

}