package com.credit.bridge.ui.verify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerLib
import com.credit.bridge.R
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.content.ConstConfig
import com.credit.bridge.databinding.ActivityConfirmProductBinding
import com.credit.bridge.databinding.ActivityTakePhotoBinding
import com.credit.bridge.remote.HttpClient
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import kotlin.use

class TakePhotoActivity : BaseActivity<ActivityTakePhotoBinding>(), View.OnClickListener{

    override fun getBinding() = ActivityTakePhotoBinding.inflate(layoutInflater)

    private var processCameraProvider: ProcessCameraProvider? = null
    private var img_path = ""
    private val cameraExecutor by lazy {
        Executors.newSingleThreadExecutor()
    }
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun initRes() {
        super.initRes()

        bindViews.takePhotoIv.setOnClickListener(this)
        bindViews.flashIv.setOnClickListener(this)
        bindViews.chooseIv.setOnClickListener(this)

        bindViews.takePhotoIv.post {
            lifecycleScope.launch {
                try {
                    val rotation = bindViews.viewCamera.display.rotation
                    val cameraSelector =
                        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                    processCameraProvider = ProcessCameraProvider.getInstance(this@TakePhotoActivity).get()
                    val preview: Preview = Preview.Builder().setTargetRotation(rotation).build()
                    preview.surfaceProvider = bindViews.viewCamera.getSurfaceProvider()
                    imageCapture = ImageCapture.Builder().setTargetRotation(Surface.ROTATION_90)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setFlashMode(ImageCapture.FLASH_MODE_ON).build()
                    try {
                        processCameraProvider?.unbindAll()
                        processCameraProvider?.bindToLifecycle(
                            this@TakePhotoActivity, cameraSelector, preview, imageCapture
                        )
                    } catch (e: Exception) {
                        e.message?.let { Log.e("Use case binding failed", it) }
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        Log.e("take photo", e.toString())
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.takePhotoIv -> {
                val tempFile = File.createTempFile("oss_upload_",
                    ".jpg",
                    cacheDir
                )
                imageCapture?.takePicture(
                    cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            super.onCaptureSuccess(image)
                            image.use { _ ->
                                val buffer = image.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }
                                FileOutputStream(tempFile).use { it.write(bytes) }
                                img_path = tempFile.absolutePath
                                setResult(RESULT_OK, Intent().putExtra("path_img", img_path))
                                finish()
                            }
                        }
                    })

                HttpClient.eventReport(this,ConstConfig.POINT_CLICK_CAMERA,
                    ConstConfig.POINT_ACTION_TYPE_CLICK,ConstConfig.POINT_CLICK_CAMERA)
            }
            R.id.flashIv -> {
                if(imageCapture?.flashMode == ImageCapture.FLASH_MODE_ON){
                    imageCapture?.setFlashMode(ImageCapture.FLASH_MODE_OFF)
                    bindViews.flashIv.setImageResource(R.mipmap.ic_flash_off)
                }else if(imageCapture?.flashMode == ImageCapture.FLASH_MODE_OFF){
                    imageCapture?.setFlashMode(ImageCapture.FLASH_MODE_ON)
                    bindViews.flashIv.setImageResource(R.mipmap.ic_flash_on)
                }
            }
            R.id.chooseIv -> {
                setResult(RESULT_OK, Intent().putExtra("path_img", img_path))
                finish()
            }
        }
    }
}