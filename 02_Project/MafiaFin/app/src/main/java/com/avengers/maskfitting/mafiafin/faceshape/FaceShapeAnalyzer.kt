package com.avengers.maskfitting.mafiafin.faceshape

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

// ImageAnalyzer 상속 추상 클래스
// ImageAnalysis : 이미지 처리, 컴퓨터 비전, 머신러닝 추론 진행이 가능하도록 CPU 에서 액세스 가능한 이미지를 앱에 제공하는 Interface
// 본 클래스는 FaceContourDetectionProcessor.kt 파일에서 import 하여 사용됨.
// 본 프로젝트에서의 사용은 객체인식에 사용된다고 생각하면됨.
abstract class FaceShapeAnalyzer<T> : ImageAnalysis.Analyzer {

    abstract val graphicOverlay: GraphicOverlay     // GraphicOverlay.kt 파일 참조

    // analyze 함수 : 이미지를 분석하여 결과를 생성함
    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            // it : let 함수에서 사용되는 키워드로, it 을 사용하면 해당 객체를 호출함. 여기서는 mediaImage.
            // detectInImage 함수 : 하단에 정의된 함수로, Listener 를 연결하여 동작을 정의함.
            detectInImage(InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees))
                // 감지 성공 시
                .addOnSuccessListener { results ->
                    // 하단에 정의된 onSuccess 함수 호출
                    onSuccess(
                        results,
                        graphicOverlay,
                        it.cropRect,     // 전달 받은 사각형 부분을 검출해낸다
                        it
                    )
                }
                // 감지 실패 시
                .addOnFailureListener {
                    onFailure(it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    abstract fun stop()

    protected abstract fun detectInImage(image: InputImage): Task<T>

    // 성공 시 호출되는 함수
    protected abstract fun onSuccess(
        results: T,
        graphicOverlay: GraphicOverlay,
        rect: Rect,
        image: Image
    )

    protected abstract fun onFailure(e: Exception)

}