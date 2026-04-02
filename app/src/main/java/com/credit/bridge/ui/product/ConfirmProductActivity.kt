package com.credit.bridge.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityConfirmProductBinding
import com.credit.bridge.databinding.ActivityProductListBinding
import com.credit.bridge.remote.bean.ProductInfo
import com.credit.bridge.util.ToastUtil

class ConfirmProductActivity : BaseActivity<ActivityConfirmProductBinding>(), View.OnClickListener  {

    override fun getBinding() = ActivityConfirmProductBinding.inflate(layoutInflater)

    var productIdList: ArrayList<Int> = ArrayList<Int>()
    var amountList: ArrayList<Int> = ArrayList<Int>()
    private var totalAmount: Any = 0
    private var totalFee: Any = 0
    private var productInfo: ProductInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    override fun initRes() {
        super.initRes()

        try {
            productIdList = intent.getIntegerArrayListExtra("productIdList")!!
            amountList = intent.getIntegerArrayListExtra("productAmountList")!!
            totalAmount = intent.getIntExtra("amount", 0)
            totalFee = intent.getIntExtra("fee", 0)
            productInfo = intent.getSerializableExtra("productInfo") as ProductInfo
        } catch (e: Exception) {

        }

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