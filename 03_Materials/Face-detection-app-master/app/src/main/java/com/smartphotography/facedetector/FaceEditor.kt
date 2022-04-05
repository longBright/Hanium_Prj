package com.smartphotography.facedetector

import android.content.Context
import android.graphics.*
import androidx.core.graphics.scale
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

private const val TAG = "FaceEditor"

class FaceEditor {
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private val facePoints = listOf(
        FirebaseVisionFaceLandmark.NOSE_BASE,
        FirebaseVisionFaceLandmark.MOUTH_LEFT,
        FirebaseVisionFaceLandmark.MOUTH_RIGHT,
        FirebaseVisionFaceLandmark.MOUTH_BOTTOM,
        FirebaseVisionFaceLandmark.LEFT_EYE,
        FirebaseVisionFaceLandmark.RIGHT_EYE,
        FirebaseVisionFaceLandmark.LEFT_CHEEK,
        FirebaseVisionFaceLandmark.RIGHT_CHEEK,
        FirebaseVisionFaceLandmark.LEFT_EAR,
        FirebaseVisionFaceLandmark.RIGHT_EAR
    )

    init {
        paint = Paint()
        paint.color = Color.YELLOW
        paint.strokeWidth = 8.0f
        paint.style = Paint.Style.STROKE
    }

    fun drawRects(result: List<FirebaseVisionFace>, bitmap: Bitmap) {
        canvas = Canvas(bitmap)
        for (face in result) {
            val bounds = face.boundingBox
            canvas.drawRect(bounds, paint)
            for (facePoint in facePoints) {
                val landmark = face.getLandmark(facePoint)
                landmark?.let {
                    val x = landmark.position.x
                    val y = landmark.position.y
                    canvas.drawRect(x - 6, y - 6, x + 6, y + 6, paint)
                }
            }
        }

    }

    fun attachSticker(result: List<FirebaseVisionFace>, bitmap: Bitmap, context:Context){
        val b=BitmapFactory.decodeResource(context.getResources(),R.drawable.nosee_2)
        for (face in result) {
            canvas = Canvas(bitmap)
            val bounds = face.boundingBox
            val midX=(bounds.left.toFloat()+bounds.right.toFloat())/2
            val midY=(bounds.top.toFloat()+bounds.bottom.toFloat())/2
            canvas.rotate(-face.headEulerAngleZ,midX,midY)
            val nose = face.getLandmark(facePoints[0])
            nose?.let {
                val x = nose.position.x
                val y = nose.position.y
                val rect=RectF(x-100,y-100,x+100,y+100)
                canvas.drawBitmap(b.scale(100,100),null,rect,null)
            }
        }

    }
}