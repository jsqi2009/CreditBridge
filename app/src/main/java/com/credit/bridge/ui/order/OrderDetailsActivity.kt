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
        orderId = orderInfo?.qyfqljd ?: 0
        orderStatus = orderInfo?.ufzqlyyxash
        if (intent.hasExtra("isExtend")) {
            isExtend = intent.getBooleanExtra("isExtend", false)
        }

        bindViews.titleLayout.titleTv.text = "Details"

        bindViews.titleLayout.backIv.setOnClickListener(this)
        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.editBankTv.setOnClickListener(this)

        getOrderDetailsInfo()

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
        requestBody.bsmweqe = orderInfo?.qyfqljd.toString()

        showLoading()
        HttpClient.getOrderDetails(this, requestBody, ConstConfig.ORDER_DETAIL_COMMON)
    }

    @Subscribe
    fun onOrderDetailsResponseEvent(event: OrderDetailsResponseEvent) {
        hideLoading()
        if (event.model == null) return
        if (event.model?.flag == ConstConfig.ORDER_DETAIL_COMMON) {
            if (event.isSuccess) {
                orderInfo = event.model?.blvb
                initOrderDetailsInfo()
                if (orderStatus == ConstConfig.ORDER_STATUS_REJECTED) {
                    val leftTime = AppUtil.getTotalSeconds(orderInfo?.eeymvvicicofnhn ?: "")
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
                val link = event.model?.blvb ?: return
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

    }

    private fun getOrderUpdateInfo() {
        showLoading()
        HttpClient.getOrderUpdateInfo(this, orderInfo?.vuinurdxypfw ?: 0 ,orderId)
    }

    @SuppressLint("SuspiciousIndentation")
    @Subscribe
    fun onOrderUpdateResponseEvent(event: OrderUpdateResponseEvent) {
        hideLoading()
        if (event.model == null) return
        if (event.isSuccess) {
            event.model?.blvb?.let {
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