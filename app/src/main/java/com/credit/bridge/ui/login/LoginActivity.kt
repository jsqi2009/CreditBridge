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
import com.credit.bridge.widget.VerifyBankBottomSheet

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
                //startActivity(Intent(this@LoginActivity, RootActivity::class.java))

                var bankVerifyBottomSheet = VerifyBankBottomSheet(
                    this,
                    object : VerifyBankBottomSheet.Listener {
                        override fun click(code :String) {
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
        }
    }
}