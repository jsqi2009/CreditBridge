package com.credit.bridge.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.credit.bridge.databinding.BottomSheetStartVerifyBinding
import com.credit.bridge.databinding.BottomSheetVerifyBankBinding
import com.credit.bridge.inter.OnClickListener
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.bean.CommonBean
import com.credit.bridge.util.ToastUtil
import com.google.android.gms.common.internal.service.Common
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartVerifyBottomSheet(
    val mContext: Context,
    var title: String,
    var panNumberOfTimes: Int,
    var takePhoto: () -> Unit,
) : BaseBottomSheet<BottomSheetStartVerifyBinding>(), View.OnClickListener {
    private var mAdapter: CommonListAdapter? = null

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetStartVerifyBinding {
        return BottomSheetStartVerifyBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showExpanded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews.titleTv.text = title
        bindViews.closeIv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.closeIv -> {
                dismiss()
            }
        }
    }

}
