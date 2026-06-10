package com.credit.bridge.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.credit.bridge.R
import com.credit.bridge.util.ScreenUtil
import com.credit.bridge.widget.GlobalLoading
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.apply
import kotlin.let


abstract class BaseBottomSheet<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val bindViews: VB
        get() = _binding!!

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    private var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    private var loadingDialog: GlobalLoading? = null
    open var showExpanded = false
    open var mIsCancelable: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = getBinding(inflater, container)
        return bindViews.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialog)
        isCancelable = mIsCancelable
    }

    @CallSuper
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onCancel(dialog: DialogInterface) {
        if (mIsCancelable) {
            super.onCancel(dialog)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            val dialog = this as? BottomSheetDialog
            bottomSheetBehavior = dialog?.behavior
            bottomSheetBehavior?.isHideable = !mIsCancelable
            bottomSheetBehavior?.setPeekHeight(ScreenUtil.dp2px(context, 400F), false)
            if (showExpanded) {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    protected fun forceExpandState() {
        if (showExpanded) {
            // Force the bottom sheet to be expanded
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    fun dismissSheet() {
        super.dismiss()
    }

    /**
     *
     */
    private fun dp2px(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

    protected fun showLoading() {
        if (!isAdded) return
        val fm = childFragmentManager
        if (fm.findFragmentByTag(GlobalLoading.TAG) != null) return
        if (loadingDialog?.isAdded == true) return
        if (loadingDialog != null) return

        loadingDialog = GlobalLoading.newInstance()
        loadingDialog?.safeShow(fm)
    }

    protected fun hideLoading() {
        loadingDialog?.safeDismiss()
        loadingDialog = null
    }

}