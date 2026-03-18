package com.credit.bridge.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.credit.bridge.R
import com.credit.bridge.base.BaseFragment
import com.credit.bridge.databinding.FragmentHomeBinding
import pub.devrel.easypermissions.EasyPermissions

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener, EasyPermissions.PermissionCallbacks{
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(v: View?) {
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