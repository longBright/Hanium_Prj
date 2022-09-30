package com.avengers.maskfitting.mafiafin.faceshape

import android.content.Context
import android.view.View
import android.graphics.*
import android.media.Image
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


// 실시간 얼굴 인식 후 특징값 추출 후 동작 처리 클래스
class FaceContourDetectionProcessor (
    private val context: Context,
    private val view: GraphicOverlay
) : FaceShapeAnalyzer<List<Face>>() {

    private val realTimeOpts = FaceDetectorOptions.Builder()    // 실시간 얼굴 인식
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    override val graphicOverlay: GraphicOverlay
        get() = view

    private lateinit var faceShapeActivity: FaceShapeActivity

    //모델에 맞는 input, output 선언
    private var output = Array(1) { FloatArray(4) }
    private var input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }

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
        graphicOverlay: GraphicOverlay,
        rect: Rect,
        image: Image,
    ) {
        graphicOverlay.clear()      // Graphic 초기화

        var lite: Interpreter? = getTfliteInterpreter("converted_model.tflite");        // tflite 모델 호출

        faces.forEach { it ->
            // Image 를 Bitmap 으로 변환
            val bitmap: Bitmap = imageToBitmap(image)
            // Bitmap을 224x224로 reize
            val resizedBitmap: Bitmap = resizeBitmapImage(bitmap, 224)

            // 얼굴부분 사각형 이미지 출력
            val faceGraphic = FaceContourGraphic(graphicOverlay, it, rect)
            graphicOverlay.add(faceGraphic)

            input = inputPreprocess(input, bitmap)  // 입력 전처리
//            input = inputPreprocess(input, resizedBitmap)  // 입력 전처리
            lite?.run(input, output)    //얼굴형 분류 시작

            // 분류 결과 처리
            var faceShapeString = getMaxValue(output[0])

            // 얼굴형분류 액티비티의 결과 값 접근
            faceShapeActivity = context as FaceShapeActivity
            faceShapeActivity.faceShapeResult = faceShapeString
        }
        graphicOverlay.postInvalidate()     // ???(예외 처리 부분)
    }
    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
    }

    private fun getTfliteInterpreter(modelPath: String): Interpreter? { //tflite 모델 연결 코드
        try {
            return Interpreter(loadModelFile(modelPath))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Image를 Bitmap으로 변환
    private fun imageToBitmap(image: Image): Bitmap {

        val yBuffer = image.planes[0].buffer // Y
        val vuBuffer = image.planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val bytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
    }

    // 모델 입력 전처리
    private fun inputPreprocess(input: Array<Array<Array<FloatArray>>>, bitmap: Bitmap): Array<Array<Array<FloatArray>>>{
        // 함수화하면 좋을듯
        val batchNum = 0
        // input 전처리 과정
        for (x in 0..223) {
            for (y in 0..223) {
                val pixel: Int = bitmap.getPixel(x, y)
                input.get(batchNum)[x][y][0] = Color.red(pixel) / 1.0f
                input.get(batchNum)[x][y][1] = Color.green(pixel) / 1.0f
                input.get(batchNum)[x][y][2] = Color.blue(pixel) / 1.0f
            }
        }
        return input
    }

    // 딥러닝 모델 결과 처리
    // 최댓값의 인덱스를 구하는 함수
    private fun getMaxValue(prediction: FloatArray) : String {
        var max = 0.0f
        var faceShapeString = ""
        var testString = ""
        for (i: Int in 0..3) {
            testString += "${prediction[i]}, "
            if (prediction[i] > max) {
                max = prediction[i]
                faceShapeString = i.toString()
            }
        }
        Log.d("결과확인", testString)
        return faceShapeString
    }

    /* 오류 발생함 확인 바람 */
    // bitmap resize
    private fun resizeBitmapImage(source: Bitmap, maxResolution: Int): Bitmap {
        val width = source.width
        val height = source.height
        var newWidth = width
        var newHeight = height
        var rate = 0.0f
        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / width.toFloat()
                newHeight = (height * rate).toInt()
                newWidth = maxResolution
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / height.toFloat()
                newWidth = (width * rate).toInt()
                newHeight = maxResolution
            }
        }
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }

    @Throws(IOException::class)
    fun loadModelFile(modelPath: String?): ByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath!!)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

}