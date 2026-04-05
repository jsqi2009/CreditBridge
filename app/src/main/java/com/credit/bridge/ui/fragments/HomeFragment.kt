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
import com.credit.bridge.databinding.FragmentHomeBinding
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
import com.squareup.otto.Subscribe
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener, EasyPermissions.PermissionCallbacks{
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)


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

        HttpClient.eventReport(requireActivity(),ConstConfig.POINT_HOME_SCREEN,
            ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_HOME_SCREEN)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.accessAccountIv -> {
                startActivity(Intent(requireActivity(), VerifyInfoActivity::class.java))
                isCreateOrder = true
                checkUploadStatus()
            }
            R.id.accessManageIv -> {
                startActivity(Intent(requireActivity(), VerifyInfoActivity::class.java))
                isCreateOrder = true
                checkUploadStatus()
            }
            R.id.startVerifyLl -> {
                startActivity(Intent(requireActivity(), VerifyInfoActivity::class.java))
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
                if(homeInfo?.dlxzautuylahk != null && homeInfo?.dlxzautuylahk?.isNotEmpty() == true){
                    val orderInfo = homeInfo?.dlxzautuylahk?.firstOrNull { it ->
                        OrderStatus.getStatusByValue(it.ufzqlyyxash) == OrderStatus.ISSUE_FAILED
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
        if (homeInfo!!.cavrafds.bovcyr != null) {
            bindViews.totalAmountTv.text =
                context?.getString(R.string.money_symbol) + " " + homeInfo?.wqdwzbxn?.tphuexdptauntagplnu.toString()
            val orderStatus = OrderStatus.getStatusByValue(homeInfo?.cavrafds?.bovcyr)
            when (orderStatus) {
                OrderStatus.CURRENT -> {
                    bindViews.verifiedNeedPay.visibility = View.VISIBLE
                    bindViews.verifiedNeedPayDue.visibility = View.GONE
                    bindViews.verifiedFail.visibility = View.GONE
                }
                OrderStatus.OVERDUE -> {
                    bindViews.verifiedNeedPay.visibility = View.GONE
                    bindViews.verifiedNeedPayDue.visibility = View.VISIBLE
                    bindViews.verifiedFail.visibility = View.GONE
                }
                OrderStatus.ISSUE_FAILED -> {
                    bindViews.verifiedNeedPay.visibility = View.GONE
                    bindViews.verifiedNeedPayDue.visibility = View.GONE
                    bindViews.verifiedFail.visibility = View.VISIBLE
                }
                else -> {
                    bindViews.verifiedNeedPay.visibility = View.GONE
                    bindViews.verifiedNeedPayDue.visibility = View.GONE
                    bindViews.verifiedFail.visibility = View.GONE
                }
            }

        } else {
            bindViews.verifiedNeedPay.visibility = View.GONE
            bindViews.verifiedNeedPayDue.visibility = View.GONE
            bindViews.verifiedFail.visibility = View.GONE
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
        homeInfo.arlscuqt.zgtjlc = vpn
        homeInfo.arlscuqt.ghquezkftxktsz = virtual
        homeInfo.arlscuqt.yxyemrayyinyhk = root

        HttpClient.getHomeInfo(requireContext(), homeInfo)
    }

    @Subscribe
    fun onCheckCollectDataStatusResponseEvent(event: CheckCollectDataStatusResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            event.model?.mtaw?.let {
                currentStep = it.lrksnnsd
                if (it.rvazxrtziwtcvrfrkzczx) {
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
                    //showPermissionPopup()
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
                //showPermissionPopup()
                requestPermissions()
            }
        } else {
            ToastUtil.showLong(requireContext(), event.networkError.toString())
        }
    }

    private fun requestPermissions() {
        try {
            val perms = arrayOf( Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
                uploadInstalledPackageList()
            } else {
                EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(this, REQUEST_CODE, *perms)
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
            uploadZipDevice()
        }else{
            if(event.model?.fzpn == 500){
                ToastUtil.showLong(requireContext(),event.model?.dvusonb.toString())
            }else{
                ToastUtil.showLong(requireContext(),event.networkError.toString())
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE])
    private fun uploadZipDevice() {
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
            ToastUtil.showLong(requireContext(),event.networkError.toString())        }
    }

    fun previewProduct(){
        isBackFromVerifyInfoPage = false
        if (homeInfo?.cavrafds?.denzlkevws == false) {
            ToastUtil.customToastView(requireContext(), homeInfo?.cavrafds?.dzgpjajkkttrvjjqi, Toast.LENGTH_SHORT)
            return
        }
        if (homeInfo?.wqdwzbxn?.tphuexdptauntagplnu == null || homeInfo?.wqdwzbxn?.tphuexdptauntagplnu == 0) {
            ToastUtil.customToastView(requireContext(), homeInfo?.cavrafds?.dzgpjajkkttrvjjqi, Toast.LENGTH_SHORT)
            return
        }
        val intent = Intent(requireContext(), ProductListActivity::class.java)
        intent.putExtra("totalAmount", homeInfo?.wqdwzbxn?.tphuexdptauntagplnu)
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