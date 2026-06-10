package com.credit.bridge.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.credit.bridge.R


/**
 * author : Jason
 * date   : 2026/3/17 22:24
 * desc   :
 */
class GlobalLoading : DialogFragment() {
    public override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.LogingDialog)
        setCancelable(false) // disable back button
    }

    public override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.global_loading, container, false)
    }

    public override fun onStart() {
        super.onStart()

        if (dialog != null && dialog?.getWindow() != null) {
            getDialog()?.getWindow()?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun safeShow(manager: FragmentManager?) {
        if (manager == null || isAdded) return
        if (manager.findFragmentByTag(TAG) != null) return
        if (manager.isStateSaved) return
        show(manager, TAG)
    }

    fun safeDismiss() {
        if (getFragmentManager() == null) return

        getFragmentManager()?.let {
            it?.isStateSaved()?.let { it1 ->
                if (!it1) {
                    dismiss()
                } else {
                    dismissAllowingStateLoss()
                }
            }
        }
    }

    companion object {
        const val TAG = "CommonLoadingDialog"

        fun newInstance(): GlobalLoading {
            return GlobalLoading()
        }
    }
}