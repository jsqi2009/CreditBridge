package com.credit.bridge.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.R
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.databinding.FragmentHomeBinding
import com.credit.bridge.ui.product.ProductListActivity
import com.credit.bridge.ui.verify.VerifyInfoActivity
import com.credit.bridge.util.DeviceInfoUtil
import pub.devrel.easypermissions.EasyPermissions

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener, EasyPermissions.PermissionCallbacks{
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            //HttpClient.collectDataIntegrity(requireContext())
        }
    }
    override fun initRes() {
        super.initRes()
        bindViews.accessAccountIv.setOnClickListener(this)
        bindViews.accessManageIv.setOnClickListener(this)

        /*val eventValue =  HashMap<String, Any>()
        eventValue[ConstConfig.POINT_HOME_SCREEN] = ""
        AppsFlyerLib.getInstance().logEvent(requireContext(), ConstConfig.POINT_HOME_SCREEN, eventValue)
        PointUploadUtils.uploadEvent(requireActivity() as AppCompatActivity,ConstConfig.POINT_ACTION_TYPE_HOLD,ConstConfig.POINT_HOME_SCREEN)*/

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

    private fun checkZipStatus() {
        showLoading()
        //HttpClient.checkUploadZip(requireContext())
    }

    private fun fetchHomeData() {

        val isVirtualMachine = DeviceInfoUtil.isVirtualMachine()
        val isUseVpn = DeviceInfoUtil.isUseVpn(requireContext())
        val isRoot = DeviceInfoUtil.isRoot()

        val vpn = if (isUseVpn) 1 else 0
        val root = if (isRoot) 1 else 0
        val virtual = if (isVirtualMachine) 1 else 0

        /*val requestBody = HomeInfoRequestBody()

        requestBody.arlscuqt.zgtjlc = vpn
        requestBody.arlscuqt.ghquezkftxktsz = virtual
        requestBody.arlscuqt.yxyemrayyinyhk = root

        HttpClient.fetchHomeInfo(requireContext(), requestBody)*/
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