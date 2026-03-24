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
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.ui.RootActivity
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.widget.CommonBottomSheet
import com.credit.bridge.widget.VerifyBankBottomSheet
import kotlin.collections.get

class LoginActivity : BaseActivity<ActivityLoginBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityLoginBinding.inflate(layoutInflater)

    private var workTypeIndex =  -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.sendTv.paint.isUnderlineText = true
        bindViews.verifyVoiceTv.paint.isUnderlineText = true

        bindViews.loginTv.setOnClickListener(this)
        bindViews.sendTv.setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.loginTv -> {
                //startActivity(Intent(this@LoginActivity, RootActivity::class.java))

                val bankVerifyBottomSheet = VerifyBankBottomSheet(
                    this, object : OnConfirmListener {
                        override fun onClick(info: String) {
                            /*showLoading()
                            HttpClient.postBankInfo(this@EditBankActivity,views.tvName.text.toString(),
                                views.etNewNumber.text.toString().replace(" ",""),
                                views.etNewNumberRe.text.toString().replace(" ",""),
                                views.etCode.text.toString().replace(" ",""),
                                code)*/
                        }
                    },"123","7777777")
                bankVerifyBottomSheet?.show(supportFragmentManager, "")
            }
            R.id.sendTv -> {
                showWorkTypeSheet()
            }
        }
    }

    fun showWorkTypeSheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Employment Status",VerifyInfoUtil.getWorkTypeList(),
            workTypeIndex, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    ToastUtil.showShort(this@LoginActivity, "Select: $index")
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }
}