package com.smartphotography.facedetector

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

class DetectFace(dialog: AlertDialog,context: Context,img:ImageView) {

    private val dialog:AlertDialog
    private val context:Context
    private val img:ImageView


    init {
        this.dialog=dialog
        this.context=context
        this.img=img
    }

     fun runFaceDetector(bitmap: Bitmap?){
        val image= FirebaseVisionImage.fromBitmap(bitmap!!)
        val options= FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        val realTimePoints= FirebaseVisionFaceDetectorOptions.Builder()
            .setMinFaceSize(0.15f)
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST) // I need more then 1 face
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS) // I need contours of faces
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
            .build()

        val detector= FirebaseVision.getInstance().getVisionFaceDetector(options)
         Glide.with(context).asBitmap().load(bitmap).into(object : CustomTarget<Bitmap>(){
             override fun onLoadCleared(placeholder: Drawable?) {
             }

             override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                 img.setImageBitmap(bitmap)
                 detector.detectInImage(image)
                     .addOnSuccessListener { result->;processFaceResult(result,resource)}
                     .addOnFailureListener{e->Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()}
             }
         })
    }
    private fun processFaceResult(result: List<FirebaseVisionFace>, bitmap: Bitmap?) {
        var count = 0
        for (face in result) {
            val bounds = face.boundingBox
            val pos: FirebaseVisionPoint

          val s= "y="+face.headEulerAngleY.toString()+" z="+face.headEulerAngleZ.toString()
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show()


            val cnv = Canvas(bitmap!!)

            val paint = Paint()
            paint.color = Color.WHITE
            paint.strokeWidth = 4.0f
            paint.style = Paint.Style.STROKE


            val midX=(bounds.left.toFloat()+bounds.right.toFloat())/2
            val midY=(bounds.top.toFloat()+bounds.bottom.toFloat())/2
           // cnv.drawRect(midX-1,midY-1,midX+1,midY+1,paint)


            cnv.rotate(-face.headEulerAngleZ,midX,midY)



            cnv.drawRect(bounds.left.toFloat(),bounds.top.toFloat(),
                bounds.right.toFloat(), bounds.bottom.toFloat(),paint)



            val nose = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)



            nose?.let {
                val b=BitmapFactory.decodeResource(context.getResources(),R.drawable.nosee)
                val m=Matrix()
                pos = nose.position
                val x = pos.x
                val y = pos.y


                  cnv.drawRect(x-1, y-1, x + 1, y + 1, paint)
                val rect=RectF(x-50,y-50,x+50,y+50)


                //  cnv.rotate(face.headEulerAngleZ)

                m.mapRect(rect)
                cnv.drawBitmap(b.scale(100,100),null,rect,null)

            }

            val lips1 = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)
            lips1?.let {
                val x = lips1.position.x
                val y = lips1.position.y
                //cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            val lips2 = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)
            lips2?.let {
                val x = lips2.position.x
                val y = lips2.position.y
               // cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            val lipsB = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)
            lipsB?.let {
                val x = lipsB.position.x
                val y = lipsB.position.y
               // cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            val eyeL = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
            eyeL?.let {
                val x = eyeL.position.x
                val y = eyeL.position.y
               // cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            val eyeR = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
            eyeR?.let {
                val x = eyeR.position.x
                val y = eyeR.position.y
               // cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            val cheekL = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK)
            cheekL?.let {
                val x = cheekL.position.x
                val y = cheekL.position.y
               // cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            val cheekR = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK)
            cheekR?.let {
                val x = cheekR.position.x
                val y = cheekR.position.y
               // cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            val earL = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
            earL?.let {
                val x = earL.position.x
                val y = earL.position.y
               // cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            val earR = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)
            earR?.let {
                val x = earR.position.x
                val y = earR.position.y
               // cnv.drawRect(x, y, x + 1, y + 1, paint)
            }
            count++
        }
        dialog.dismiss()
        Toast.makeText(context,"Detected "+count+" faces", Toast.LENGTH_SHORT).show()
    }

}