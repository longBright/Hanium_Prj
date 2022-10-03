package com.avengers.maskfitting.mafiafin.main.fitting

import android.graphics.Rect
import android.media.Image
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

import java.io.IOException

// 실시간 얼굴 인식 후 특징값 추출 후 동작 처리 클래스
class FaceContourDetectionProcessor() : FittingAnalyzer<List<Face>>() {

    private val realTimeOpts = FaceDetectorOptions.Builder()    // 실시간 얼굴 인식
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    override fun detectInImage(image: InputImage): Task<List<Face>> {   // 이미지 입력
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(
        faces: List<Face>,
        rect: Rect,
        image: Image
    ) {
        faces.forEach {
            // 얼굴 보냄.
            // val results = K-means()
            // Log.d(TAG, "${results.size}")
        }
        Log.d(TAG, image.cropRect.javaClass.name)
        Log.d(TAG, "Face Detector success.")
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
    }

}