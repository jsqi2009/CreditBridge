package com.credit.bridge.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import com.credit.bridge.R
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.content.ConstConfig.ORDER_STATUS_ISSUE_FAILED
import com.credit.bridge.databinding.FragmentHomeBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.HomeInfo
import com.credit.bridge.remote.body.RequestHomeInfoBody
import com.credit.bridge.remote.event.CheckCollectDataStatusResponseEvent
import com.credit.bridge.remote.event.CheckRecreditNeededResponseEvent
import com.credit.bridge.remote.event.CheckUploadStatus2ResponseEvent
import com.credit.bridge.remote.event.CheckUploadStatusResponseEvent
import com.credit.bridge.remote.event.ExecuteRecreditResponseEvent
import com.credit.bridge.remote.event.HomeInfoResponseEvent
import com.credit.bridge.remote.event.PrivacyPolicyUrlResponseEvent
import com.credit.bridge.remote.event.UpdateTabIndexEvent
import com.credit.bridge.remote.event.UploadInstalledPackageListResponseEvent
import com.credit.bridge.remote.event.UploadSystemResponseEvent
import com.credit.bridge.ui.order.OrderDetailsActivity
import com.credit.bridge.ui.product.ProductListActivity
import com.credit.bridge.ui.verify.VerifyInfoActivity
import com.credit.bridge.util.DeviceInfoUtil
import com.credit.bridge.util.DialogUtil
import com.credit.bridge.util.SystemDataUtils
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.widget.PermissionBottomSheet
import com.squareup.otto.Subscribe
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.util.concurrent.Executors

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener, EasyPermissions.PermissionCallbacks{
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    private val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

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

    private val verifyInfoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null) {
                isBackFromVerifyInfoPage = true
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        skipHomeUploadEvents = false
        if (isVisible) {
            checkCollectDataStatus()
        }
    }
    override fun initRes() {
        super.initRes()
        bindViews.accessAccountIv.setOnClickListener(this)
        bindViews.accessManageIv.setOnClickListener(this)
        bindViews.startVerifyLl.setOnClickListener(this)
        bindViews.verifiedNeedPayDue.setOnClickListener(this)
        bindViews.verifiedNeedPay.setOnClickListener(this)
        bindViews.verifiedFail.setOnClickListener(this)

        HttpClient.eventReport(requireActivity(),ConstConfig.EVENT_HOME_SCREEN,
            ConstConfig.EVENT_ACTION_HOLD,ConstConfig.EVENT_HOME_SCREEN)

        autoShowPermissionSheet()


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.accessAccountIv -> {
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
                        intent.putExtra("orderInfo", orderInfo)
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
        if (homeInfo!!.hahsraev != null && homeInfo!!.hahsraev?.gdcuhe != null) {
            bindViews.totalAmountTv.text =
                context?.getString(R.string.money_symbol) + " " + String.format("%,d", homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn)
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

        } else if (homeInfo!!.hahsraev == null){
            bindViews.accessAccountIv.visibility = View.GONE
            bindViews.startVerifyLl.visibility = View.VISIBLE
            isAuthed = false
            bindViews.verifiedNeedPayDue.visibility = View.GONE
            bindViews.verifiedNeedPay.visibility = View.GONE
            bindViews.verifiedFail.visibility = View.GONE
        } else {
            bindViews.verifiedNeedPay.visibility = View.GONE
            bindViews.verifiedNeedPayDue.visibility = View.GONE
            bindViews.verifiedFail.visibility = View.GONE
            bindViews.llVer.visibility = View.VISIBLE
            bindViews.llHor.visibility = View.GONE
        }
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
        HttpClient.checkCollectDataStatus(requireContext())
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
        hideLoading()
        if (event.isSuccess) {
            event.model?.mtaw?.let {
                currentStep = it.lrksnnsd
                //if (it.rvazxrtziwtcvrfrkzczx) {
                if (it.masxqgkeptyuo) {
                    bindViews.accessAccountIv.visibility = View.VISIBLE
                    bindViews.startVerifyLl.visibility = View.GONE
                    isAuthed = true
                } else {
                    bindViews.accessAccountIv.visibility = View.GONE
                    bindViews.startVerifyLl.visibility = View.VISIBLE
                    isAuthed = false
                    bindViews.verifiedNeedPayDue.visibility = View.GONE
                    bindViews.verifiedNeedPay.visibility = View.GONE
                    bindViews.verifiedFail.visibility = View.GONE
                }
            }
            getHomeData()
        } else {
            ToastUtil.showLong(requireContext(), event.errorMessage.toString())
        }
    }


    @Subscribe
    fun onHomeInfoEvent(event: HomeInfoResponseEvent) {
        if (event.isSuccess) {
            homeInfo = event.model?.mtaw
            if (homeInfo != null) {
                refreshView()
            }
            if(isBackFromVerifyInfoPage){
                previewProduct()
            }
        }
    }

    @Subscribe
    fun onCheckUploadStatusResponseEvent(event: CheckUploadStatusResponseEvent) {
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
                    previewProduct()
                }else {
                    var intent = Intent(requireContext(), VerifyInfoActivity::class.java)
                    intent.putExtra("currentStep", currentStep)
                    verifyInfoLauncher.launch(intent)
                }
            }
        }else{
            ToastUtil.showLong(requireContext(),event.errorMessage.toString())
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

    private fun requestNeedPermissions() {
        try {

            if (EasyPermissions.hasPermissions(requireActivity(), *permissions)) {
                uploadInstalledPackageList()
            } else {
                /*EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(this, REQUEST_CODE, *permissions)
                        .setRationale("Device Permission Required To help identify your device and protect your account, Rupee Cycle requires access to device status information.") //
                        .setPositiveButtonText("Allow")
                        .setNegativeButtonText("Deny")
                        .build()
                )*/

                showRequestPermissionDialog()
            }
        } catch (e: Exception) {
        }
    }

    private fun showRequestPermissionDialog() {
        DialogUtil.showRequestPermissionDialog(
            requireContext(),
            onConfirm = { requestSystemPermissions() },
            onCancel = {
                ToastUtil.showLong(requireContext(), "Please allow permissions to continue")
            }
        )
    }

    private fun requestSystemPermissions() {
        requestPermissions(permissions, REQUEST_CODE)
        /*EasyPermissions.requestPermissions(
            PermissionRequest.Builder(this, REQUEST_CODE, *permissions).build()
        )*/
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
                null
            }
            mainHandler.post {
                if (!isAdded) {
                    finishAppUploadPipeline()
                    return@post
                }
                if (installedList == null) {
                    finishAppUploadPipeline()
                    ToastUtil.showLong(requireContext(), "Failed to collect app list")
                    return@post
                }
                showLoading()
                HttpClient.uploadInstalledPackageList(appContext, installedList)
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
        uploadIoExecutor.execute {
            val deviceInfo = try {
                SystemDataUtils.getDeviceInfo(appContext)
            } catch (e: Exception) {
                null
            }
            mainHandler.post {
                if (!isAdded) {
                    finishAppUploadPipeline()
                    return@post
                }
                if (deviceInfo == null) {
                    finishAppUploadPipeline()
                    ToastUtil.showLong(requireContext(), "Failed to collect device info")
                    return@post
                }
                showLoading()
                HttpClient.uploadSystemInfo(appContext, deviceInfo)
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
                    if (!isRecreditNeeded) {
                        previewProduct()
                    }
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
                    if (privacyPolicyUrl.isEmpty()) {
                        requestNeedPermissions()
                    } else {
                        requestNeedPermissions()
                    }
                } else {
                    if (isCreateOrder) {
                        previewProduct()
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
        if (requestCode == REQUEST_CODE) {
            uploadInstalledPackageList()
        }
    }

    override fun onPermissionsDenied(
        requestCode: Int,
        perms: List<String?>
    ) {
        if (requestCode == REQUEST_CODE) {
            ToastUtil.showLong(requireContext(),"Please allow permissions to continue")
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
            val permissionSheet = PermissionBottomSheet( requireActivity(),
                onRefuseListener = {
                    //requestNeedPermissions()
                    //CacheManager.isNeedShowPermissionSheet = false
                }, onAgreeListener = {
                    requestNeedPermissions()
                    CacheManager.isNeedShowPermissionSheet = false
                })
            permissionSheet.show(requireActivity().supportFragmentManager, "permissionSheet")
        }else{
            requestNeedPermissions()
        }
    }

    private fun showRecreditNeededDialog() {
        DialogUtil.showRecreditNeededDialog(requireContext(), onConfirm = {
            previewProduct()
        }, onCancel = {
        })
    }

    private fun autoShowPermissionSheet() {
        if (CacheManager.isNeedShowPermissionSheet) {
            val permissionSheet = PermissionBottomSheet( requireActivity(),
                onRefuseListener = {

                }, onAgreeListener = {
                    showRequestPermissionDialog()
                    CacheManager.isNeedShowPermissionSheet = false
                })
            permissionSheet.show(requireActivity().supportFragmentManager, "permissionSheet")
        }
    }

    companion object {
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