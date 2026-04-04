package com.credit.bridge.ui.verify

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.credit.bridge.remote.event.VerifyContactInfoResponseEvent
import com.credit.bridge.util.AppUtil.formatSubString
import com.credit.bridge.util.ImageUploader
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.widget.CommonBottomSheet
import com.credit.bridge.widget.StartVerifyBottomSheet
import com.credit.bridge.widget.VerifyBankBottomSheet
import com.squareup.otto.Subscribe
import kotlinx.coroutines.launch
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
    private var contact1Index = -1
    private var contact2Index = -1

    var real_path = ""

    private val contact1Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val contactUri = result.data?.data ?: return@registerForActivityResult
        contentResolver.query(
            contactUri,
            arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
            null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    ?.replace(" ", "")?.replace("-", "")
                bindViews.verify2.contact1Tv.text = name ?: ""
                bindViews.verify2.phone1Tv.text = phone ?: ""
            }
        }
    }

    private val contact2Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val contactUri = result.data?.data ?: return@registerForActivityResult
        contentResolver.query(
            contactUri,
            arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
            null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    ?.replace(" ", "")?.replace("-", "")
                bindViews.verify2.contact2Tv.text = name ?: ""
                bindViews.verify2.phone2Tv.text = phone ?: ""
            }
        }
    }

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

        bindViews.verify2.relationship1Ll.setOnClickListener(this)
        bindViews.verify2.contact1Ll.setOnClickListener(this)
        bindViews.verify2.phone1Ll.setOnClickListener(this)
        bindViews.verify2.relationship2Ll.setOnClickListener(this)
        bindViews.verify2.contact2Ll.setOnClickListener(this)
        bindViews.verify2.phone2Ll.setOnClickListener(this)

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
                showMonthlyIncomeSheet()
            }
            R.id.educationStatusLl -> {
                showEducationSheet()
            }
            R.id.maritalStatusLl -> {
                showMaritalSheet()
            }
            R.id.numberOfChildrenLl -> {
                showNumberSheet()
            }
            R.id.relationship1Ll -> {
                showRelationship1Sheet()
            }
            R.id.relationship2Ll -> {
                showRelationship2Sheet()
            }
            R.id.contact1Ll -> {
                chooseContact1()
            }
            R.id.contact2Ll -> {
                chooseContact2()
            }
            R.id.phone1Ll -> {
                chooseContact1()
            }
            R.id.phone2Ll -> {
                chooseContact2()
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

        currentStep++
        refreshUI()
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

        if (contact1Index == -1) {
            ToastUtil.showLong(this, "Please select the relationship for Contact 1")
            return
        }
        val contact1Value = bindViews.verify2.contact1Tv.text
        val phone1Value = bindViews.verify2.phone1Tv.text
        val contact2Value = bindViews.verify2.contact2Tv.text
        val phone2Value = bindViews.verify2.phone2Tv.text
        val relation1 = bindViews.verify2.relationship1Tv.text
        val relation2 = bindViews.verify2.relationship2Tv.text

        if (contact1Value.isEmpty()) {
            ToastUtil.showLong(this, "Contact 1 name cannot be empty")
            return
        }
        if (phone1Value.isEmpty()) {
            ToastUtil.showLong(this, "Contact 1 phone number cannot be empty")
            return
        }
        if (contact2Index == -1) {
            ToastUtil.showLong(this, "Please select the relationship for Contact 2")
            return
        }
        if (contact2Value.isEmpty()) {
            ToastUtil.showLong(this, "Contact 2 name cannot be empty")
            return
        }
        if (phone2Value.isEmpty()) {
            ToastUtil.showLong(this, "Contact 2 phone number cannot be empty")
            return
        }
        var totalCount = 3
        if(relation1.toString().trim() == relation2.toString().trim()){
            totalCount--
        }
        if(contact1Value.toString().trim() == contact2Value.toString().trim()){
            totalCount--
        }
        if(phone1Value.toString().trim() == phone2Value.toString().trim()){
            totalCount--
        }
        if(totalCount < 3){
            ToastUtil.showLong(this, "Please make sure the contact details are different")
            return
        }
        HttpClient.eventReport(this,ConstConfig.POINT_CONTACT_SUBMIT,
            ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_CONTACT_SUBMIT)

        val contactList = arrayListOf<RequestContactBody>()
        val contact1 = RequestContactBody()
        contact1.mngspckl = VerifyInfoUtil.contact1FormatList[contact1Index]
        contact1.jhov = bindViews.verify2.contact1Tv.text.toString()
        contact1.sucbzl = bindViews.verify2.phone1Tv.text.toString()
        val contact2 = RequestContactBody()
        contact2.mngspckl = VerifyInfoUtil.contact2FormatList[contact2Index]
        contact2.jhov = bindViews.verify2.contact2Tv.text.toString()
        contact2.sucbzl = bindViews.verify2.phone2Tv.text.toString()
        contactList.add(contact1)
        contactList.add(contact2)

        showLoading()
        HttpClient.verifyContactInfo(this,contactList,)
    }

    @Subscribe
    fun onVerifyContactInfoResponseEvent(event: VerifyContactInfoResponseEvent) {
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

    private fun showRelationship1Sheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Please select",VerifyInfoUtil.getContact1List(),
            contact1Index, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    contact1Index = index
                    bindViews.verify2.relationship1Tv.text = VerifyInfoUtil.getContact1List()[contact1Index].name
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun showRelationship2Sheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Please select",VerifyInfoUtil.getContact2List(),
            contact2Index, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    contact2Index = index
                    bindViews.verify2.relationship2Tv.text = VerifyInfoUtil.getContact2List()[contact2Index].name
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun chooseContact1() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        contact1Launcher.launch(intent)
    }

    private fun chooseContact2() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        contact1Launcher.launch(intent)
    }








}