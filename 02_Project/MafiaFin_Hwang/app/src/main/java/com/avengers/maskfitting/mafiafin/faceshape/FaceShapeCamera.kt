package com.avengers.maskfitting.mafiafin.faceshape

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.ScaleGestureDetector
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.avengers.maskfitting.mafiafin.faceshape.GraphicOverlay //+

class FaceShapeCamera(    //카메라 관리 클래스
    private val context: Context,
    private val finderView: PreviewView,
    private val lifecycleOwner: LifecycleOwner,
    private val graphicOverlay: GraphicOverlay     //+
) {

    private var preview: Preview? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null


    lateinit var cameraExecutor: ExecutorService
    lateinit var imageCapture: ImageCapture
    lateinit var metrics: DisplayMetrics

    var rotation: Float = 0f
    var cameraSelectorOption = CameraSelector.LENS_FACING_FRONT      // default : 전면 카메라

    init {
        createNewExecutor()
    }

    // 새로운 실행자 생성
    private fun createNewExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    // Image Analyzer 의 타입을 설정함. 우리 프로젝트에서 사용되는 부분은 Face 부분. +
    //private fun selectAnalyzer(): ImageAnalysis.Analyzer {
    //  return FaceContourDetectionProcessor(graphicOverlay)
    //}

    // 카메라 설정
    private fun setCameraConfig(
        cameraProvider: ProcessCameraProvider?,
        cameraSelector: CameraSelector
    ) {
        try {
            cameraProvider?.unbindAll()     // 바인딩 해제
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalyzer
            )
            preview?.setSurfaceProvider(
                finderView.createSurfaceProvider()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }

    // 화면 Zoom(확대 및 축소) 기능
    private fun setUpPinchToZoom() {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1F
                val delta = detector.scaleFactor
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(context, listener)
        finderView.setOnTouchListener { _, event ->
            finderView.post {
                scaleGestureDetector.onTouchEvent(event)
            }
            return@setOnTouchListener true
        }
    }

    // 카메라 시작
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            Runnable {
                cameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()     // 카메라 프리뷰

                imageAnalyzer = ImageAnalysis.Builder() //  원하는 analyzer 넣기
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, FaceContourDetectionProcessor(context, graphicOverlay)) // 얼굴 인식 Analyzer
                    }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraSelectorOption)
                    .build()

                metrics =  DisplayMetrics().also { finderView.display.getRealMetrics(it) }

                imageCapture =
                    ImageCapture.Builder()
                        .setTargetResolution(Size(metrics.widthPixels, metrics.heightPixels))
                        .build()

                setUpPinchToZoom()
                setCameraConfig(cameraProvider, cameraSelector)

            }, ContextCompat.getMainExecutor(context)
        )
    }

    // 전/후면 카메라 전환 -> 카메라 쓰는 액티비티에서 호출해서 쓰면됨.
    fun changeCameraSelector() {
        cameraProvider?.unbindAll()
        cameraSelectorOption =
            if (cameraSelectorOption == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
            else CameraSelector.LENS_FACING_BACK
        startCamera()
    }

    // Image Analyzer 교체
//    fun changeAnalyzer(visionType: VisionType) {
//        if (analyzerVisionType != visionType) {
//            cameraProvider?.unbindAll()
//            analyzerVisionType = visionType
//            startCamera()       // 카메라 재시작
//        }
//    }

    // 가로모드
    fun isHorizontalMode() : Boolean {
        return rotation == 90f || rotation == 270f
    }

    // 전면 카메라 사용
    fun isFrontMode() : Boolean {
        return cameraSelectorOption == CameraSelector.LENS_FACING_FRONT
    }

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val TAG = "CameraXBasic"
    }

}