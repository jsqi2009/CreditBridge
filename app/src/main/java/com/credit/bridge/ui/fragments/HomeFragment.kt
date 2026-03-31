package com.credit.bridge.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.credit.bridge.remote.event.HomeInfoResponseEvent
import com.credit.bridge.ui.product.ProductListActivity
import com.credit.bridge.ui.verify.VerifyInfoActivity
import com.credit.bridge.util.DeviceInfoUtil
import com.credit.bridge.util.OrderStatus
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe
import pub.devrel.easypermissions.EasyPermissions

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener, EasyPermissions.PermissionCallbacks{
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)


    private var isAuthed = false
    private var isBackFromVerifyInfoPage = false
    var currentStep = 0
    var homeInfo: HomeInfo? = null


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
            }
            R.id.accessManageIv -> {
                startActivity(Intent(requireActivity(), ProductListActivity::class.java))
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

    private fun checkZipStatus() {
        showLoading()
        //HttpClient.checkUploadZip(requireContext())
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
            event.model?.blvb?.let {
                currentStep = it.jkeurrbf
                if (it.vovobiifulrzpxjxcoqkb) {
                    bindViews.accessManageIv.visibility = View.VISIBLE
                    bindViews.startVerifyLl.visibility = View.GONE
                    isAuthed = true
                } else {
                    bindViews.accessManageIv.visibility = View.GONE
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
            homeInfo = event.model?.blvb
            if (homeInfo != null) {
                refreshView()
            }
            if(isBackFromVerifyInfoPage){
                previewProduct()
            }
        }
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

    }

    override fun onPermissionsDenied(
        requestCode: Int,
        perms: List<String?>
    ) {

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