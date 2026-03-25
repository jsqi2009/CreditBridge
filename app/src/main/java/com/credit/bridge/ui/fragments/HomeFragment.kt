package com.credit.bridge.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.credit.bridge.R
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.databinding.FragmentHomeBinding
import com.credit.bridge.ui.verify.VerifyInfoActivity
import pub.devrel.easypermissions.EasyPermissions

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener, EasyPermissions.PermissionCallbacks{
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initRes() {
        super.initRes()
        bindViews.accessAccountIv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.accessAccountIv -> {
                startActivity(Intent(requireActivity(), VerifyInfoActivity::class.java))
            }
        }
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