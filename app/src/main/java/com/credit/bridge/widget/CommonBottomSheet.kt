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
import com.credit.bridge.databinding.BottomSheetVerifyBankBinding
import com.credit.bridge.inter.OnClickListener
import com.credit.bridge.inter.OnConfirmListener
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.bean.CommonBean
import com.credit.bridge.util.ToastUtil
import com.google.android.gms.common.internal.service.Common
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CommonBottomSheet(
    val mContext: Context,
    var title: String,
    var dataList: ArrayList<CommonBean>?,
    var selectIndex: Int,
    var onSelectListener: OnSelectListener
) : BaseBottomSheet<BottomSheetCommonBinding>(), View.OnClickListener {
    private var mAdapter: CommonListAdapter? = null

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetCommonBinding {
        return BottomSheetCommonBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showExpanded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews.titleTv.text = title
        bindViews.dismissIv.setOnClickListener(this)

        if (!dataList.isNullOrEmpty()) {
            initAdapter()
        }
    }

    private fun initAdapter() {
        bindViews.recyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = CommonListAdapter(mContext, selectIndex,
            dataList!!, object : OnClickListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onClick(index: Int) {
                    selectIndex = index
                    mAdapter?.selectedIndex = index
                    mAdapter?.notifyDataSetChanged()
                    if (selectIndex != -1) {
                        onSelectListener.onSelect(selectIndex)
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        dismiss()
                    }, 300)
                }
            })

        bindViews.recyclerView.adapter = mAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dismissIv -> {
                dismiss()
            }
        }
    }

}
