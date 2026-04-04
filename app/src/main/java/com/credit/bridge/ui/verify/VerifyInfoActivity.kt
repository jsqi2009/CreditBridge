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
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.databinding.ActivityVerifyInfoBinding
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.body.RequestContactBody
import com.credit.bridge.remote.event.OssInfoFaceResponseEvent
import com.credit.bridge.remote.event.OssInfoResponseEvent
import com.credit.bridge.remote.event.VerifyBaseUserInfoResponseEvent
import com.credit.bridge.util.AppUtil.formatSubString
import com.credit.bridge.util.ImageUploader
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.widget.CommonBottomSheet
import com.credit.bridge.widget.StartVerifyBottomSheet
import com.credit.bridge.widget.VerifyBankBottomSheet
import com.squareup.otto.Subscribe
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException
import java.io.File
import kotlin.collections.get

class VerifyInfoActivity : BaseActivity<ActivityVerifyInfoBinding>(), View.OnClickListener {


    override fun getBinding() = ActivityVerifyInfoBinding.inflate(layoutInflater)

    private var currentStep = 1
    private var isPanVerifySuccess = false
    private var workTypeIndex = -1
    private var monthlyIncomeIndex = -1
    private var educationIndex = -1
    private var maritalIndex = -1
    private var numberOfChildIndex = -1

    var real_path = ""

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
        bindViews.verify1.workStatusLl.setOnClickListener(this)
        bindViews.verify1.incomeLl.setOnClickListener(this)
        bindViews.verify1.educationStatusLl.setOnClickListener(this)
        bindViews.verify1.maritalStatusLl.setOnClickListener(this)
        bindViews.verify1.numberOfChildrenLl.setOnClickListener(this)

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
            R.id.workStatusLl -> {
                showWorkTypeSheet()
            }
            R.id.incomeLl -> {
                showStartVerifySheet()
            }
            R.id.educationStatusLl -> {
                showStartVerifySheet()
            }
            R.id.maritalStatusLl -> {
                showStartVerifySheet()
            }
            R.id.numberOfChildrenLl -> {
                showStartVerifySheet()
            }
            R.id.continueTv -> {
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
            4 -> {
                verifyPanAction()
            }
        }
    }


    private fun refreshUI() {
        when (currentStep) {
            1 -> {
                bindViews.verify1.root.visibility = View.VISIBLE

                bindViews.titleLayout.rightTv.text = "1/5"

                HttpClient.eventReport(this,ConstConfig.POINT_INTO_INFO,
                    ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_INTO_INFO)
            }
            2 -> {
                bindViews.verify1.root.visibility = View.GONE
                bindViews.verify2.root.visibility = View.VISIBLE

                bindViews.titleLayout.rightTv.text = "2/5"

                HttpClient.eventReport(this,ConstConfig.POINT_CONTACT_INPUT,
                    ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_CONTACT_INPUT)
            }
            3 -> {
                bindViews.verify2.root.visibility = View.GONE
                bindViews.verify3.root.visibility = View.VISIBLE

                bindViews.titleLayout.rightTv.text = "3/5"

                HttpClient.eventReport(this,ConstConfig.POINT_BANKCARD_INPUT,
                    ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_BANKCARD_INPUT)
            }
            4 -> {
                bindViews.verify3.root.visibility = View.GONE
                bindViews.verify4.root.visibility = View.VISIBLE

                bindViews.titleLayout.rightTv.text = "4/5"

                HttpClient.eventReport(this,ConstConfig.POINT_IDCARD_INPUT,
                    ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_IDCARD_INPUT)
            }
            5 -> {
                bindViews.verify4.root.visibility = View.GONE
                bindViews.verify5.root.visibility = View.VISIBLE

                bindViews.titleLayout.rightTv.text = "5/5"

                HttpClient.eventReport(this,ConstConfig.POINT_INPUT_LIVENESS,
                    ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_INPUT_LIVENESS)

                HttpClient.verifyCcrFaceNumber(this@VerifyInfoActivity)
            }
        }
    }

    private fun verifyBaseUserAction() {

        if (workTypeIndex == -1) {
            ToastUtil.showLong(this, "Please select your employment status")
            return
        }
        if (monthlyIncomeIndex == -1) {
            ToastUtil.showLong(this, "Please select your monthly disposable income")
            return
        }
        if (educationIndex == -1) {
            ToastUtil.showLong(this, "Please select your highest education level")
            return
        }
        if (maritalIndex == -1) {
            ToastUtil.showLong(this, "Please select your marital status")
            return
        }
        if (numberOfChildIndex == -1) {
            ToastUtil.showLong(this, "Please select the number of dependents")
            return
        }
        if (bindViews.verify1.emailEt.text.isEmpty() || !bindViews.verify1.emailEt.text.contains("@")) {
            ToastUtil.showLong(this, "Please enter a valid email address")
            return
        }
        if (bindViews.verify1.whatsappEt.text.isEmpty()) {
            ToastUtil.showLong(this, "Please enter your WhatsApp number")
            return
        }

        HttpClient.eventReport(this,ConstConfig.POINT_INFO_SUBMIT,
            ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_INFO_SUBMIT)


        showLoading()
        HttpClient.verifyBaseUserInfo(this,
            VerifyInfoUtil.numOfChildrenFormatList[numberOfChildIndex],
            bindViews.verify1.emailEt.text.toString(),
            VerifyInfoUtil.workTypeFormatList[workTypeIndex],
            VerifyInfoUtil.educationFormatList[educationIndex],
            VerifyInfoUtil.maritalFormatList[maritalIndex],
            VerifyInfoUtil.monthlyIncomeFormatList[monthlyIncomeIndex],
            bindViews.verify1.whatsappEt.text.toString()
        )
    }

    @Subscribe
    fun onVerifyBaseUserInfoResponseEvent(event: VerifyBaseUserInfoResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            currentStep++
            refreshUI()
        } else {
            if(event.model == null){
                ToastUtil.showLong(this,event.networkError.toString())
            }else{
                if(event.model?.wuhi == 500){
                    ToastUtil.showLong(this,event.model?.znxbvyn)
                }
            }
        }
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

    private fun verifyPanAction() {
        /*val eventValue =  HashMap<String, Any>()
        eventValue[ConstConfig.POINT_BANKCARD_SUBMIT] = ""
        AppsFlyerLib.getInstance().logEvent(this, ConstConfig.POINT_BANKCARD_SUBMIT, eventValue)
        PointUploadUtils.uploadEvent(this,ConstConfig.POINT_ACTION_TYPE_CLICK,ConstConfig.POINT_BANKCARD_SUBMIT)*/

        showLoading()
        HttpClient.verifyPanInfo(this, "", "","", "")
    }


    @Subscribe
    fun onOssInfoResponseEvent(event: OssInfoResponseEvent) {
        if(event.isSuccess){
            event.model?.blvb?.let{
                ImageUploader.uploadImage(real_path,it,object:Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        hideLoading()
                        runOnUiThread {
                            ToastUtil.showLong(this@VerifyInfoActivity,"upload fail：${e.message}")
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        runOnUiThread {
                            val fileName = File(real_path).name
                            val ossImageUrl = "${it.dwr}$fileName"
                            HttpClient.verifyOcrPan(this@VerifyInfoActivity,ossImageUrl.formatSubString())
                            HttpClient.getUserCredit(this@VerifyInfoActivity)
                        }
                    }
                })
            }
        }else {
           hideLoading()
            ToastUtil.showLong(this,event.networkError.toString())
        }

    }

    @Subscribe
    fun onOssInfoFaceResponseEvent(event: OssInfoFaceResponseEvent) {
        if(event.isSuccess){
            event.model?.blvb?.let{
                ImageUploader.uploadImage(real_path,it,object:Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        hideLoading()
                        runOnUiThread {
                            ToastUtil.showLong(this@VerifyInfoActivity,"upload fail：${e.message}")
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        runOnUiThread {
                            if (response.isSuccessful) {
                                val fileName = File(real_path).name
                                val ossImageUrl = "${it.dwr}$fileName"
                                HttpClient.verifyOcrFace(this@VerifyInfoActivity,ossImageUrl.formatSubString())

                            } else {
                                hideLoading()
                                ToastUtil.showLong(this@VerifyInfoActivity,"upload error：HTTP error code ${response.code}")
                            }
                        }
                        response.close()
                    }
                })
            }
        }else {
            hideLoading()
            ToastUtil.showLong(this,event.networkError.toString())
        }

    }

    private fun showWorkTypeSheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Please select",VerifyInfoUtil.getWorkTypeList(),
            workTypeIndex, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    workTypeIndex = index
                    bindViews.verify1.workStatusTv.text = VerifyInfoUtil.getWorkTypeList()[workTypeIndex].name
                    if(bindViews.verify1.incomeTv.text.isEmpty()){
                        showMonthlyIncomeSheet()
                    }
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun showMonthlyIncomeSheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Please select",VerifyInfoUtil.getMonthlyIncomeList(),
            monthlyIncomeIndex, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    monthlyIncomeIndex = index
                    bindViews.verify1.incomeTv.text = VerifyInfoUtil.getMonthlyIncomeList()[monthlyIncomeIndex].name
                    if(bindViews.verify1.educationStatusTv.text.isEmpty()){
                        showEducationSheet()
                    }
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun showEducationSheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Please select",VerifyInfoUtil.getEducationList(),
            educationIndex, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    educationIndex = index
                    bindViews.verify1.educationStatusTv.text = VerifyInfoUtil.getEducationList()[educationIndex].name
                    if(bindViews.verify1.maritalStatusTv.text.isEmpty()){
                        showMaritalSheet()
                    }
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun showMaritalSheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Please select",VerifyInfoUtil.getMaritalList(),
            maritalIndex, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    maritalIndex = index
                    bindViews.verify1.maritalStatusTv.text = VerifyInfoUtil.getMaritalList()[maritalIndex].name
                    if(bindViews.verify1.numberOfChildrenTv.text.isEmpty()){
                        showNumberSheet()
                    }
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun showNumberSheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Please select",VerifyInfoUtil.getNumOfChildrenList(),
            numberOfChildIndex, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    numberOfChildIndex = index
                    bindViews.verify1.numberOfChildrenTv.text = VerifyInfoUtil.getNumOfChildrenList()[numberOfChildIndex].name
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }





}