package com.credit.bridge.ui.verify

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.ActivityVerifyInfoBinding
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.CommonBean
import com.credit.bridge.remote.bean.QuestionInfoResponse
import com.credit.bridge.remote.event.CompleteVerifyResponseEvent
import com.credit.bridge.remote.event.OcrFaceNumberResponseEvent
import com.credit.bridge.remote.event.OcrPanNumberResponseEvent
import com.credit.bridge.remote.event.OcrPanResponseEvent
import com.credit.bridge.remote.event.OssInfoFaceResponseEvent
import com.credit.bridge.remote.event.OssInfoResponseEvent
import com.credit.bridge.remote.event.QuestionByStep1ResponseEvent
import com.credit.bridge.remote.event.QuestionByStep2ResponseEvent
import com.credit.bridge.remote.event.QuestionByStep3ResponseEvent
import com.credit.bridge.remote.event.QuestionByStep4ResponseEvent
import com.credit.bridge.remote.event.SaveQuestion1ResponseEvent
import com.credit.bridge.remote.event.SaveQuestion2ResponseEvent
import com.credit.bridge.remote.event.SaveQuestion3ResponseEvent
import com.credit.bridge.remote.event.SaveQuestion4ResponseEvent
import com.credit.bridge.remote.event.VerifyBankInfoResponseEvent
import com.credit.bridge.remote.event.VerifyBaseUserInfoResponseEvent
import com.credit.bridge.remote.event.VerifyContactInfoResponseEvent
import com.credit.bridge.remote.event.VerifyOcrFaceResponseEvent
import com.credit.bridge.remote.event.VerifyPanInfoResponseEvent
import com.credit.bridge.ui.product.SubmitSuccessActivity
import com.credit.bridge.util.BirthdayDateHelper
import com.credit.bridge.util.AppUtil.formatSubString
import com.credit.bridge.util.DialogUtil
import com.credit.bridge.util.ImageUploader
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.inter.OnBirthdaySelectListener
import com.credit.bridge.widget.BirthdayPickerBottomSheet
import com.credit.bridge.widget.CommonBottomSheet
import com.credit.bridge.widget.StartVerifyBottomSheet
import com.liveness.dflivenesslibrary.DFTransferResultInterface
import com.liveness.dflivenesslibrary.liveness.DFActionLivenessActivity
import com.liveness.dflivenesslibrary.liveness.util.Constants
import com.squareup.otto.Subscribe
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.FileOutputStream
import kotlin.use

class VerifyInfoActivity : BaseActivity<ActivityVerifyInfoBinding>(), View.OnClickListener, EasyPermissions.PermissionCallbacks {


    override fun getBinding() = ActivityVerifyInfoBinding.inflate(layoutInflater)

    private var currentStep = 0
    private var verifyStep1KeyboardHandlingActive = false
    private var isPanVerifySuccess = false
    private var workTypeIndex = -1
    private var monthlyIncomeIndex = -1
    private var educationIndex = -1
    private var maritalIndex = -1
    private var numberOfChildIndex = -1
    private var contact1Index = -1
    private var contact2Index = -1
    private var genderIndex = -1

    var real_path = ""
    var cardImgPath = ""
    private val REQUEST_CODE_PERMISSION = 1002
    var panNumberOfTimes = 0
    var panNumberFailTimes = 0
    var isOcrNumberPassed = false
    var faceNumberOfTimes = 0
    var isUseOcePan = false
    var isUseVerifyFace = false
    var isFacePassed = false

    private var step1QuestionInfo: QuestionInfoResponse = QuestionInfoResponse()
    private var step2QuestionInfo: QuestionInfoResponse = QuestionInfoResponse()
    private var step3QuestionInfo: QuestionInfoResponse = QuestionInfoResponse()
    private var step4QuestionInfo: QuestionInfoResponse = QuestionInfoResponse()
    private var step5QuestionInfo: QuestionInfoResponse = QuestionInfoResponse()

    private val contact1Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val contactUri = result.data?.data ?: return@registerForActivityResult
        try {
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
        } catch (e: Exception) {

        }
    }

    private val contact2Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val contactUri = result.data?.data ?: return@registerForActivityResult
        try {
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
        } catch (e: Exception) {
        }
    }

    private val liveFaceLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        val resultCode = result.resultCode
        HttpClient.verifyOcrFaceNumber(this@VerifyInfoActivity)
        if (resultCode == RESULT_OK ) {
            val mResult =  (this.application as DFTransferResultInterface).result
            if (mResult != null) {
                val imageResultArr = mResult.livenessImageResults
                if (imageResultArr != null) {
                    val size = imageResultArr.size
                    if (size > 0) {
                        var imageResult = imageResultArr[0]
                        var imageBitmap = BitmapFactory.decodeByteArray(
                            imageResult.image,
                            0,
                            imageResult.image.size
                        )
                        bindViews.verify5.verifyFaceIv.setImageBitmap(imageBitmap)
                        val tempFile = File.createTempFile("ocr_face_",
                            ".jpg",
                            cacheDir
                        )
                        FileOutputStream(tempFile).use { it.write(imageResult.image) }
                        real_path = tempFile.absolutePath
                        showLoading()
                        HttpClient.getOssInfo(this@VerifyInfoActivity, 2)
                    }
                }
            }else{

                HttpClient.eventReport(this,ConstConfig.EVENT_FAIL_LIVENESS,
                    ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_FAIL_LIVENESS)
            }
        } else {

            HttpClient.eventReport(this,ConstConfig.EVENT_FAIL_LIVENESS,
                ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_FAIL_LIVENESS)

            if (result.data != null) {
                val errorCode = data!!.getIntExtra(DFActionLivenessActivity.KEY_RESULT_ERROR_CODE, -10000);
                Log.e("onActivityResult", "action liveness cancel，error code:" + errorCode);
            }
        }
    }


    private val takePhoto: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null && !TextUtils.isEmpty(result.data?.getStringExtra("path_img"))) {
            cardImgPath = result.data?.getStringExtra("path_img") ?: ""
            Glide.with(this@VerifyInfoActivity).load(cardImgPath).into(bindViews.verify4.cardIv)
            identifyOcrPanCardInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        panNumberFailTimes = 0
    }

    override fun initRes() {
        super.initRes()

        currentStep = intent.getIntExtra("currentStep",0)
        Log.e("VerifyInfoActivity", "currentStep: $currentStep")

        bindViews.titleLayout.titleTv.text = "Details"
        bindViews.titleLayout.rightTv.text = "1/5"
        bindViews.titleLayout.rightTv.visibility = View.VISIBLE

        currentStep++
        refreshUI()

        bindViews.titleLayout.backLl.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.continueTv.setOnClickListener(this)
        bindViews.verify1.workStatusLl.setOnClickListener(this)
        bindViews.verify1.incomeLl.setOnClickListener(this)
        bindViews.verify1.educationStatusLl.setOnClickListener(this)
        bindViews.verify1.maritalStatusLl.setOnClickListener(this)
        bindViews.verify1.numberOfChildrenLl.setOnClickListener(this)
        bindViews.verify1.whatsappEt.addTextChangedListener(verifyStep1WhatsappTextWatcher)

        bindViews.verify2.relationship1Ll.setOnClickListener(this)
        bindViews.verify2.contact1Ll.setOnClickListener(this)
        bindViews.verify2.phone1Ll.setOnClickListener(this)
        bindViews.verify2.relationship2Ll.setOnClickListener(this)
        bindViews.verify2.contact2Ll.setOnClickListener(this)
        bindViews.verify2.phone2Ll.setOnClickListener(this)

        bindViews.verify3.accountNumberEt.addTextChangedListener(accountTextWatcher)
        bindViews.verify3.confirmAccountNumberEt.addTextChangedListener(confirmAccountTextWatcher)

        bindViews.verify4.panNumberIv.setOnClickListener(this)
        bindViews.verify4.birthDateLl.setOnClickListener(this)
        bindViews.verify4.genderLl.setOnClickListener(this)
        bindViews.retryTv.setOnClickListener(this)
        bindViews.step4ContinueTv.setOnClickListener(this)

        bindViews.verify5.verifyFaceIv.setOnClickListener(this)

        bindViews.retryTv.paint.isUnderlineText = true

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> {
                showVerifyTipsDialog()
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
            R.id.panNumberIv -> {
                //startOcrPanNumber()
                showStartOcrPanNumberSheet()
            }
            R.id.birthDateLl -> {
                showBirthdayPickerSheet()
            }
            R.id.genderLl -> {
                showGenderSheet()
            }
            R.id.verifyFaceIv -> {
                startVerifyFace()
            }
            R.id.continueTv -> {
                handleStepOperation()
            }
            R.id.retryTv -> {
                showStartOcrPanNumberSheet()
            }
            R.id.step4ContinueTv -> {
                verifyPanAction()
            }
        }
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
            5 -> {
                completeVerify()
            }
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun refreshUI() {
        when (currentStep) {
            1 -> {
                bindViews.verify1.root.visibility = View.VISIBLE
                bindViews.verifyTopImg.background = getDrawable(R.mipmap.ic_verify_top_1)
                bindViews.titleLayout.titleTv.text = "Basic Information"
                bindViews.titleLayout.rightTv.text = "1/5"

                HttpClient.getQuestionByStep(this, currentStep)

                HttpClient.eventReport(this,ConstConfig.EVENT_INTO_INFO,
                    ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_INTO_INFO)
            }
            2 -> {
                bindViews.verify1.root.visibility = View.GONE
                bindViews.verify2.root.visibility = View.VISIBLE
                bindViews.verifyTopImg.background = getDrawable(R.mipmap.ic_verify_top_2)
                bindViews.titleLayout.titleTv.text = "Contact Information"
                bindViews.titleLayout.rightTv.text = "2/5"

                HttpClient.getQuestionByStep(this, currentStep)

                HttpClient.eventReport(this,ConstConfig.EVENT_CONTACT_INPUT,
                    ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_CONTACT_INPUT)
            }
            3 -> {
                bindViews.verify2.root.visibility = View.GONE
                bindViews.verify3.root.visibility = View.VISIBLE
                bindViews.verifyTipsLayout.visibility = View.VISIBLE
                bindViews.verifyTopImg.background = getDrawable(R.mipmap.ic_verify_top_3)
                bindViews.titleLayout.titleTv.text = "Bank Information"
                bindViews.titleLayout.rightTv.text = "3/5"

                HttpClient.getQuestionByStep(this, currentStep)

                HttpClient.eventReport(this,ConstConfig.EVENT_BANKCARD_INPUT,
                    ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_BANKCARD_INPUT)
            }
            4 -> {
                bindViews.verify3.root.visibility = View.GONE
                bindViews.verify4.root.visibility = View.VISIBLE
                bindViews.verifyTipsLayout.visibility = View.VISIBLE
                bindViews.attemptLeftTv.visibility = View.VISIBLE
                bindViews.attemptLeftTv.text = getString(R.string.verify_photo_attempts_left_today) + panNumberOfTimes
                bindViews.verifyTipsTv.text = "Please verify your ID information to proceed with account confirmation."
                bindViews.verifyTopImg.background = getDrawable(R.mipmap.ic_verify_top_4)
                bindViews.titleLayout.titleTv.text = "KYC Information"
                bindViews.titleLayout.rightTv.text = "4/5"

                HttpClient.getQuestionByStep(this, currentStep)

                HttpClient.eventReport(this,ConstConfig.EVENT_IDCARD_INPUT,
                    ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_IDCARD_INPUT)
            }
            5 -> {
                bindViews.verify4.root.visibility = View.GONE
                bindViews.stepBtn4.visibility = View.GONE
                bindViews.verifyTipsLayout.visibility = View.GONE
                bindViews.verify5.root.visibility = View.VISIBLE
                bindViews.step5Line.root.visibility = View.VISIBLE
                bindViews.verifyTipsTv.visibility = View.GONE
                bindViews.verifyTopImg.background = getDrawable(R.mipmap.ic_verify_top_5)
                bindViews.attemptLeftTv.visibility = View.VISIBLE
                bindViews.attemptLeftTv.text = getString(R.string.verify_face_attempts_left_today) + faceNumberOfTimes
                bindViews.titleLayout.titleTv.text = "Liveness Verification"
                bindViews.titleLayout.rightTv.text = "5/5"
                bindViews.continueTv.text = "Submit"
                bindViews.continueTv.visibility = View.VISIBLE

                HttpClient.getQuestionByStep(this, currentStep)

                HttpClient.eventReport(this,ConstConfig.EVENT_INPUT_LIVENESS,
                    ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_INPUT_LIVENESS)

                HttpClient.verifyOcrFaceNumber(this@VerifyInfoActivity)
            }
        }
        syncVerifyStep1KeyboardScroll()
    }

    private fun syncVerifyStep1KeyboardScroll() {
        if (currentStep == 1) {
            enableVerifyStep1KeyboardHandling()
        } else if (verifyStep1KeyboardHandlingActive) {
            disableVerifyStep1KeyboardHandling()
        }
    }

    private fun enableVerifyStep1KeyboardHandling() {
        if (verifyStep1KeyboardHandlingActive) return
        verifyStep1KeyboardHandlingActive = true

        val root = bindViews.main
        val defaultBottom = resources.getDimensionPixelSize(R.dimen.bottom_menu_height_64)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            if (currentStep != 1) {
                return@setOnApplyWindowInsetsListener insets
            }
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.setPadding(0, 0, 0, maxOf(imeBottom, navBottom, defaultBottom))
            scheduleVerifyStep1FocusedFieldScroll()
            insets
        }
        ViewCompat.requestApplyInsets(root)

        val focusScroll = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus && currentStep == 1 && bindViews.verify1.root.visibility == View.VISIBLE) {
                scrollVerifyStep1FieldIntoView(v)
            }
        }
        bindViews.verify1.emailEt.onFocusChangeListener = focusScroll
        bindViews.verify1.whatsappEt.onFocusChangeListener = focusScroll
    }

    private fun disableVerifyStep1KeyboardHandling() {
        if (!verifyStep1KeyboardHandlingActive) return
        verifyStep1KeyboardHandlingActive = false

        val root = bindViews.main
        ViewCompat.setOnApplyWindowInsetsListener(root, null)
        root.setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.bottom_menu_height_64))
        bindViews.verify1.emailEt.onFocusChangeListener = null
        bindViews.verify1.whatsappEt.onFocusChangeListener = null
        ViewCompat.requestApplyInsets(root)
    }

    private fun scheduleVerifyStep1FocusedFieldScroll() {
        if (currentStep != 1 || bindViews.verify1.root.visibility != View.VISIBLE) return
        val focused = currentFocus ?: return
        if (focused != bindViews.verify1.emailEt && focused != bindViews.verify1.whatsappEt) return
        bindViews.verifyScrollView.postDelayed({
            if (currentStep == 1) {
                scrollVerifyStep1FieldIntoView(focused)
            }
        }, 80)
    }

    private fun scrollVerifyStep1FieldIntoView(focused: View) {
        if (currentStep != 1) return
        val scrollView = bindViews.verifyScrollView
        scrollView.post {
            if (currentStep != 1 || bindViews.verify1.root.visibility != View.VISIBLE) return@post
            val content = scrollView.getChildAt(0) ?: return@post
            val rect = Rect()
            focused.getDrawingRect(rect)
            scrollView.offsetDescendantRectToMyCoords(focused, rect)
            rect.bottom += resources.getDimensionPixelSize(R.dimen.margin_20)
            scrollView.requestChildRectangleOnScreen(content, rect, true)
        }
    }

    @Subscribe
    fun onQuestionByStep1ResponseEvent(event: QuestionByStep1ResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            if (event.model != null) {
                event.model?.mtaw.let {
                    step1QuestionInfo = event.model!!.mtaw!!
                    val originalList = step1QuestionInfo.ffuyqtcgfw[0].dqivkmhqfzwexxoc ?: emptyList()
                    val workTypeList: ArrayList<CommonBean?> = originalList
                        .map { CommonBean(name = it.ufpowipd)}
                        .toCollection(ArrayList())
                    showWorkTypeSheet()
                }
            }
        }
    }

    @Subscribe
    fun onQuestionByStep2ResponseEvent(event: QuestionByStep2ResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            if (event.model != null) {
                event.model?.mtaw.let {
                    step2QuestionInfo = event.model!!.mtaw!!
                    showRelationship1Sheet()
                }
            }
        }
    }

    @Subscribe
    fun onQuestionByStep3ResponseEvent(event: QuestionByStep3ResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            if (event.model != null) {
                event.model?.mtaw.let {
                    step3QuestionInfo = event.model!!.mtaw!!
                }
            }
        }
    }

    @Subscribe
    fun onQuestionByStep4ResponseEvent(event: QuestionByStep4ResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            if (event.model != null) {
                event.model?.mtaw.let {
                    step4QuestionInfo = event.model!!.mtaw!!
                    startOcrPanNumber()
                }
            }
        }
    }
    private fun verifyBaseUserAction() {

        if (workTypeIndex == -1) {
            ToastUtil.showLong(this, "Please choose your employment type")
            return
        }
        if (monthlyIncomeIndex == -1) {
            ToastUtil.showLong(this, "Please select your monthly available income")
            return
        }
        if (educationIndex == -1) {
            ToastUtil.showLong(this, "Please choose your education qualification")
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
            ToastUtil.showLong(this, "Please enter your WhatsApp mobile number")
            return
        }

        HttpClient.eventReport(this,ConstConfig.EVENT_INFO_SUBMIT,
            ConstConfig.EVENT_ACTION_CLICK,ConstConfig.EVENT_INFO_SUBMIT)


        val questionList = VerifyInfoUtil.getStep1RequestBody(workTypeIndex, monthlyIncomeIndex,
            educationIndex, maritalIndex, numberOfChildIndex, bindViews.verify1.emailEt.text.toString(),
            bindViews.verify1.whatsappEt.text.toString(),  step1QuestionInfo)

        showLoading()
        HttpClient.saveQuestionInfo(this, questionList, currentStep)
    }

    @Subscribe
    fun onSaveQuestion1ResponseEvent(event: SaveQuestion1ResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            currentStep++
            refreshUI()
        } else {
            if(event.model == null){
                ToastUtil.showLong(this,event.networkError.toString())
            }else{
                if(event.model?.fzpn == 500){
                    ToastUtil.showLong(this,event.model?.dvusonb)
                }
            }
        }
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
                if(event.model?.fzpn == 500){
                    ToastUtil.showLong(this,event.model?.dvusonb)
                }
            }
        }
    }

    private fun verifyContactAction() {

        if (contact1Index == -1) {
            ToastUtil.showLong(this, "Please select the relationship for Emergency Contact 1")
            return
        }
        val contact1Value = bindViews.verify2.contact1Tv.text.toString()
        val phone1Value = bindViews.verify2.phone1Tv.text.toString()
        val contact2Value = bindViews.verify2.contact2Tv.text.toString()
        val phone2Value = bindViews.verify2.phone2Tv.text.toString()
        val relation1 = bindViews.verify2.relationship1Tv.text.toString()
        val relation2 = bindViews.verify2.relationship2Tv.text.toString()

        if (contact1Value.isEmpty()) {
            ToastUtil.showLong(this, "Please enter the name of Emergency Contact 1")
            return
        }
        if (phone1Value.isEmpty()) {
            ToastUtil.showLong(this, "Please enter the phone number of Emergency Contact 1")
            return
        }
        if (contact2Index == -1) {
            ToastUtil.showLong(this, "Please select the relationship for Emergency Contact 2")
            return
        }
        if (contact2Value.isEmpty()) {
            ToastUtil.showLong(this, "Please enter the name of Emergency Contact 2")
            return
        }
        if (phone2Value.isEmpty()) {
            ToastUtil.showLong(this, "Please enter the phone number of Emergency Contact 2")
            return
        }
        if (relation1 == relation2) {
            ToastUtil.showLong(this, "Emergency contacts must have different relationships")
            return
        }
        if (contact1Value == contact2Value) {
            ToastUtil.showLong(this, "Please provide two different emergency contacts")
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
            ToastUtil.showLong(this, "Contact details cannot be duplicated")
            return
        }
        HttpClient.eventReport(this,ConstConfig.EVENT_CONTACT_SUBMIT,
            ConstConfig.EVENT_ACTION_CLICK,ConstConfig.EVENT_CONTACT_SUBMIT)

        val questionList = VerifyInfoUtil.getStep2RequestBody(VerifyInfoUtil.contact1FormatList[contact1Index] ,
            VerifyInfoUtil.contact2FormatList[contact2Index],
            contact1Value, contact2Value, phone1Value,phone2Value,
           step2QuestionInfo)

        showLoading()
        HttpClient.saveQuestionInfo(this, questionList, currentStep)
    }

    @Subscribe
    fun onSaveQuestion2ResponseEvent(event: SaveQuestion2ResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            currentStep++
            refreshUI()
        } else {
            if(event.model == null){
                ToastUtil.showLong(this,event.networkError.toString())
            }else{
                if(event.model?.fzpn == 500){
                    ToastUtil.showLong(this,event.model?.dvusonb)
                }
            }
        }
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
                if(event.model?.fzpn == 500){
                    ToastUtil.showLong(this,event.model?.dvusonb)
                }
            }
        }
    }

    private fun verifyBankAction() {

        val accountNumber = bindViews.verify3.accountNumberEt.text.toString()
        val confirmAccountNumber = bindViews.verify3.confirmAccountNumberEt.text.toString()
        val ifscCode = bindViews.verify3.ifscCodeEt.text.toString()

        if (accountNumber.toString().isEmpty()) {
            ToastUtil.showLong(this, "Please enter your bank account number")
            return
        }
        if (confirmAccountNumber.toString().isEmpty()) {
            ToastUtil.showLong(this, "Please confirm your bank account number")
            return
        }
        if (ifscCode.toString().isEmpty()) {
            ToastUtil.showLong(this, "Please provide the IFSC code")
            return
        }
        if (accountNumber.toString().replace(" ","") != confirmAccountNumber.toString().replace(" ","")) {
            ToastUtil.showLong(this, "The account numbers do not match")
            return
        }
        if (ifscCode.toString().length != 11) {
            ToastUtil.showLong(this, "IFSC code must be exactly 11 characters")
            return
        }

        HttpClient.eventReport(this,ConstConfig.EVENT_BANKCARD_SUBMIT,
            ConstConfig.EVENT_ACTION_CLICK,ConstConfig.EVENT_BANKCARD_SUBMIT)

        val questionList = VerifyInfoUtil.getStep3RequestBody(accountNumber.replace(" ", ""),
            confirmAccountNumber.replace(" ", ""), ifscCode, step3QuestionInfo)
        showLoading()
        HttpClient.saveQuestionInfo(this, questionList, currentStep)

        /*showLoading()
        HttpClient.verifyBankInfo(this, "", accountNumber.trim()
            ,confirmAccountNumber.trim(), ifscCode,"")*/
    }

    @Subscribe
    fun onSaveQuestion3ResponseEvent(event: SaveQuestion3ResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            currentStep++
            refreshUI()
        } else {
            if(event.model == null){
                ToastUtil.showLong(this,event.networkError.toString())
            }else{
                if(event.model?.fzpn == 500){
                    ToastUtil.showLong(this,event.model?.dvusonb)
                }
            }
        }
    }

    @Subscribe
    fun onVerifyBankInfoResponseEvent(event: VerifyBankInfoResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            currentStep++
            refreshUI()
        } else {
            if(event.model == null){
                ToastUtil.showLong(this,event.networkError.toString())
            }else{
                if(event.model?.fzpn == 500){
                    ToastUtil.showLong(this,event.model?.dvusonb)
                }
            }
        }
    }

    private fun verifyPanAction() {

        if (bindViews.verify4.panInfoLl.isGone) {
            if(isUseOcePan){
                ToastUtil.showLong(this, "Unable to recognize the image. Please retake the photo")
            }else{
                ToastUtil.showLong(this, "Please capture a photo to continue")
            }
            return
        }

        val fullName = bindViews.verify4.fullNameTv.text.toString()
        val panNumber = bindViews.verify4.panNumberTv.text.toString()
        val birthDate = BirthdayDateHelper.toFormValue(bindViews.verify4.birthDateTv.text.toString())
            ?: bindViews.verify4.birthDateTv.text.toString()

        if (panNumber.isEmpty()) {
            ToastUtil.showLong(this, "Please enter your PAN number")
            return
        }
        if (fullName.isEmpty()) {
            ToastUtil.showLong(this, "Please enter your full legal name")
            return
        }
        if (birthDate.isEmpty()) {
            ToastUtil.showLong(this, "Please select your birth date")
            return
        }
        if (genderIndex == -1) {
            ToastUtil.showLong(this, "Please select your gender")
            return
        }


        HttpClient.eventReport(this,ConstConfig.EVENT_IDCARD_SUNMIT,
            ConstConfig.EVENT_ACTION_CLICK,ConstConfig.EVENT_IDCARD_SUNMIT)

        val questionList = VerifyInfoUtil.getStep4RequestBody(panNumber, fullName, birthDate,
            VerifyInfoUtil.genderFormatList[genderIndex], step4QuestionInfo)

        showLoading()
        HttpClient.saveQuestionInfo(this, questionList, currentStep)
    }

    @Subscribe
    fun onSaveQuestion4ResponseEvent(event: SaveQuestion4ResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            currentStep++
            refreshUI()
        } else {
            if(event.model == null){
                ToastUtil.showLong(this,event.networkError.toString())
            }else{
                if(event.model?.fzpn == 500){
                    ToastUtil.showLong(this,event.model?.dvusonb)
                }
            }
        }
    }

    @Subscribe
    fun onVerifyPanInfoResponseEvent(event: VerifyPanInfoResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            currentStep++
            refreshUI()
        } else {
            if(event.model == null){
                ToastUtil.showLong(this,event.networkError.toString())
            }else{
                if(event.model?.fzpn == 500){
                    ToastUtil.showLong(this,event.model?.dvusonb)
                }
            }
        }
    }


    @Subscribe
    fun onOssInfoFaceResponseEvent(event: OssInfoFaceResponseEvent) {
        if(event.isSuccess){
            event.model?.mtaw?.let{
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
                                val ossImageUrl = "${it.fev}$fileName"
                                showLoading()
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

    private fun completeVerify(){
        /*if(!isFacePassed){
            ToastUtil.showLong(this, "Face verification was unsuccessful. Please try again")
            return
        }*/
        if(!isFacePassed){
            startVerifyFace()
            return
        }

        showLoading()
        HttpClient.completeVerify(this)
    }

    @Subscribe
    fun onCompleteVerifyResponseEvent(event: CompleteVerifyResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            showVerifySuccessDialog()
        }
    }

    private fun showWorkTypeSheet() {
        val workTypeSheet = CommonBottomSheet(
            this,step1QuestionInfo.ffuyqtcgfw[0].qpwjbrdvuq ,VerifyInfoUtil.getWorkTypeList(),
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
            this,step1QuestionInfo.ffuyqtcgfw[1].qpwjbrdvuq,VerifyInfoUtil.getMonthlyIncomeList(),
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
            this,step1QuestionInfo.ffuyqtcgfw[2].qpwjbrdvuq,VerifyInfoUtil.getEducationList(),
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
            this,step1QuestionInfo.ffuyqtcgfw[3].qpwjbrdvuq,VerifyInfoUtil.getMaritalList(),
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
            this,step1QuestionInfo.ffuyqtcgfw[4].qpwjbrdvuq,VerifyInfoUtil.getNumOfChildrenList(),
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
            this,step2QuestionInfo.ffuyqtcgfw[0].qpwjbrdvuq,VerifyInfoUtil.getContact1List(),
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
            this,step2QuestionInfo.ffuyqtcgfw[3].qpwjbrdvuq,VerifyInfoUtil.getContact2List(),
            contact2Index, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    contact2Index = index
                    bindViews.verify2.relationship2Tv.text = VerifyInfoUtil.getContact2List()[contact2Index].name
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun showBirthdayPickerSheet() {
        val initial = bindViews.verify4.birthDateTv.text?.toString()
        val sheet = BirthdayPickerBottomSheet(
            this,
            initialFormValue = if (initial.isNullOrBlank()) null else initial,
            onBirthdaySelectListener = object : OnBirthdaySelectListener {
                override fun onSelect(formValue: String, displayText: String) {
                    bindViews.verify4.birthDateTv.text = displayText
                }
            }
        )
        sheet.show(supportFragmentManager, "birthdayPickerSheet")
    }

    private fun showGenderSheet() {
        val workTypeSheet = CommonBottomSheet(
            this,"Please select",VerifyInfoUtil.getGenderList(),
            genderIndex, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    genderIndex = index
                    bindViews.verify4.genderTv.text = VerifyInfoUtil.getGenderList()[genderIndex].name
                }
            })
        workTypeSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun chooseContact1() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            contact1Launcher.launch(intent)
        } else {
            ToastUtil.showLong(this,"No Contact app")
        }
    }

    private fun chooseContact2() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            contact2Launcher.launch(intent)
        } else {
            ToastUtil.showLong(this,"No Contact app")
        }
    }

    private val verifyStep1WhatsappTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (currentStep != 1) return
            val t = bindViews.verify1.whatsappEt.text?.toString()?.trim() ?: return
            if (t.length == 10 && t.all { it.isDigit() }) {
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val token = (currentFocus ?: bindViews.root).windowToken
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(token, 0)
    }

    private var accountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            val text = s.toString()
            val formatted = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
            if (formatted != text) {
                bindViews.verify3.accountNumberEt.setText(formatted)
                bindViews.verify3.accountNumberEt.setSelection(bindViews.verify3.accountNumberEt.text.toString().length)
            }
        }
    }

    private var confirmAccountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            val text = s.toString()
            val formatted = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
            if (formatted != text) {
                bindViews.verify3.confirmAccountNumberEt.setText(formatted)
                bindViews.verify3.confirmAccountNumberEt.setSelection(bindViews.verify3.confirmAccountNumberEt.text.toString().length)
            }
        }
    }

    private fun startOcrPanNumber() {
        showLoading()
        HttpClient.verifyOcrPanNumber(this)
    }

    @Subscribe
    fun onOcrPanNumberResponseEvent(event: OcrPanNumberResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            event.model?.mtaw?.let {
                panNumberOfTimes = it
                bindViews.step4LeftTv.text = getString(R.string.verify_photo_attempts_left_today) + " " + panNumberOfTimes
                bindViews.attemptLeftTv.text = getString(R.string.verify_photo_attempts_left_today) + " " + panNumberOfTimes
                //showStartOcrPanNumberSheet()
            }
        }else{
            ToastUtil.showLong(this,event.networkError.toString())
        }
    }

    private fun showStartOcrPanNumberSheet() {
        if (CacheManager.isAlreadyShowPanNumberSheet) {
            takePhotoDirectly()
        } else {
            val startOcrPanNumberSheet = StartVerifyBottomSheet(this, "", panNumberOfTimes, takePhoto = {
                CacheManager.isAlreadyShowPanNumberSheet = true
                val permission = arrayOf(Manifest.permission.CAMERA)
                if (EasyPermissions.hasPermissions(this@VerifyInfoActivity, *permission)) {
                    takePhoto.launch(Intent(this@VerifyInfoActivity, TakePhotoActivity::class.java).apply {})
                } else {
                    EasyPermissions.requestPermissions(
                        PermissionRequest.Builder(
                            this@VerifyInfoActivity,
                            REQUEST_CODE_PERMISSION,
                            *permission
                        )
                            .setRationale("Camera Permission Required To capture and upload verification photos, Rupee Cycle requires access to your camera.")
                            .setPositiveButtonText("Allow")
                            .setNegativeButtonText("Deny")
                            .build()
                    )
                }
            } )
            startOcrPanNumberSheet.show(supportFragmentManager, "startOcrPanNumberSheet")
        }

    }

    private fun takePhotoDirectly() {
        val permission = arrayOf(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this@VerifyInfoActivity, *permission)) {
            takePhoto.launch(Intent(this@VerifyInfoActivity, TakePhotoActivity::class.java).apply {})
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    this@VerifyInfoActivity,
                    REQUEST_CODE_PERMISSION,
                    *permission
                )
                    .setRationale("Camera Permission Required To capture and upload verification photos, Rupee Cycle requires access to your camera.")
                    .setPositiveButtonText("Allow")
                    .setNegativeButtonText("Deny")
                    .build()
            )
        }
    }

    private fun identifyOcrPanCardInfo(imagePath: Uri? = null) {
        showLoading()
        val builder = if (imagePath == null) {
            Luban.with(this).load(cardImgPath)
        } else {
            Luban.with(this).load(imagePath)
        }
        builder.setCompressListener(object : OnCompressListener {
            override fun onStart() {
            }

            override fun onSuccess(index: Int, file: File) {
                real_path = file.absolutePath
                HttpClient.getOssInfo(this@VerifyInfoActivity, 1)
            }

            override fun onError(index: Int, e: Throwable) {
                hideLoading()
            }
        }).launch()
    }

    @Subscribe
    fun onOssInfoResponseEvent(event: OssInfoResponseEvent) {
        //hideLoading()
        if(event.isSuccess){
            event.model?.mtaw?.let{
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
                            val ossImageUrl = "${it.fev}$fileName"
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

    /**
     * get the identify result
     */
    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onOcrPanResponseEvent(event: OcrPanResponseEvent) {
        if(event.isSuccess){
            event.model?.mtaw?.let {
                isUseOcePan = true
                if(it.mazcrn == "PASS"){
                    isOcrNumberPassed = true
                    bindViews.verify4.panInfoLl.visibility = View.VISIBLE
                    bindViews.verify4.panTips.visibility = View.GONE
                    bindViews.stepBtn4.visibility = View.VISIBLE
                    bindViews.continueTv.visibility = View.GONE
                    bindViews.attemptLeftTv.visibility = View.GONE
                    bindViews.step4LeftTv.text = getString(R.string.verify_photo_attempts_left_today) + " " + panNumberOfTimes
                    bindViews.verify4.fullNameTv.setText(it.ynyj)
                    bindViews.verify4.panNumberTv.setText(it.fwrcjkq)
                    bindViews.verify4.birthDateTv.text =
                        BirthdayDateHelper.toDisplayText(it.bzumerxb) ?: it.bzumerxb
                    hideLoading()
                }else{
                    hideLoading()
                    ToastUtil.showLong(this, it.dvusonb)
                    panNumberFailTimes  = panNumberFailTimes + 1
                    bindViews.verify4.fullNameTv.setText("")
                    bindViews.verify4.panNumberTv.setText("")
                    bindViews.verify4.birthDateTv.text = ""

                    enableManualInputOcrPanInfo()
                    isOcrNumberPassed = false

                    HttpClient.eventReport(this,ConstConfig.EVENT_IDCARD_FAIL,
                        ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_IDCARD_FAIL)
                }
            }
        } else {
            panNumberFailTimes = panNumberFailTimes + 1
            enableManualInputOcrPanInfo()
            isOcrNumberPassed = false
            HttpClient.eventReport(this,ConstConfig.EVENT_IDCARD_FAIL,
                ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_IDCARD_FAIL)

            if(event.model != null){
                hideLoading()
                ToastUtil.showLong(this, event.model?.dvusonb)
            }else{
                hideLoading()
                ToastUtil.showLong(this, "Network Error")
            }
        }
        startOcrPanNumber()
    }

    private fun enableManualInputOcrPanInfo() {
        if (panNumberFailTimes >= 2 || isOcrNumberPassed) {
            bindViews.verify4.panInfoLl.visibility = View.VISIBLE
            bindViews.verify4.panTips.visibility = View.GONE
            bindViews.stepBtn4.visibility = View.VISIBLE
            bindViews.continueTv.visibility = View.GONE
            bindViews.attemptLeftTv.visibility = View.GONE
            bindViews.step4LeftTv.text = getString(R.string.verify_photo_attempts_left_today) + " " + panNumberOfTimes
            bindViews.verify4.fullNameTv.setText("")
            bindViews.verify4.panNumberTv.setText("")
            bindViews.verify4.birthDateTv.text = ""
        }
    }

    @Subscribe
    fun onOcrFaceNumberResponseEvent(event: OcrFaceNumberResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            event.model?.mtaw?.let {
                faceNumberOfTimes = it
                bindViews.attemptLeftTv.text = resources.getString(R.string.verify_face_attempts_left_today) + " " +  faceNumberOfTimes
            }
        }else{
            if(event.model?.fzpn == 500){
                ToastUtil.showLong(this,event.model?.dvusonb)
            }else {
                ToastUtil.showLong(this, event.networkError.toString())
            }
        }
    }

    private fun startVerifyFace() {
        if (faceNumberOfTimes == 0) {
            ToastUtil.showLong(this@VerifyInfoActivity,"Daily limit reached. Try again tomorrow.")
            return
        }

        isUseVerifyFace = true
        val permission = arrayOf(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this@VerifyInfoActivity, *permission)) {
            val bundle = Bundle()
            bundle.putString(DFActionLivenessActivity.OUTTYPE, Constants.MULTIIMG)
            bundle.putString(DFActionLivenessActivity.EXTRA_MOTION_SEQUENCE, "STILL BLINK MOUTH NOD YAW")
            val intent = Intent()
            intent.setClass(this, DFActionLivenessActivity::class.java)
            intent.putExtras(bundle)
            intent.putExtra(DFActionLivenessActivity.KEY_DETECT_IMAGE_RESULT, true)
            liveFaceLauncher.launch(intent)

            HttpClient.eventReport(this,ConstConfig.EVENT_START_LIVENESS,
                ConstConfig.EVENT_ACTION_CLICK,ConstConfig.EVENT_START_LIVENESS)

        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    this@VerifyInfoActivity,
                    REQUEST_CODE_PERMISSION,
                    *permission
                )
                    .setRationale("Camera Permission Required To capture and upload verification photos, Rupee Cycle requires access to your camera.")
                    .setPositiveButtonText("Allow")
                    .setNegativeButtonText("Deny")
                    .build()
            )
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onVerifyOcrFaceResponseEvent(event: VerifyOcrFaceResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            event.model?.mtaw?.let {
                isFacePassed = true
                //showVerifySuccessDialog()
            }
        }else {
            if(event.model != null){
                ToastUtil.showLong(this, event.model?.dvusonb)
            }else{
                ToastUtil.showLong(this, "Network Error")
            }
        }
    }

    override fun onPermissionsGranted(
        requestCode: Int,
        perms: List<String?>
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if(isUseVerifyFace){
                val bundle = Bundle()
                bundle.putString(DFActionLivenessActivity.OUTTYPE, Constants.MULTIIMG)
                bundle.putString(DFActionLivenessActivity.EXTRA_MOTION_SEQUENCE, "STILL BLINK MOUTH NOD YAW")
                val intent = Intent()
                intent.setClass(this, DFActionLivenessActivity::class.java)
                intent.putExtras(bundle)
                intent.putExtra(DFActionLivenessActivity.KEY_DETECT_IMAGE_RESULT, true)
                liveFaceLauncher.launch(intent)
            }else{
                takePhoto.launch(Intent(this@VerifyInfoActivity, SubmitSuccessActivity::class.java).apply {})
            }
        }
    }

    override fun onPermissionsDenied(
        requestCode: Int,
        perms: List<String?>
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            ToastUtil.showLong(this, "Required permissions must be enabled to proceed.")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun showVerifySuccessDialog() {
        DialogUtil.showVerifySuccessDialog(this, onConfirm = {
            setResult(RESULT_OK, Intent())
            finish()
        }, onCancel = {

        })
    }

    private fun showVerifyTipsDialog() {
        DialogUtil.showVerifyTipsDialog(this, onConfirm = {
        }, onCancel = {
            finish()
        })
    }


}