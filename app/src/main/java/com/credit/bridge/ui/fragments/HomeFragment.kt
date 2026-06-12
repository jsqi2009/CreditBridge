package com.credit.bridge.ui.fragments

import android.app.Activity
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.content.ConstConfig.ORDER_STATUS_ISSUE_FAILED
import com.credit.bridge.databinding.FragmentHomeBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.BaseDeviceInfo
import com.credit.bridge.remote.bean.HomeInfo
import com.credit.bridge.remote.bean.SystemInfo
import com.credit.bridge.remote.body.RequestHomeInfoBody
import com.credit.bridge.remote.event.CheckCollectDataStatusResponseEvent
import com.credit.bridge.remote.event.CheckRecreditNeededResponseEvent
import com.credit.bridge.remote.event.CheckUploadStatus2ResponseEvent
import com.credit.bridge.remote.event.CheckUploadStatusResponseEvent
import com.credit.bridge.remote.event.ExecuteRecreditResponseEvent
import com.credit.bridge.remote.event.HomeInfoResponseEvent
import com.credit.bridge.remote.event.PrivacyPolicyUrlResponseEvent
import com.credit.bridge.remote.event.PrivacyPolicyUrlResponseEvent2
import com.credit.bridge.remote.event.UpdateCardEvent
import com.credit.bridge.remote.event.UpdateTabIndexEvent
import com.credit.bridge.remote.event.UploadInstalledPackageListResponseEvent
import com.credit.bridge.remote.event.UploadSystemResponseEvent
import com.credit.bridge.ui.order.OrderDetailsActivity
import com.credit.bridge.ui.product.ProductListActivity
import com.credit.bridge.ui.verify.VerifyInfoActivity
import com.credit.bridge.util.DeviceInfoUtil
import com.credit.bridge.util.DialogUtil
import com.credit.bridge.util.LocationHelper
import com.credit.bridge.util.PermissionGuideType
import com.credit.bridge.util.SystemDataUtils
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.widget.PermissionBottomSheet
import com.squareup.otto.Subscribe
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.Executors
import kotlin.collections.arrayListOf

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener, EasyPermissions.PermissionCallbacks{
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    private val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    private var isAuthed = false
    var isCreateOrder = false
    private var isBackFromVerifyInfoPage = false
    var currentStep = 0
    var homeInfo: HomeInfo? = null
    private var privacyPolicyUrl = ""
    private val REQUEST_CODE = 1000
    var zipDone = false
    private var isRecreditNeeded = false
    private var isAccountCreditPipelineBusy = false
    private var isCollectingOrUploadingApps = false
    private var skipHomeUploadEvents = false
    private var pendingUploadAfterPermission = false
    private var pendingOrderNavigation = false

    private val verifyInfoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null) {
                isBackFromVerifyInfoPage = true
            }
            hideLoading()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun syncFromSession() {
        HomeSessionState.restoreFromCache()
        isAuthed = HomeSessionState.isAuthed
        currentStep = HomeSessionState.currentStep
        homeInfo = HomeSessionState.homeInfo
        applyAuthUi()
        homeInfo?.let { refreshView() }
    }

    override fun onResume() {
        super.onResume()
        syncFromSession()
        skipHomeUploadEvents = false
        if (isVisible) {
            checkCollectDataStatus()
            tryResumeUploadAfterPermissionFromSettings()
        }
    }

    override fun onStop() {
        hideLoading()
        super.onStop()
    }
    override fun initRes() {
        super.initRes()
        bindViews.accessAccountIv.setOnClickListener(this)
        //bindViews.accessManageIv.setOnClickListener(this)
        bindViews.startVerifyLl.setOnClickListener(this)
        bindViews.verifiedNeedPayDue.setOnClickListener(this)
        bindViews.verifiedNeedPay.setOnClickListener(this)
        bindViews.verifiedFail.setOnClickListener(this)

        HttpClient.eventReport(requireActivity(),ConstConfig.EVENT_HOME_SCREEN,
            ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_HOME_SCREEN)

        //autoShowPermissionSheet()
        getPolicy()
        syncFromSession()
    }

    private fun getPolicy() {
        showLoading()
        HttpClient.getPrivacyPolicyUrl2(requireContext())
    }

    private fun applyAuthUi() {
        if (!isAdded) return
        if (isAuthed) {
            bindViews.accessAccountIv.visibility = View.VISIBLE
            bindViews.startVerifyLl.visibility = View.GONE
        } else {
            bindViews.accessAccountIv.visibility = View.GONE
            bindViews.startVerifyLl.visibility = View.VISIBLE
            bindViews.verifiedNeedPayDue.visibility = View.GONE
            bindViews.verifiedNeedPay.visibility = View.GONE
            bindViews.verifiedFail.visibility = View.GONE
            bindViews.totalAmountTv.text = getString(R.string.home_credit_amount_placeholder)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.accessAccountIv -> {

                /*val intent = Intent(requireContext(), ProductListActivity::class.java)
                intent.putExtra("amountLimit", homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn)
                startActivity(intent)*/

                if (homeInfo?.hahsraev?.lwgdzqyuks == false) {
                    ToastUtil.customToastView(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv, Toast.LENGTH_SHORT)
                    return
                }
                if (homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn == null || homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn == 0) {
                    ToastUtil.customToastView(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv, Toast.LENGTH_SHORT)
                    return
                }
                isCreateOrder = true
                homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn?.let {
                    if (it > 0) {
                        //previewProduct()
                        checkRecreditNeeded()
                    } else {
                        homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv?.let {
                            ToastUtil.showLong(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv)
                        }
                    }
                }
            }
            R.id.accessManageIv -> {
                if (homeInfo?.hahsraev?.lwgdzqyuks == false) {
                    ToastUtil.customToastView(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv, Toast.LENGTH_SHORT)
                    return
                }
                if (homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn == null || homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn == 0) {
                    ToastUtil.customToastView(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv, Toast.LENGTH_SHORT)
                    return
                }
                isCreateOrder = true
                //checkUploadStatus()
                homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn?.let {
                    if (it > 0) {
                        //previewProduct()
                        checkRecreditNeeded()
                    } else {

                    }
                }
            }
            R.id.startVerifyLl -> {

                HttpClient.eventReport(requireContext(),ConstConfig.EVENT_REQUEST_VERIFICATION_START,
                    ConstConfig.EVENT_ACTION_CLICK,ConstConfig.EVENT_REQUEST_VERIFICATION_START)

                isCreateOrder = false
                checkUploadStatus()
            }
            R.id.verified_need_pay_due -> {
                eventBus?.post(UpdateTabIndexEvent(1))
            }
            R.id.verified_need_pay -> {
                eventBus?.post(UpdateTabIndexEvent(1))
            }
            R.id.verified_fail -> {
                if(homeInfo?.zydllhkuuvpqz != null && homeInfo?.zydllhkuuvpqz?.isNotEmpty() == true){
                    val orderInfo = homeInfo?.zydllhkuuvpqz?.firstOrNull { it ->
                        it.xjywdrtdxzt== ORDER_STATUS_ISSUE_FAILED
                    }
                    if(orderInfo != null){
                        val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
                        intent.putExtra("info", orderInfo)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshView() {
        if (homeInfo == null) {
            return
        }
        val amount = homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn
        if (isAuthed && amount != null && amount >= 0) {
            bindViews.totalAmountTv.text =
                context?.getString(R.string.money_symbol) + " " + String.format("%,d", amount)
        } else if (!isAuthed) {
            bindViews.totalAmountTv.text = getString(R.string.home_credit_amount_placeholder)
        }
        if (homeInfo!!.hahsraev != null && homeInfo!!.hahsraev?.gdcuhe != null) {
            val orderStatus = homeInfo?.hahsraev?.gdcuhe
            when (orderStatus) {
                ConstConfig.ORDER_STATUS_CURRENT -> {
                    bindViews.verifiedNeedPay.visibility = View.VISIBLE
                    bindViews.verifiedNeedPayDue.visibility = View.GONE
                    bindViews.verifiedFail.visibility = View.GONE
                    bindViews.llVer.visibility = View.GONE
                    bindViews.llHor.visibility = View.VISIBLE
                }
                ConstConfig.ORDER_STATUS_OVERDUE -> {
                    bindViews.verifiedNeedPay.visibility = View.GONE
                    bindViews.verifiedNeedPayDue.visibility = View.VISIBLE
                    bindViews.verifiedFail.visibility = View.GONE
                    bindViews.llVer.visibility = View.GONE
                    bindViews.llHor.visibility = View.VISIBLE
                }
                ConstConfig.ORDER_STATUS_ISSUE_FAILED -> {
                    bindViews.verifiedNeedPay.visibility = View.GONE
                    bindViews.verifiedNeedPayDue.visibility = View.GONE
                    bindViews.verifiedFail.visibility = View.VISIBLE
                    bindViews.llVer.visibility = View.GONE
                    bindViews.llHor.visibility = View.VISIBLE
                }
                else -> {
                    bindViews.verifiedNeedPay.visibility = View.GONE
                    bindViews.verifiedNeedPayDue.visibility = View.GONE
                    bindViews.verifiedFail.visibility = View.GONE
                    bindViews.llVer.visibility = View.VISIBLE
                    bindViews.llHor.visibility = View.GONE
                }
            }

        } else if (homeInfo!!.hahsraev == null) {
            bindViews.verifiedNeedPayDue.visibility = View.GONE
            bindViews.verifiedNeedPay.visibility = View.GONE
            bindViews.verifiedFail.visibility = View.GONE
            if (isAuthed) {
                bindViews.llVer.visibility = View.VISIBLE
                bindViews.llHor.visibility = View.GONE
            }
        } else {
            bindViews.verifiedNeedPay.visibility = View.GONE
            bindViews.verifiedNeedPayDue.visibility = View.GONE
            bindViews.verifiedFail.visibility = View.GONE
            bindViews.llVer.visibility = View.VISIBLE
            bindViews.llHor.visibility = View.GONE
        }
        applyAuthUi()
    }

    private fun checkUploadStatus() {
        showLoading()
        HttpClient.checkUploadStatus(requireContext())
    }

    private fun checkUploadStatus2() {
        showLoading()
        HttpClient.checkUploadStatus2(requireContext())
    }

    private fun checkCollectDataStatus() {
        try {
            showLoading()
            HttpClient.checkCollectDataStatus(requireContext())
        } catch (e: Exception) {
        }
    }
    private fun getHomeData() {

        val isVirtualMachine = DeviceInfoUtil.isVirtualDevice()
        val isUseVpn = DeviceInfoUtil.isVpnOpen(requireContext())
        val isRoot = DeviceInfoUtil.isRoot()

        val vpn = if (isUseVpn) 1 else 0
        val root = if (isRoot) 1 else 0
        val virtual = if (isVirtualMachine) 1 else 0

        val homeInfo = RequestHomeInfoBody()
        homeInfo.rxplnymc.hkoluy = vpn
        homeInfo.rxplnymc.sgcoofijlhbhgh = virtual
        homeInfo.rxplnymc.plhvpyzwqhoynw = root

        HttpClient.getHomeInfo(requireContext(), homeInfo)
    }

    @Subscribe
    fun onCheckCollectDataStatusResponseEvent(event: CheckCollectDataStatusResponseEvent) {
        try {
            hideLoading()
            if (event.isSuccess) {
                event.model?.mtaw?.let { HomeSessionState.updateCollectInfo(it) }
                //syncFromSession()
                isAuthed = HomeSessionState.isAuthed
                currentStep = HomeSessionState.currentStep
                applyAuthUi()
                showLoading()
                getHomeData()
            } else {
                syncFromSession()
                ToastUtil.showLong(requireContext(), event.errorMessage.toString())
            }
        } catch (e: Exception) {
            hideLoading()
        }
    }


    @Subscribe
    fun onHomeInfoEvent(event: HomeInfoResponseEvent) {
        try {
            hideLoading()
            if (event.isSuccess) {
                homeInfo = event.model?.mtaw
                HomeSessionState.homeInfo = homeInfo
                if (homeInfo != null) {
                    refreshView()
                }
                if(isBackFromVerifyInfoPage){
                    tryNavigateToOrderPage()
                }
            }
        } catch (e: Exception) {
            hideLoading()
        }
    }

    @Subscribe
    fun onCheckUploadStatusResponseEvent(event: CheckUploadStatusResponseEvent) {
        try {
            hideLoading()
            if (event.isSuccess) {
                if(event.model?.mtaw != true){
                    if(privacyPolicyUrl.isEmpty()) {
                        HttpClient.getPrivacyPolicyUrl(requireContext())
                    }else{
                        showPermissionSheet()
                    }
                }else{
                    if(isCreateOrder){
                        tryNavigateToOrderPage()
                    }else {
                        var intent = Intent(requireContext(), VerifyInfoActivity::class.java)
                        intent.putExtra("currentStep", currentStep)
                        verifyInfoLauncher.launch(intent)
                    }
                }
            }else{
                ToastUtil.showLong(requireContext(),event.errorMessage.toString())
            }
        } catch (e: Exception) {
            hideLoading()
        }
    }

    @Subscribe
    fun onPrivacyPolicyUrlResponseEvent(event: PrivacyPolicyUrlResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            event.model?.mtaw?.let {
                privacyPolicyUrl = it
                showPermissionSheet()
            }
        } else {
            ToastUtil.showLong(requireContext(), event.errorMessage.toString())
        }
    }

    @Subscribe
    fun onPrivacyPolicyUrlResponseEvent2(event: PrivacyPolicyUrlResponseEvent2) {
        hideLoading()
        if (event.isSuccess) {
            event.model?.mtaw?.let {
                privacyPolicyUrl = it
                Log.e("HomeFragment", "privacyPolicyUrl: $it")
                autoShowPermissionSheet()
            }
        } else {
            ToastUtil.showLong(requireContext(), event.errorMessage.toString())
        }
    }

    private fun tryNavigateToOrderPage() {
        if (!isAdded) return
        if (isRecreditNeeded) return
        if (!hasAllRequiredPermissions()) {
            pendingOrderNavigation = true
            if (hasAnyPermanentlyDeniedPermission()) {
                showPermissionGuideDialog()
            } else {
                requestSystemPermissions()
            }
            return
        }
        pendingOrderNavigation = false
        previewProduct()
    }

    private fun hasAllRequiredPermissions(): Boolean {
        return EasyPermissions.hasPermissions(requireActivity(), *permissions)
    }

    private fun hasAnyPermanentlyDeniedPermission(): Boolean {
        return getPermanentlyDeniedGuideType() != null
    }

    private fun getPermanentlyDeniedGuideType(): PermissionGuideType? {
        if (!CacheManager.hasRequestedRuntimePermissions) return null
        val activity = requireActivity()
        if (isPermanentlyDenied(activity, Manifest.permission.READ_PHONE_STATE)) {
            return PermissionGuideType.DEVICE_INFO
        }
        if (isPermanentlyDenied(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            return PermissionGuideType.LOCATION
        }
        return null
    }

    private fun isPermanentlyDenied(activity: Activity, permission: String): Boolean {
        return !EasyPermissions.hasPermissions(activity, permission)
            && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    private fun ensurePermissionsThenUpload() {
        try {
            pendingUploadAfterPermission = true
            if (hasAllRequiredPermissions()) {
                pendingUploadAfterPermission = false
                uploadInstalledPackageList()
                return
            }
            if (hasAnyPermanentlyDeniedPermission()) {
                showPermissionGuideDialog()
                return
            }
            requestSystemPermissions()
        } catch (_: Exception) {
            pendingUploadAfterPermission = false
        }
    }

    private fun showPermissionGuideDialog() {
        val type = getPermanentlyDeniedGuideType() ?: PermissionGuideType.DEVICE_INFO
        DialogUtil.showRequestPermissionDialog(
            requireContext(),
            type,
            onConfirm = { openAppSettings() },
            onCancel = {
                ToastUtil.showLong(requireContext(), "Please allow permissions to continue")
            }
        )
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    private fun tryResumeUploadAfterPermissionFromSettings() {
        if (!isAdded) return
        if (!hasAllRequiredPermissions()) return
        if (pendingOrderNavigation) {
            pendingOrderNavigation = false
            previewProduct()
            return
        }
        if (!pendingUploadAfterPermission) return
        pendingUploadAfterPermission = false
        uploadInstalledPackageList()
    }

    private fun onDeclarationSheetClosed(requestUploadAfterGrant: Boolean) {
        CacheManager.isNeedShowPermissionSheet = false
        pendingUploadAfterPermission = requestUploadAfterGrant
        requestSystemPermissions()
    }

    private fun requestSystemPermissions() {
        requestPermissions(permissions, REQUEST_CODE)
    }

    private fun uploadInstalledPackageList() {
        if (!isAdded || skipHomeUploadEvents) return
        if (isCollectingOrUploadingApps) return
        isCollectingOrUploadingApps = true
        showLoading()
        val appContext = requireContext().applicationContext
        uploadIoExecutor.execute {
            val installedList = try {
                SystemDataUtils.getInstalledAppList(appContext)
            } catch (e: Exception) {
                emptyArray()
            }
            mainHandler.post {
                if (!isAdded) {
                    finishAppUploadPipeline()
                    return@post
                }
                /*if (installedList == null) {
                    finishAppUploadPipeline()
                    ToastUtil.showLong(requireContext(), "Failed to collect app list")
                    return@post
                }*/
                showLoading()
                HttpClient.uploadInstalledPackageList(appContext, installedList )
            }
        }
    }

    private fun finishAppUploadPipeline() {
        isCollectingOrUploadingApps = false
        hideLoading()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE])
    @Subscribe
    fun onUploadInstalledPackageListResponseEvent(event: UploadInstalledPackageListResponseEvent) {
        if (!isAdded || skipHomeUploadEvents) {
            finishAppUploadPipeline()
            return
        }
        hideLoading()
        if (event.isSuccess) {
            uploadSystemInfo()
        } else {
            finishAppUploadPipeline()
            if (event.model?.fzpn == 500) {
                ToastUtil.showLong(requireContext(), event.model?.dvusonb.toString())
            } else {
                ToastUtil.showLong(requireContext(), event.errorMessage.toString())
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE])
    private fun uploadSystemInfo() {
        if (!isAdded || skipHomeUploadEvents) {
            finishAppUploadPipeline()
            return
        }
        val appContext = requireContext().applicationContext
        Log.d(TAG, "uploadSystemInfo: start location fetch")
        LocationHelper.fetchLocation(requireContext()) { location ->
            if (!isAdded || skipHomeUploadEvents) {
                Log.w(TAG, "uploadSystemInfo: fragment detached after location fetch")
                finishAppUploadPipeline()
                return@fetchLocation
            }
            Log.d(
                TAG,
                "uploadSystemInfo: location fetched ${location?.latitude},${location?.longitude}, " +
                    "locationInfoEmpty=${location == null}"
            )
            uploadIoExecutor.execute {
                val deviceInfo = try {
                    SystemDataUtils.getDeviceInfo(appContext, location)
                } catch (e: Exception) {
                    Log.e(TAG, "uploadSystemInfo: getDeviceInfo failed unexpectedly", e)
                    arrayOf(SystemInfo())
                }
                mainHandler.post {
                    if (!isAdded) {
                        finishAppUploadPipeline()
                        return@post
                    }
                    showLoading()
                    HttpClient.uploadSystemInfo(appContext, deviceInfo)
                }
            }
        }
    }

    @Subscribe
    fun onUploadSystemResponseEvent(event: UploadSystemResponseEvent) {
        if (!isAdded || skipHomeUploadEvents) {
            finishAppUploadPipeline()
            return
        }
        try {
            hideLoading()
            if (event.isSuccess) {
                zipDone = true
                if (isCreateOrder) {
                    tryNavigateToOrderPage()
                } else {
                    skipHomeUploadEvents = true
                    val intent = Intent(requireContext(), VerifyInfoActivity::class.java)
                    intent.putExtra("currentStep", currentStep)
                    verifyInfoLauncher.launch(intent)
                }
            } else {
                ToastUtil.showLong(requireContext(), event.errorMessage.toString())
            }
        } finally {
            finishAppUploadPipeline()
        }
    }

    private fun checkRecreditNeeded() {
        if (isAccountCreditPipelineBusy) return
        isAccountCreditPipelineBusy = true
        showLoading()
        HttpClient.checkRecreditNeeded(requireContext())
    }

    @Subscribe
    fun onCheckRecreditNeededResponseEvent(event: CheckRecreditNeededResponseEvent) {
        hideLoading()
        if (event.isSuccess) {

            if (event.model?.mtaw == true) {
                isRecreditNeeded = true
                //executeRecredit()
                showRecreditNeededDialog()
                checkUploadStatus2()
            } else {
                isRecreditNeeded = false
                //previewProduct()
                checkUploadStatus2()
            }
        }else{
            isAccountCreditPipelineBusy = false
            ToastUtil.showLong(requireContext(),event.errorMessage.toString())}
    }

    @Subscribe
    fun onCheckUploadStatusResponseEvent2(event: CheckUploadStatus2ResponseEvent) {
        if (!isAdded || skipHomeUploadEvents) return
        hideLoading()
        try {
            if (event.isSuccess) {
                if (event.model?.mtaw != true) {
                    ensurePermissionsThenUpload()
                } else {
                    if (isCreateOrder) {
                        tryNavigateToOrderPage()
                    } else {
                        skipHomeUploadEvents = true
                        val intent = Intent(requireContext(), VerifyInfoActivity::class.java)
                        intent.putExtra("currentStep", currentStep)
                        verifyInfoLauncher.launch(intent)
                    }
                }
            } else {
                ToastUtil.showLong(requireContext(), event.errorMessage.toString())
            }
        } finally {
            isAccountCreditPipelineBusy = false
        }
    }

    private fun executeRecredit() {
        showLoading()
        HttpClient.executeRecredit(requireContext())
    }

    @Subscribe
    fun onExecuteRecreditResponseEvent(event: ExecuteRecreditResponseEvent) {
        hideLoading()
        if (event.isSuccess) {

        }else{
            ToastUtil.showLong(requireContext(),event.errorMessage.toString())}
    }


    fun previewProduct() {
        isBackFromVerifyInfoPage = false
        if (homeInfo?.hahsraev?.lwgdzqyuks == false) {
            ToastUtil.customToastView(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv, Toast.LENGTH_SHORT)
            return
        }
        if (homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn == null || homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn == 0) {
            ToastUtil.customToastView(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv, Toast.LENGTH_SHORT)
            return
        }
        skipHomeUploadEvents = true
        val intent = Intent(requireContext(), ProductListActivity::class.java)
        intent.putExtra("amountLimit", homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn)
        startActivity(intent)
    }

    override fun onPermissionsGranted(
        requestCode: Int,
        perms: List<String?>
    ) {
        if (requestCode != REQUEST_CODE) return
        handlePermissionResultAfterRequest()
    }

    override fun onPermissionsDenied(
        requestCode: Int,
        perms: List<String?>
    ) {
        if (requestCode != REQUEST_CODE) return
        handlePermissionResultAfterRequest()
    }

    private fun handlePermissionResultAfterRequest() {
        CacheManager.hasRequestedRuntimePermissions = true
        if (!hasAllRequiredPermissions()) {
            ToastUtil.showLong(requireContext(), "Please allow permissions to continue")
            if (hasAnyPermanentlyDeniedPermission()) {
                showPermissionGuideDialog()
            }
            return
        }
        if (pendingOrderNavigation) {
            pendingOrderNavigation = false
            previewProduct()
            return
        }
        if (pendingUploadAfterPermission) {
            pendingUploadAfterPermission = false
            uploadInstalledPackageList()
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

    fun showPermissionSheet() {
        if (CacheManager.isNeedShowPermissionSheet) {
            showPermissionDeclarationSheet(requestUploadAfterGrant = true)
        } else {
            ensurePermissionsThenUpload()
        }
    }

    private fun showRecreditNeededDialog() {
        DialogUtil.showRecreditNeededDialog(requireContext(), onConfirm = {
            isRecreditNeeded = false
            tryNavigateToOrderPage()
        }, onCancel = {
        })
    }

    private fun autoShowPermissionSheet() {
        if (CacheManager.isNeedShowPermissionSheet) {
            showPermissionDeclarationSheet(requestUploadAfterGrant = false)
        }
    }

    private fun showPermissionDeclarationSheet(requestUploadAfterGrant: Boolean) {
        val onClose = { onDeclarationSheetClosed(requestUploadAfterGrant) }
        Log.d(TAG, "showPermissionDeclarationSheet: $requestUploadAfterGrant, privacyPolicyUrl:" + privacyPolicyUrl)
        val permissionSheet = PermissionBottomSheet(
            requireActivity(),
            privacyPolicyUrl,
            onRefuseListener = onClose,
            onAgreeListener = onClose
        )
        permissionSheet.show(requireActivity().supportFragmentManager, "permissionSheet")
    }

    @Subscribe
    fun onUpdateCardEvent(event: UpdateCardEvent) {
        try {
            showLoading()
            checkCollectDataStatus()
            tryResumeUploadAfterPermissionFromSettings()
        } catch (e: Exception) {
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
        private val uploadIoExecutor = Executors.newSingleThreadExecutor()
        private val mainHandler = Handler(Looper.getMainLooper())

        @JvmStatic
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}