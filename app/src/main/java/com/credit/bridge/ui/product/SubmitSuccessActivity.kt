package com.credit.bridge.ui.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityConfirmProductBinding
import com.credit.bridge.databinding.ActivitySubmitSuccessBinding
import com.credit.bridge.remote.HttpClient
import com.credit.bridge.remote.body.RequestFeedbackBody
import com.credit.bridge.remote.event.FeedbackResponseEvent
import com.credit.bridge.remote.event.FetchFeedbackConfigResponseEvent
import com.credit.bridge.remote.event.UpdateTabIndexEvent
import com.credit.bridge.remote.response.JumpConfig
import com.credit.bridge.util.ToastUtil
import com.squareup.otto.Subscribe

class SubmitSuccessActivity : BaseActivity<ActivitySubmitSuccessBinding>(), View.OnClickListener {

    override fun getBinding() = ActivitySubmitSuccessBinding.inflate(layoutInflater)

    private  var starCount = 4
    var jumpConfig: JumpConfig? = null
    private var currentStarRating = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    override fun initRes() {
        super.initRes()

        bindViews.titleLayout.titleTv.text = "Usage details"

        bindViews.titleLayout.titleTv.setOnClickListener(this)
        bindViews.titleLayout.backLl.setOnClickListener(this)
        bindViews.submitTv.setOnClickListener(this)
        bindViews.backToHomeLayout1.setOnClickListener(this)
        bindViews.backToHomeLayout2.setOnClickListener(this)

        bindViews.starView.setRating(3)
        bindViews.starView.onRatingChange = { rating ->
            Log.d("Star", "Current Star Rating: $rating")
            currentStarRating = rating
        }

        fetchFeedbackConfig()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backLl -> {
                finish()
            }
            R.id.submitTv -> {
                submitFeedback()
            }
            R.id.backToHomeLayout1 -> {
                backToHome()
            }
            R.id.backToHomeLayout2 -> {
                backToHome()
            }
        }
    }


    private fun backToHome() {
        eventBus.post(UpdateTabIndexEvent(1))
        finish()
    }

    private fun fetchFeedbackConfig() {
        showLoading()
        HttpClient.fetchFeedbackConfig(this)
    }

    @Subscribe
    fun onFetchFeedbackConfigResponseEvent(event: FetchFeedbackConfigResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            val response = event.model?.mtaw
            jumpConfig = response?.whhdieyt
            if (response?.wigmxieideltelq == true) {
                bindViews.defaultLayout.visibility = View.GONE
                bindViews.starLayout.visibility = View.VISIBLE
            } else {
                bindViews.defaultLayout.visibility = View.VISIBLE
                bindViews.starLayout.visibility = View.GONE
            }
        }
    }

    private fun submitFeedback() {

        val feedback = bindViews.feedbackEt.text.toString()
        if (feedback.isEmpty()) {
            ToastUtil.showLong(this, "Comment content cannot be empty")
            return
        }

        val body = RequestFeedbackBody()
        body.guawnoc = feedback
        body.feedType = "RATING"
        body.qvyjt = currentStarRating

        showLoading()
        HttpClient.submitFeedback(this, body)
    }

    @Subscribe
    fun onFeedbackEvent(event: FeedbackResponseEvent) {
        hideLoading()
        if (event.isSuccess) {
            if (starCount == 5) {
                try {
                    val uri = jumpConfig?.ocuerrncq?.toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("==onResponse==", e.toString())
                }
            } else {
                backToHome()
            }
        }
    }

}