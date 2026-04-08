package com.credit.bridge.ui.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityEditCardBinding
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.inter.OnSelectListener
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.event.FetchBankInfoResponseEvent
import com.credit.bridge.remote.response.BankInfo
import com.credit.bridge.util.NumberUtils
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.util.VerifyInfoUtil
import com.credit.bridge.widget.PermissionBottomSheet
import com.credit.bridge.widget.VerifyBankInfoBottomSheet
import com.squareup.otto.Subscribe

class EditCardActivity : BaseActivity<ActivityEditCardBinding>(), View.OnClickListener {


    override fun getBinding() = ActivityEditCardBinding.inflate(layoutInflater)


    private var bankInfo: BankInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.backIv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.text = "Edit Bank Details"

        bindViews.submitTv.setOnClickListener(this)

        bindViews.currentAccountEt.addTextChangedListener(currentAccountTextWatcher)
        bindViews.newAccountEt.addTextChangedListener(newAccountTextWatcher)
        bindViews.confirmNewAccountEt.addTextChangedListener(confirmNewAccountTextWatcher)

        getBankInfo()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.submitTv -> {
                saveBankInfo()
            }
        }
    }

    private fun getBankInfo() {
        showLoading()
        HttpClient.fetchBankInfo(this)
    }

    @Subscribe
    fun onFetchBankInfoResponseEvent(event: FetchBankInfoResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            bankInfo = event.model?.mtaw
            if (!bankInfo?.rcpqzqrn.isNullOrEmpty()) {
                bindViews.ifscTv.text = getString(R.string.product_ifsc) + " " +
                        NumberUtils.formatNumber(bankInfo?.rcpqzqrn,3,2)
            }
            if (!bankInfo?.qmtddx.isNullOrEmpty()) {
                bindViews.accountTv.text = getString(R.string.product_account) + " " +
                        NumberUtils.formatNumber(bankInfo?.qmtddx,3,2)
                bindViews.currentAccountEt.setText(bankInfo?.qmtddx)
            }
        }else{
            ToastUtil.showLong(this,event.networkError.toString())
        }
    }

    private fun saveBankInfo() {

        val currentAccount = bindViews.currentAccountEt.text.toString()
        val newAccount = bindViews.newAccountEt.text.toString()
        val confirmNewAccount = bindViews.confirmNewAccountEt.text.toString()
        val newIfsc = bindViews.newIfscEt.text.toString()

        if (currentAccount.isEmpty()) {
            ToastUtil.showLong(this, "Current account number cannot be empty")
            return
        }
        if (newAccount.isEmpty()) {
            ToastUtil.showLong(this, "New bank account number cannot be empty")
            return
        }
        if (confirmNewAccount.isEmpty()) {
            ToastUtil.showLong(this, "Please re-enter the new bank account number")
            return
        }
        if (newIfsc.isEmpty()) {
            ToastUtil.showLong(this, "IFSC code cannot be empty")
            return
        }
        if (currentAccount.replace(" ","") != bankInfo?.qmtddx) {
            ToastUtil.showLong(this, "Please enter your current bank account number")
            return
        }
        if (newAccount.replace(" ","") != confirmNewAccount.replace(" ","")) {
            ToastUtil.showLong(this, "Account number and re-entered account number must match")
            return
        }
        if (newIfsc.length != 11) {
            ToastUtil.showLong(this, "IFSC code must be 11 characters")
            return
        }

        showVerifyBankSheet()
    }

    private fun showVerifyBankSheet() {
        val verifyBankInfoBottomSheet = VerifyBankInfoBottomSheet(
            this, "Employment Status", VerifyInfoUtil.getWorkTypeList(),
            -1, object : OnSelectListener {
                override fun onSelect(index: Int) {
                    ToastUtil.showShort(this@EditCardActivity, "Select: $index")
                }
            })
        verifyBankInfoBottomSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private var currentAccountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s.toString()
            val formatted = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
            if (formatted != text) {
                bindViews.currentAccountEt.setText(formatted)
                bindViews.currentAccountEt.setSelection(bindViews.currentAccountEt.text.toString().length)
            }
        }
    }

    private var newAccountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s.toString()
            val formatted = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
            if (formatted != text) {
                bindViews.newAccountEt.setText(formatted)
                bindViews.newAccountEt.setSelection(bindViews.newAccountEt.text.toString().length)
            }
        }
    }

    private var confirmNewAccountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s.toString()
            val formatted = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
            if (formatted != text) {
                bindViews.confirmNewAccountEt.setText(formatted)
                bindViews.confirmNewAccountEt.setSelection(bindViews.confirmNewAccountEt.text.toString().length)
            }
        }
    }
}