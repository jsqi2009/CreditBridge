package com.credit.bridge.ui.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.ActivityLoginBinding
import com.credit.bridge.databinding.ActivityOrderDetailsBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.bean.OrderInfo
import com.credit.bridge.remote.body.RequestOrderDetailsBody
import com.credit.bridge.remote.event.OrderDetailsResponseEvent
import com.credit.bridge.remote.event.OrderUpdateResponseEvent
import com.credit.bridge.remote.event.PaymentLinkDetailsResponseEvent
import com.credit.bridge.ui.RootActivity
import com.credit.bridge.util.AppUtil
import com.credit.bridge.util.NumberUtils
import com.credit.bridge.util.OrderStatus
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe
import java.util.Locale

class OrderDetailsActivity : BaseActivity<ActivityOrderDetailsBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityOrderDetailsBinding.inflate(layoutInflater)

    private var orderInfo: OrderInfo? = null
    private var orderId: Int = 0
    private val LOCK_DATE_FORMAT = "dd-MM-yyyy"
    private var isExtend: Boolean = false
    private lateinit var myCountDownTimer: CountDownTimer
    val totalMillis = 2 * 24 * 60 * 60 * 1000L
    var orderStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    override fun onResume() {
        super.onResume()
        if (!isExtend) {
            getOrderDetailsInfo()
        }else{
            getOrderUpdateInfo()
        }
    }

    override fun initRes() {
        super.initRes()
        orderInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("info", OrderInfo::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("info") as? OrderInfo
        }
        orderId = orderInfo?.kcyrbnp ?: 0
        orderStatus = orderInfo?.xjywdrtdxzt
//        orderStatus = ConstConfig.ORDER_STATUS_ISSUE_FAILED
        if (intent.hasExtra("isExtend")) {
            bindViews.continueTv.visibility = View.VISIBLE
            isExtend = intent.getBooleanExtra("isExtend", false)
            getOrderUpdateInfo()
        } else {
            bindViews.continueTv.visibility = View.GONE
        }

        bindViews.titleLayout.titleTv.text = "Details"

        bindViews.titleLayout.backLl.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.editBankTv.setOnClickListener(this)
        bindViews.continuePaymentTv.setOnClickListener(this)
        bindViews.viewPaymentOptionsTv.setOnClickListener(this)
        bindViews.continueTv.setOnClickListener(this)

        if (orderInfo?.gphysdjxvns == true || isExtend) {
            initExtendInfo()
        } else {
            initOrderDetailsInfo()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> {
                finish()
            }
            R.id.titleTv -> {
                ToastUtil.showShort(this, "Right")
            }
            R.id.editBankTv -> {
                startActivity(Intent(this@OrderDetailsActivity, EditCardActivity::class.java))
            }
            R.id.viewPaymentOptionsTv -> {
                if (orderInfo?.umoatyothkt == true) {
                    isExtend =  true
                    getOrderUpdateInfo()
                }
            }
            R.id.continuePaymentTv -> {
                getPaymentLink()
            }
            R.id.continueTv -> {
                getPaymentLink2()
            }
        }
    }

    private fun getOrderDetailsInfo() {

        val requestBody = RequestOrderDetailsBody()
        requestBody.stfexlk = orderInfo?.kcyrbnp.toString()

        showLoading()
        HttpClient.getOrderDetails(this, requestBody, ConstConfig.ORDER_DETAIL_COMMON)
    }

    @Subscribe
    fun onOrderDetailsResponseEvent(event: OrderDetailsResponseEvent) {
        hideLoading()
        if (event.model == null) return
        if (event.model?.flag == ConstConfig.ORDER_DETAIL_COMMON) {
            if (event.isSuccess) {
                orderInfo = event.model?.mtaw
                //initOrderDetailsInfo()
                if (orderInfo?.gphysdjxvns == true || isExtend) {
                    initExtendInfo()
                } else {
                    initOrderDetailsInfo()
                    handelRejectedOrder()
                }
            }
        }
    }

    private fun handelRejectedOrder() {
        if (orderStatus == ConstConfig.ORDER_STATUS_REJECTED) {
            val leftTime = AppUtil.getTotalSeconds(orderInfo?.vzlwrta ?: "")
            if (leftTime > 0) {
                bindViews.cancelFrozenLayout.rootView.visibility = View.VISIBLE
                bindViews.cancelLayout.rootView.visibility = View.GONE
                bindViews.cancelFrozenLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.cancelFrozenLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.cancelFrozenLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }
                startCountdownTimer(leftTime)
            } else {
                bindViews.cancelLayout.rootView.visibility = View.VISIBLE
                bindViews.cancelFrozenLayout.rootView.visibility = View.GONE
                bindViews.cancelLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.cancelLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.cancelLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }
            }
        }
    }

    private fun getPaymentLink() {
        showLoading()
        HttpClient.getPaymentLink(this, false, orderId.toString(), 2)
    }

    private fun getPaymentLink2() {
        showLoading()
        HttpClient.getPaymentLink(this, true, orderId.toString(), 2)
    }

    @Subscribe
    fun onPaymentLinkDetailsResponseEvent(event: PaymentLinkDetailsResponseEvent) {
        hideLoading()
        if(event.isSuccess){
            try {
                val link = event.model?.mtaw ?: return
                val uri = link.toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: Exception) {
            }
        }else{
            ToastUtil.showLong(this,event.networkError.toString())
        }
    }

    //zhan qi
    private fun getOrderUpdateInfo() {
        showLoading()
        HttpClient.getOrderUpdateInfo(this, orderInfo?.ksczvtrzbqru ?: 0 ,orderId)
    }

    @SuppressLint("SuspiciousIndentation")
    @Subscribe
    fun onOrderUpdateResponseEvent(event: OrderUpdateResponseEvent) {
        hideLoading()
        if (event.model == null) return
        if (event.isSuccess) {
            initExtendInfo()
            event.model?.mtaw?.let {

                bindViews.extendLayout.dueDateTv.text = it.nbnkkcnyigtwzm
                bindViews.extendLayout.chargeTv.text = getString(R.string.money_symbol) + " " + NumberUtils.formatIntToStr(it.cmjrzfchjdqfyqdadyoxqlurcf)
                bindViews.extendLayout.nextStatementDateTv.text = it.wdtuvzvegzujzpwa
            }
        }
    }

    private fun initExtendInfo() {
        try {
            bindViews.continueTv.visibility = View.VISIBLE

            bindViews.titleLayout.titleTv.text = "Bill update"
            bindViews.processingLayout.rootView.visibility = View.GONE
            bindViews.tipsLayout.visibility = View.GONE
            bindViews.paidLayout.rootView.visibility = View.GONE
            bindViews.overdueLayout.rootView.visibility = View.GONE
            bindViews.cancelLayout.rootView.visibility = View.GONE
            bindViews.cancelFrozenLayout.rootView.visibility = View.GONE
            bindViews.dueLayout.rootView.visibility = View.GONE
            bindViews.failureLayout.rootView.visibility = View.GONE
            bindViews.extendLayout.rootView.visibility = View.VISIBLE
            bindViews.continuePaymentTv.visibility = View.GONE
            bindViews.viewPaymentOptionsTv.visibility = View.GONE

            bindViews.extendLayout.dueDateTv.text = orderInfo?.vzlwrta
            bindViews.extendLayout.chargeTv.text = getString(R.string.money_symbol) + " " +
                    orderInfo?.vimzxivnoztqbclsxanyxuhx.toString()
            bindViews.extendLayout.nextStatementDateTv.text = orderInfo?.vzlwrta
        } catch (e: Exception) {

        }
    }


    @SuppressLint("SetTextI18n")
    private fun initOrderDetailsInfo() {
        if (orderInfo == null) {
            return
        }
        when (orderStatus) {
            ConstConfig.ORDER_STATUS_PRE_REVIEW, ConstConfig.ORDER_STATUS_ISSUING -> {
                bindViews.processingLayout.rootView.visibility = View.VISIBLE

                bindViews.processingLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.processingLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.processingLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }
                bindViews.processingLayout.ifscTv.text = NumberUtils.formatNumber(orderInfo?.eqzbyofrbkzo,3,2)
                bindViews.processingLayout.accountTv.text = NumberUtils.formatNumber(orderInfo?.ifhldxjdjt,3,2)

            }
            ConstConfig.ORDER_STATUS_OVERDUE -> {
                bindViews.overdueLayout.rootView.visibility = View.VISIBLE
                bindViews.continuePaymentTv.visibility = View.VISIBLE
                bindViews.viewPaymentOptionsTv.visibility = View.VISIBLE

                bindViews.overdueLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.overdueLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.overdueLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }
                bindViews.overdueLayout.dueDateTv.text = orderInfo?.vzlwrta.toString()
                bindViews.overdueLayout.dueDurationTv.text = orderInfo?.qpruccpdjot.toString() + " Days"
                bindViews.overdueLayout.durChargesTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.vimzxivnoztqbclsxanyxuhx?.let { NumberUtils.formatIntToStr(it) }
                bindViews.overdueLayout.totalAmountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.kmlwyjhlacigctavsolh?.let { NumberUtils.formatIntToStr(it) }

                orderInfo?.qpruccpdjot?.let {
                    if (it.toInt() > 7) {
                        bindViews.viewPaymentOptionsTv.visibility = View.GONE
                    }
                }
            }
            ConstConfig.ORDER_STATUS_CURRENT -> {
                bindViews.dueLayout.rootView.visibility = View.VISIBLE
                bindViews.continuePaymentTv.visibility = View.VISIBLE
                bindViews.viewPaymentOptionsTv.visibility = View.VISIBLE

                bindViews.dueLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.dueLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.dueLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }
                bindViews.dueLayout.dueDateTv.text = orderInfo?.vzlwrta.toString()
                bindViews.dueLayout.amountDueTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.kgchobzqirjuftermzzgajda?.let { NumberUtils.formatIntToStr(it) }
            }
            ConstConfig.ORDER_STATUS_PAID_OFF -> {
                bindViews.paidLayout.rootView.visibility = View.VISIBLE

                bindViews.paidLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.paidLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.paidLayout.amountPaidTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.ascgbdbkkcdkzhqxvfa?.let { NumberUtils.formatIntToStr(it) }
                bindViews.paidLayout.paymentDateTv.text = orderInfo?.didahlwwwkrte
            }
            ConstConfig.ORDER_STATUS_REJECTED -> {
                bindViews.cancelLayout.rootView.visibility = View.VISIBLE

                bindViews.cancelLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.cancelLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.cancelLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }
            }
            ConstConfig.ORDER_STATUS_ISSUE_FAILED -> {
                bindViews.failureLayout.rootView.visibility = View.VISIBLE
                bindViews.editBankTv.visibility = View.VISIBLE

                bindViews.titleImage.setImageResource(R.mipmap.ic_fail_title)

                bindViews.titleLayout.titleTv.text = "Request Not Completed"
                bindViews.tipsTv.text = getString(R.string.product_tips_failure)
                bindViews.failureLayout.ifscTv.text = NumberUtils.formatNumber(orderInfo?.eqzbyofrbkzo,3,2)
                bindViews.failureLayout.accountTv.text = NumberUtils.formatNumber(orderInfo?.ifhldxjdjt,3,2)
                bindViews.failureLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.failureLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.failureLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }
            }
            ConstConfig.ORDER_STATUS_CLOSED -> {

                bindViews.cancelLayout.rootView.visibility = View.VISIBLE

                bindViews.cancelLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.cancelLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.cancelLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }

                /*bindViews.extendLayout.rootView.visibility = View.VISIBLE
                bindViews.continueTv.visibility = View.VISIBLE

                bindViews.extendLayout.dueDateTv.text = orderInfo?.vzlwrta
                bindViews.extendLayout.chargeTv.text = orderInfo?.vimzxivnoztqbclsxanyxuhx.toString()
                bindViews.extendLayout.nextStatementDateTv.text = orderInfo?.pujfiulsldnnbtb
                bindViews.extendLayout.rootView.visibility = View.GONE
                bindViews.continueTv.visibility = View.GONE*/
            }
            else -> {
                bindViews.cancelLayout.rootView.visibility = View.VISIBLE

                bindViews.cancelLayout.dateTv.text = orderInfo?.dhqprsdsv
                bindViews.cancelLayout.usageIdTv.text = orderInfo?.kcyrbnp.toString()
                bindViews.cancelLayout.amountTv.text = getString(R.string.money_symbol) + " " +
                        orderInfo?.otjjqwdpupp?.let { NumberUtils.formatIntToStr(it) }
            }

        }
    }



    private fun startCountdownTimer(totalMillis: Long) {
        myCountDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val days = totalSeconds / 86400
                val hours = (totalSeconds % 86400) / 3600
                bindViews.cancelFrozenLayout.daysTv.text =
                    String.format(Locale.getDefault(), "%02d", days)
                bindViews.cancelFrozenLayout.hoursTv.text =
                    String.format(Locale.getDefault(), "%d", hours)
            }

            override fun onFinish() {
            }
        }

        myCountDownTimer.start()
    }
}