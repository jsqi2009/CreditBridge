package com.credit.bridge.ui.order

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityEditCardBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.body.RequestBankInfoBody
import com.credit.bridge.remote.event.FetchBankInfoResponseEvent
import com.credit.bridge.remote.event.VerifyBankInfoResponseEvent
import com.credit.bridge.remote.response.BankInfo
import com.credit.bridge.util.NumberUtils
import com.credit.bridge.util.ToastUtil
import com.credit.bridge.widget.VerifyBankInfoBottomSheet
import com.squareup.otto.Subscribe

class EditCardActivity : BaseActivity<ActivityEditCardBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityEditCardBinding.inflate(layoutInflater)

    private var bankInfo: BankInfo? = null
    private var editCardKeyboardHandlingActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.backLl.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.text = "Edit Bank Details"

        bindViews.submitTv.setOnClickListener(this)

        bindViews.currentAccountEt.addTextChangedListener(currentAccountTextWatcher)
        bindViews.newAccountEt.addTextChangedListener(newAccountTextWatcher)
        bindViews.confirmNewAccountEt.addTextChangedListener(confirmNewAccountTextWatcher)
        bindViews.newIfscEt.addTextChangedListener(newIfscTextWatcher)

        enableEditCardKeyboardHandling()
        getBankInfo()
    }

    override fun onDestroy() {
        disableEditCardKeyboardHandling()
        super.onDestroy()
    }

    private fun enableEditCardKeyboardHandling() {
        if (editCardKeyboardHandlingActive) return
        editCardKeyboardHandlingActive = true

        val root = bindViews.main
        val defaultBottom = resources.getDimensionPixelSize(R.dimen.bottom_menu_height_64)
        root.setPadding(0, 0, 0, defaultBottom)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.setPadding(0, 0, 0, maxOf(imeBottom, navBottom, defaultBottom))
            scheduleEditCardFocusedFieldScroll()
            insets
        }
        ViewCompat.requestApplyInsets(root)

        val focusScroll = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                scrollEditCardFieldIntoView(v)
            }
        }
        bindViews.currentAccountEt.onFocusChangeListener = focusScroll
        bindViews.newAccountEt.onFocusChangeListener = focusScroll
        bindViews.confirmNewAccountEt.onFocusChangeListener = focusScroll
        bindViews.newIfscEt.onFocusChangeListener = focusScroll
    }

    private fun disableEditCardKeyboardHandling() {
        if (!editCardKeyboardHandlingActive) return
        editCardKeyboardHandlingActive = false

        val root = bindViews.main
        ViewCompat.setOnApplyWindowInsetsListener(root, null)
        root.setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.bottom_menu_height_64))
        bindViews.currentAccountEt.onFocusChangeListener = null
        bindViews.newAccountEt.onFocusChangeListener = null
        bindViews.confirmNewAccountEt.onFocusChangeListener = null
        bindViews.newIfscEt.onFocusChangeListener = null
        ViewCompat.requestApplyInsets(root)
    }

    private fun scheduleEditCardFocusedFieldScroll() {
        val focused = currentFocus ?: return
        if (focused != bindViews.currentAccountEt &&
            focused != bindViews.newAccountEt &&
            focused != bindViews.confirmNewAccountEt &&
            focused != bindViews.newIfscEt
        ) {
            return
        }
        bindViews.editCardScrollView.postDelayed({
            scrollEditCardFieldIntoView(focused)
        }, 80)
    }

    private fun scrollEditCardFieldIntoView(focused: View) {
        val scrollView = bindViews.editCardScrollView
        scrollView.post {
            val content = scrollView.getChildAt(0) ?: return@post
            val rect = Rect()
            focused.getDrawingRect(rect)
            scrollView.offsetDescendantRectToMyCoords(focused, rect)
            rect.bottom += resources.getDimensionPixelSize(R.dimen.margin_20)
            scrollView.requestChildRectangleOnScreen(content, rect, true)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> {
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
        if (event.isSuccess) {
            bankInfo = event.model?.mtaw
            if (!bankInfo?.rcpqzqrn.isNullOrEmpty()) {
                bindViews.ifscTv.text = getString(R.string.product_ifsc) + " " +
                    NumberUtils.formatNumber(bankInfo?.rcpqzqrn, 3, 2)
            }
            if (!bankInfo?.qmtddx.isNullOrEmpty()) {
                bindViews.accountTv.text = getString(R.string.product_account) + " " +
                    NumberUtils.formatNumber(bankInfo?.qmtddx, 3, 2)
                bindViews.currentAccountEt.setText(bankInfo?.qmtddx)
            }
        } else {
            ToastUtil.showLong(this, event.networkError.toString())
        }
    }

    private fun saveBankInfo() {

        val currentAccount = bindViews.currentAccountEt.text.toString()
        val newAccount = bindViews.newAccountEt.text.toString()
        val confirmNewAccount = bindViews.confirmNewAccountEt.text.toString()
        val newIfsc = bindViews.newIfscEt.text.toString()

        if (currentAccount.isEmpty()) {
            ToastUtil.showLong(this, "Please enter your current bank account number")
            return
        }
        if (newAccount.isEmpty()) {
            ToastUtil.showLong(this, "Please enter your new bank account number")
            return
        }
        if (confirmNewAccount.isEmpty()) {
            ToastUtil.showLong(this, "Please confirm your new bank account number")
            return
        }
        if (newIfsc.isEmpty()) {
            ToastUtil.showLong(this, "Please provide a valid IFSC code")
            return
        }
        if (currentAccount.replace(" ", "") != bankInfo?.qmtddx) {
            ToastUtil.showLong(this, "Please enter your current bank account number")
            return
        }
        if (newAccount.replace(" ", "") != confirmNewAccount.replace(" ", "")) {
            ToastUtil.showLong(this, "The account numbers entered do not match")
            return
        }
        if (newIfsc.replace(" ", "").length != 11) {
            ToastUtil.showLong(this, "IFSC code should contain 11 characters")
            return
        }

        showVerifyBankSheet(newIfsc, newAccount)
    }

    private fun showVerifyBankSheet(ifsc: String, account: String) {
        val verifyBankInfoBottomSheet = VerifyBankInfoBottomSheet(
            this, "", ifsc, account, onConfirm = {
                updateBankInfo(it, ifsc, account)
            })
        verifyBankInfoBottomSheet.show(supportFragmentManager, "workTypeSheet")
    }

    private fun updateBankInfo(code: String, ifsc: String, account: String) {

        val body = RequestBankInfoBody()
        body.jmxiec = account
        body.htwvejg = bankInfo?.xzafqxn.toString()
        body.bgsyfqdi = ifsc
        body.lifmsxnfvsgu = account
        body.fzxl = code

        HttpClient.updateBankInfo(this, body)
    }

    @Subscribe
    fun onVerifyBankInfoResponseEvent(event: VerifyBankInfoResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            finish()
        } else {
            if (event.model != null && event.model?.fzpn == 500) {
                ToastUtil.showLong(this, event.model?.dvusonb)
            } else {
                ToastUtil.showLong(this, event.networkError.toString())
            }
        }
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

    private var newIfscTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s.toString()
            val formatted = text.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
            if (formatted != text) {
                bindViews.newIfscEt.setText(formatted)
                bindViews.newIfscEt.setSelection(bindViews.newIfscEt.text.toString().length)
            }
        }
    }
}
