package com.credit.bridge.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.credit.bridge.R
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetPemissionBinding

class PermissionBottomSheet(
    val mContext: Context,
    val onRefuseListener: () -> Unit,
    val onAgreeListener: () -> Unit
) : BaseBottomSheet<BottomSheetPemissionBinding>(), View.OnClickListener {

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetPemissionBinding {
        return BottomSheetPemissionBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showExpanded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews.dismissIv.setOnClickListener(this)
        bindViews.refuseTv.setOnClickListener(this)
        bindViews.agreeTv.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dismissIv -> {
                dismiss()
            }
            R.id.refuseTv -> {
                dismiss()
                onRefuseListener.invoke()
            }
            R.id.agreeTv -> {
                dismiss()
                onAgreeListener.invoke()
            }
        }
    }

}
