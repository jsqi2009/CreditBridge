package com.credit.bridge.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.R
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.content.ConstConfig.ORDER_STATUS_ISSUE_FAILED
import com.credit.bridge.databinding.FragmentHomeBinding
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.HomeInfo
import com.credit.bridge.remote.body.RequestHomeInfoBody
import com.credit.bridge.remote.event.CheckCollectDataStatusResponseEvent
import com.credit.bridge.remote.event.CheckUploadStatusResponseEvent
import com.credit.bridge.remote.event.HomeInfoResponseEvent
import com.credit.bridge.remote.event.PrivacyPolicyUrlResponseEvent
import com.credit.bridge.remote.event.UpdateTabIndexEvent
import com.credit.bridge.remote.event.UploadInstalledPackageListResponseEvent
import com.credit.bridge.remote.event.UploadSystemResponseEvent
import com.credit.bridge.ui.order.OrderDetailsActivity
import com.credit.bridge.ui.product.ProductListActivity
import com.credit.bridge.ui.verify.VerifyInfoActivity
import com.credit.bridge.util.DeviceInfoUtil
import com.credit.bridge.util.OrderStatus
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.widget.PermissionBottomSheet
import com.squareup.otto.Subscribe
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

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

        HttpClient.eventReport(requireActivity(),ConstConfig.POINT_HOME_SCREEN,
            ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_HOME_SCREEN)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.accessAccountIv -> {
                isCreateOrder = true
                //checkUploadStatus()
                previewProduct()
            }
            R.id.accessManageIv -> {
                isCreateOrder = true
                //checkUploadStatus()
                homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn?.let {
                    if (it > 0) {
                        previewProduct()
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
        if (homeInfo!!.hahsraev.gdcuhe != null) {
            bindViews.totalAmountTv.text =
                context?.getString(R.string.money_symbol) + " " + homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn.toString()
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
            ToastUtil.showLong(requireContext(), event.networkError.toString())
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
            ToastUtil.showLong(requireContext(),event.networkError.toString())
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
            ToastUtil.showLong(requireContext(), event.networkError.toString())
        }
    }

    private fun requestPermissions() {
        try {

            if (EasyPermissions.hasPermissions(requireActivity(), *permissions)) {
                uploadInstalledPackageList()
            } else {
                EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(this, REQUEST_CODE, *permissions)
                        .setRationale("Device Permission Required To help identify your device and protect your account, Rupee Cycle requires access to device status information.") //
                        .setPositiveButtonText("Allow")
                        .setNegativeButtonText("Deny")
                        .build()
                )
            }
        } catch (e: Exception) {
        }
    }

    private fun uploadInstalledPackageList() {
        showLoading()
        HttpClient.uploadInstalledPackageList(requireContext())
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE])
    @Subscribe
    fun onUploadInstalledPackageListResponseEvent(event: UploadInstalledPackageListResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            uploadSystemInfo()
        }else{
            if(event.model?.fzpn == 500){
                ToastUtil.showLong(requireContext(),event.model?.dvusonb.toString())
            }else{
                ToastUtil.showLong(requireContext(),event.networkError.toString())
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE])
    private fun uploadSystemInfo() {
        HttpClient.uploadSystemInfo(requireContext())
    }

    @Subscribe
    fun onUploadSystemResponseEvent(event: UploadSystemResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            zipDone = true
            if(isCreateOrder){
                previewProduct()
            }else {
                var intent = Intent(requireContext(), VerifyInfoActivity::class.java)
                intent.putExtra("currentStep", currentStep)
                verifyInfoLauncher.launch(intent)
            }
        }else{
            ToastUtil.showLong(requireContext(),event.networkError.toString())}
    }

    fun previewProduct(){
        isBackFromVerifyInfoPage = false
        if (homeInfo?.hahsraev?.lwgdzqyuks == false) {
            ToastUtil.customToastView(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv, Toast.LENGTH_SHORT)
            return
        }
        if (homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn == null || homeInfo?.otytwlcq?.gkdtfbvtbvquxbewhmn == 0) {
            ToastUtil.customToastView(requireContext(), homeInfo?.hahsraev?.hxwklbbxhcxyjbfhv, Toast.LENGTH_SHORT)
            return
        }
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
            ToastUtil.showLong(requireContext(),"Please grant the required permissions to continue.")
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
        val permissionSheet = PermissionBottomSheet( requireActivity(),
            onRefuseListener = {
                requestPermissions()
        }, onAgreeListener = {
                requestPermissions()
        })
        permissionSheet.show(requireActivity().supportFragmentManager, "permissionSheet")
    }

    companion object {
        @JvmStatic
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}