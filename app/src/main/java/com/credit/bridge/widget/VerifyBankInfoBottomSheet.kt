package com.credit.bridge.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.credit.bridge.R
import com.credit.bridge.adapter.CommonListAdapter
import com.credit.bridge.base.BaseBottomSheet
import com.credit.bridge.databinding.BottomSheetCommonBinding
import com.credit.bridge.databinding.BottomSheetPemissionBinding
import com.credit.bridge.databinding.BottomSheetVerifyBankBinding
import com.credit.bridge.databinding.BottomSheetVerifyBankInfoBinding
import com.credit.bridge.inter.OnClickListener
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.bean.CommonBean
import com.credit.bridge.util.ToastUtil
import com.google.android.gms.common.internal.service.Common
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyBankInfoBottomSheet(
    val mContext: Context,
    var title: String,
    var ifsc: String,
    var account: String,
    var onConfirm: () -> Unit
) : BaseBottomSheet<BottomSheetVerifyBankInfoBinding>(), View.OnClickListener {
    private var mAdapter: CommonListAdapter? = null

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetVerifyBankInfoBinding {
        return BottomSheetVerifyBankInfoBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showExpanded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bindViews.dismissIv.setOnClickListener(this)
        bindViews.cancelTv.setOnClickListener(this)
        bindViews.confirmTv.setOnClickListener(this)

        initRes()
    }

    private fun initRes() {
        bindViews.sendTv.paint.isUnderlineText = true
        bindViews.verifyVoiceTv.paint.isUnderlineText = true

        bindViews.accountTv.text = formatValue(account)
        bindViews.ifscTv.text = formatValue(ifsc)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dismissIv -> {
                dismiss()
            }
            R.id.cancelTv -> {
                dismiss()
            }
            R.id.confirmTv -> {
                if (bindViews.codeEt.toString().isEmpty()) {
                    ToastUtil.showLong(requireContext(), "Verification code cannot be empty")
                    return
                }
                onConfirm.invoke()
                dismiss()
            }
        }
    }

    private fun formatValue(text: String): String{
        val newValue = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
        return newValue
    }


}
