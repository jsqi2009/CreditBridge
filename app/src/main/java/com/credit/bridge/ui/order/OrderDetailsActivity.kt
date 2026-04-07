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
import com.credit.bridge.util.OrderStatus
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe

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
        if (intent.hasExtra("isExtend")) {
            isExtend = intent.getBooleanExtra("isExtend", false)
        }

        bindViews.titleLayout.titleTv.text = "Details"

        bindViews.titleLayout.backIv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.editBankTv.setOnClickListener(this)

        initOrderDetailsInfo()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backIv -> {
                finish()
            }
            R.id.titleTv -> {
                ToastUtil.showShort(this, "Right")
            }
            R.id.editBankTv -> {
                startActivity(Intent(this@OrderDetailsActivity, EditCardActivity::class.java))
            }
        }
    }

    private fun getOrderDetailsInfo() {

        val requestBody = RequestOrderDetailsBody()
        requestBody.bsmweqe = orderInfo?.kcyrbnp.toString()

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
                initOrderDetailsInfo()
                if (orderStatus == ConstConfig.ORDER_STATUS_REJECTED) {
                    val leftTime = AppUtil.getTotalSeconds(orderInfo?.heieiavicbvpq ?: "")
                    if (leftTime > 0) {
                        /*views.rlCountDown.visibility = View.VISIBLE
                        views.tvCancelDesc.visibility = View.GONE*/
                        startCountdownTimer(leftTime)
                    } else {
                        /*views.rlCountDown.visibility = View.GONE
                        views.tvCancelDesc.visibility = View.VISIBLE*/
                    }
                }
            }
        }
    }

    private fun getPaymentLink() {
        showLoading()
        HttpClient.getPaymentLink(this, false,orderId.toString(), 2)
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

    private fun initOrderDetailsInfo() {
        if (orderInfo == null) {
            return
        }
        when (orderStatus) {
            ConstConfig.ORDER_STATUS_PRE_REVIEW, ConstConfig.ORDER_STATUS_ISSUING -> {
                bindViews.processingLayout.rootView.visibility = View.VISIBLE

                bindViews.processingLayout.dateTv.text = ""
                bindViews.processingLayout.usageIdTv.text = ""
                bindViews.processingLayout.amountTv.text = ""
                bindViews.processingLayout.ifscTv.text = ""
                bindViews.processingLayout.accountTv.text = ""
            }
            ConstConfig.ORDER_STATUS_OVERDUE -> {
                bindViews.overdueLayout.rootView.visibility = View.VISIBLE
                bindViews.continuePaymentTv.visibility = View.VISIBLE
                bindViews.viewPaymentOptionsTv.visibility = View.VISIBLE

                bindViews.overdueLayout.dateTv.text = ""
                bindViews.overdueLayout.usageIdTv.text = ""
                bindViews.overdueLayout.amountTv.text = ""
                bindViews.overdueLayout.dueDateTv.text = ""
                bindViews.overdueLayout.dueDurationTv.text = ""
                bindViews.overdueLayout.durChargesTv.text = ""
                bindViews.overdueLayout.totalAmountTv.text = ""
            }
            ConstConfig.ORDER_STATUS_CURRENT -> {
                bindViews.dueLayout.rootView.visibility = View.VISIBLE
                bindViews.continuePaymentTv.visibility = View.VISIBLE
                bindViews.viewPaymentOptionsTv.visibility = View.VISIBLE

                bindViews.dueLayout.dateTv.text = ""
                bindViews.dueLayout.usageIdTv.text = ""
                bindViews.dueLayout.amountTv.text = ""
                bindViews.dueLayout.dueDateTv.text = ""
                bindViews.dueLayout.amountDueTv.text = ""
            }
            ConstConfig.ORDER_STATUS_PAID_OFF -> {
                bindViews.paidLayout.rootView.visibility = View.VISIBLE

                bindViews.paidLayout.dateTv.text = ""
                bindViews.paidLayout.usageIdTv.text = ""
                bindViews.paidLayout.amountTv.text = ""
                bindViews.paidLayout.amountDueTv.text = ""
            }
            ConstConfig.ORDER_STATUS_REJECTED -> {
                bindViews.cancelLayout.rootView.visibility = View.VISIBLE

                bindViews.cancelLayout.dateTv.text = ""
                bindViews.cancelLayout.usageIdTv.text = ""
                bindViews.cancelLayout.amountTv.text = ""
            }
            ConstConfig.ORDER_STATUS_ISSUE_FAILED -> {
                bindViews.cancelFrozenLayout.rootView.visibility = View.VISIBLE

                bindViews.cancelFrozenLayout.dateTv.text = ""
                bindViews.cancelFrozenLayout.usageIdTv.text = ""
                bindViews.cancelFrozenLayout.amountTv.text = ""
            }
            ConstConfig.ORDER_STATUS_CLOSED -> {
                bindViews.extendLayout.rootView.visibility = View.VISIBLE
                bindViews.continueTv.visibility = View.VISIBLE

                bindViews.extendLayout.dueDateTv.text = ""
                bindViews.extendLayout.chargeTv.text = ""
                bindViews.extendLayout.nextStatementDateTv.text = ""
                bindViews.extendLayout.rootView.visibility = View.GONE
                bindViews.continueTv.visibility = View.GONE
            }
            else -> {
                bindViews.failureLayout.rootView.visibility = View.VISIBLE
                bindViews.editBankTv.visibility = View.VISIBLE

                bindViews.failureLayout.ifscTv.text = ""
                bindViews.failureLayout.accountTv.text = ""
                bindViews.failureLayout.dateTv.text = ""
                bindViews.failureLayout.usageIdTv.text = ""
                bindViews.failureLayout.amountTv.text = ""
            }

        }
    }

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
            event.model?.mtaw?.let {
                //views.tvExtendFee.text = "₹ "+CommonUtils.formatFloatToStr(it.usiyspmxkqopjxmcbbbxijkiag)
            }
        }
    }


    private fun startCountdownTimer(totalMillis: Long) {
        myCountDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val days = totalSeconds / 86400
                val hours = (totalSeconds % 86400) / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60

                /*updateTwoDigits(views.tvDayTens, views.tvDayOnes, days)
                updateTwoDigits(views.tvHourTens, views.tvHourOnes, hours)
                updateTwoDigits(views.tvMinuteTens, views.tvMinuteOnes, minutes)
                updateTwoDigits(views.tvSecondTens, views.tvSecondOnes, seconds)*/
            }

            override fun onFinish() {
            }
        }

        myCountDownTimer.start()
    }

    private fun updateLeftTimeText(tens: TextView, ones: TextView, value: Long) {
        val str = value.toString().padStart(2, '0')
        tens.text = str[0].toString()
        ones.text = str[1].toString()
    }

}