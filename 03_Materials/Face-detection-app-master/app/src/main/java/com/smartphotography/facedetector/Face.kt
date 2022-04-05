package com.smartphotography.facedetector

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

private const val TAG = "Face"

class Face(private val listener: onFaceDetectionCompleteListener) {

    interface onFaceDetectionCompleteListener {
        fun onFaceDetectionComplete()
        fun processFaceResults(result: List<FirebaseVisionFace>)
    }

    fun runFaceDetector(imagePath: Uri?, context: Context) {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        detector.detectInImage(FirebaseVisionImage.fromFilePath(context, imagePath!!))
            .addOnSuccessListener { result ->
                listener.onFaceDetectionComplete()
                listener.processFaceResults(result)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}