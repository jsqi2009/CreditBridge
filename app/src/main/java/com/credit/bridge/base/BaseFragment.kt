package com.credit.bridge.base

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.credit.bridge.content.AndroidBus
import com.credit.bridge.ui.App
import com.credit.bridge.widget.GlobalLoading

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val views: VB
        get() = _binding!!

    var eventBus: AndroidBus? = null
    private var loadingDialog: GlobalLoading? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus = App[requireActivity()].eventBus
        eventBus!!.register(this)
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = getBinding(inflater, container)
        initRes()
        return views.root
    }

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroyView() {
        //_binding = null
        eventBus!!.unregister(this)
        if (loadingDialog != null) {
            loadingDialog?.safeDismiss();
        }
        super.onDestroyView()
    }

    open fun getMenuRes() = -1

    open fun initRes() = Unit

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuRes = getMenuRes()

        if (menuRes != -1) {
            inflater.inflate(menuRes, menu)
        }
    }

    protected fun showLoading() {
        if (!isAdded()) return

        if (loadingDialog == null) {
            loadingDialog = GlobalLoading.newInstance()
        }
        loadingDialog!!.safeShow(getChildFragmentManager())
    }

    protected fun hideLoading() {
        if (loadingDialog != null) {
            loadingDialog!!.safeDismiss()
        }
    }


}