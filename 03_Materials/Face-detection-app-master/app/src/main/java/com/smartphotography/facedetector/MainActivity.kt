package com.smartphotography.facedetector

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG="MainActivity"
private const val GALLERY_IMAGE_PICK_CODE=1
private const val CAMERA_IMAGE_PICK_CODE=2

class MainActivity : AppCompatActivity(), Face.onFaceDetectionCompleteListener {

    private lateinit var waitingDialog: AlertDialog
    private lateinit var cameraImagePath: String
    private lateinit var selectedImageUri: Uri
    private lateinit var face: Face
    private lateinit var faceEditor:FaceEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        waitingDialog =
            SpotsDialog.Builder().setContext(this).setMessage("Please wait...").setCancelable(false)
                .build()
        face = Face(this)
        faceEditor = FaceEditor()

        val hasReadContactsPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), CAMERA_IMAGE_PICK_CODE
            )
        }

        gallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startGallery()
            } else {
                showSnackBar(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "Please grant the permissions."
                )
            }
        }

        camera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startCamera()
            } else {
                showSnackBar(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "Please grant the permissions."
                )
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val imageURI = data!!.data
            selectedImageUri = imageURI!!
        }
        if (requestCode == CAMERA_IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = Uri.fromFile(File(cameraImagePath))
        }
        face.runFaceDetector(selectedImageUri, this)
        waitingDialog.show()
        Glide.with(this).asBitmap().load(selectedImageUri).into(img)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_IMAGE_PICK_CODE)
    }

    private fun startCamera() {
        val takepic = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takepic.resolveActivity(packageManager) != null) {
            val photoFile: File?
            photoFile = createPhotoFile()
            if (photoFile != null) {
                cameraImagePath = photoFile.absolutePath
                val photoUri = FileProvider.getUriForFile(
                    applicationContext,
                    "com.smartphotography.facedetector.fileprovider", photoFile
                )
                takepic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takepic, CAMERA_IMAGE_PICK_CODE)
            }
        }
    }

    private fun createPhotoFile(): File? {
        val name: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image: File? = null
        try {
            image = File.createTempFile(name, ".jpg", storageDir)
        } catch (e: IOException) {
        }
        return image
    }

    private fun showSnackBar(view: View, perm_type: String, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Enable access", {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm_type)) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), CAMERA_IMAGE_PICK_CODE
                    )
                } else {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    this.startActivity(intent)
                }
            }).show()
    }

    override fun onFaceDetectionComplete() {
        waitingDialog.dismiss()
        Log.d(TAG, "onFacesDetected(): Face detection completed.")
    }

    override fun processFaceResults(result: List<FirebaseVisionFace>) {
        //Count total  number of faces in an image
        val faceCounts = result.size
        Log.d(TAG, "processFaceResults(): Detected faces = ${faceCounts}")

        //Drwar rectangle over the face to identify
        Glide.with(this).asBitmap().load(selectedImageUri).into(object:CustomTarget<Bitmap>(){
            override fun onLoadCleared(placeholder: Drawable?) {
                TODO("Not yet implemented")
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                img.setImageBitmap(resource)
                faceEditor.drawRects(result, resource)
                faceEditor.attachSticker(result,resource,this@MainActivity)
            }
        })
    }
}