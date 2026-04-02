package com.credit.bridge.ui.verify

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.databinding.ActivityVerifyInfoBinding
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.body.RequestContactBody
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.widget.CommonBottomSheet
import com.credit.bridge.widget.StartVerifyBottomSheet
import com.credit.bridge.widget.VerifyBankBottomSheet

class VerifyInfoActivity : BaseActivity<ActivityVerifyInfoBinding>(), View.OnClickListener {


    override fun getBinding() = ActivityVerifyInfoBinding.inflate(layoutInflater)

    private var currentStep = 1
    private var isPanVerifySuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.text = "Details"
        bindViews.titleLayout.rightTv.text = "1/5"
        bindViews.titleLayout.rightTv.visibility = View.VISIBLE

        bindViews.titleLayout.backIv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.continueTv.setOnClickListener(this)

        bindViews.retryTv.paint.isUnderlineText = true

        bindViews.verify1.root.visibility = View.VISIBLE
        bindViews.verify2.root.visibility = View.GONE
        bindViews.verify3.root.visibility = View.GONE
        bindViews.verify4.root.visibility = View.GONE
        bindViews.verify5.root.visibility = View.GONE
        bindViews.verifyTipsLayout.visibility = View.GONE
        bindViews.stepBtn4.visibility = View.GONE
        bindViews.attemptLeftTv.visibility = View.GONE
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
               //showStartVerifySheet()
                handleStepOperation()
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

    private fun handleStepOperation() {
        when (currentStep) {
            1 -> {
                verifyBaseUserAction()
            }
            2 -> {
                verifyContactAction()
            }
            3 -> {
                verifyBankAction()
            }
        }
    }

    private fun verifyBaseUserAction() {
        /*val eventValue =  HashMap<String, Any>()
        eventValue[ConstConfig.POINT_INFO_SUBMIT] = ""
        AppsFlyerLib.getInstance().logEvent(this, ConstConfig.POINT_INFO_SUBMIT, eventValue)
        PointUploadUtils.uploadEvent(this,ConstConfig.POINT_ACTION_TYPE_CLICK,ConstConfig.POINT_INFO_SUBMIT)*/

        ToastUtil.showLong(this, "Please select your employment status")
        return

        /*if (monthlyIndex == -1) {
            ToastUtil.showLong(this, "Please select your monthly disposable income")
            return@setOnClickListener
        }
        if (educationIndex == -1) {
            ToastUtil.showLong(this, "Please select your highest education level")
            return@setOnClickListener
        }
        if (maritalIndex == -1) {
            ToastUtil.showLong(this, "Please select your marital status")
            return@setOnClickListener
        }
        if (dependentsIndex == -1) {
            ToastUtil.showLong(this, "Please select the number of dependents")
            return@setOnClickListener
        }
        if (views.tvEmail.text.isEmpty() || !views.tvEmail.text.contains("@")) {
            ToastUtil.showLong(this, "Please enter a valid email address")
            return@setOnClickListener
        }
        if (views.tvWhatsApp.text.isEmpty()) {
            ToastUtil.showLong(this, "Please enter your WhatsApp number")
            return@setOnClickListener
        }*/

        showLoading()
        HttpClient.verifyBaseUserInfo(
            this,
            "",
            "",
            "",
            "oneEducationList[educationIndex]",
            "oneMaritalList[maritalIndex]",
            "oneMonthlyList[monthlyIndex]",
            "views.tvWhatsApp.text.toString()",
        )
    }

    private fun verifyContactAction() {
        /*val eventValue =  HashMap<String, Any>()
        eventValue[ConstConfig.POINT_INFO_SUBMIT] = ""
        AppsFlyerLib.getInstance().logEvent(this, ConstConfig.POINT_INFO_SUBMIT, eventValue)
        PointUploadUtils.uploadEvent(this,ConstConfig.POINT_ACTION_TYPE_CLICK,ConstConfig.POINT_INFO_SUBMIT)*/

        var contactList = arrayListOf<RequestContactBody>()
        /*val one = RequestContactBody()
        one.mngspckl = oneContactList[oneContactIndex]
        one.jhov = views.tvOneName.text.toString()
        one.sucbzl = views.tvOnePhone.text.toString()
        val two = RequestContactBody()
        two.mngspckl = twoContactList[twoContactIndex]
        two.jhov = views.tvTwoName.text.toString()
        two.sucbzl = views.tvTwoPhone.text.toString()
        contactList.add(one)
        contactList.add(two)*/

        showLoading()
        HttpClient.verifyContactInfo(this,contactList,)
    }

    private fun verifyBankAction() {
        /*val eventValue =  HashMap<String, Any>()
        eventValue[ConstConfig.POINT_BANKCARD_SUBMIT] = ""
        AppsFlyerLib.getInstance().logEvent(this, ConstConfig.POINT_BANKCARD_SUBMIT, eventValue)
        PointUploadUtils.uploadEvent(this,ConstConfig.POINT_ACTION_TYPE_CLICK,ConstConfig.POINT_BANKCARD_SUBMIT)*/

        showLoading()
        HttpClient.verifyBankInfo(this, "", "","", "","")
    }

}