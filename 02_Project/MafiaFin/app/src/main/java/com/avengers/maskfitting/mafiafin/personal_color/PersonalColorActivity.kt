package com.avengers.maskfitting.mafiafin.personal_color


import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.databinding.ActivityPersonalCameraBinding
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_personal_camera.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


// 퍼스널 컬러 진단 액티비티
// 이전 액티비티(얼굴형 진단)에서 intent 로 넘어온 값 확인 동작 구현 필요
class PersonalColorActivity : AppCompatActivity() {

    var image: InputImage? = null
    var personalColorResult : String = ""

    private var img: ImageView? = null
    private var btn_capture: Button? = null
    private var btn_gallery: Button? = null
    private var btn_send: Button? = null
    private var progress: ProgressDialog? = null
    private var queue: RequestQueue? = null
    private var currentPhotoPath: String? = null
    private var bitmap: Bitmap? = null
    private var imageString: String? = null
    private lateinit var binding: ActivityPersonalCameraBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
        btn_complete.visibility = View.GONE
        btn_capture!!.setOnClickListener { cameraOpenIntent() }
        btn_gallery!!.setOnClickListener { gallery_open_intent() }

        // 검사하기 버튼
        btn_send!!.setOnClickListener {
            progress = ProgressDialog(this@PersonalColorActivity)
            progress!!.setMessage("Uploading...")
            progress!!.show()
            sendImage()
            btn_complete.visibility = View.VISIBLE
        }

        val faceShapeResult = intent.getStringExtra("FaceShape").toString()
        // 검사완료 버튼
        btn_complete.setOnClickListener {
            var intent = Intent(this, PersonalColorOutputActivity::class.java)
            Log.d("PersonalColorActivity", personalColorResult)
            intent.putExtra("PersonalColor", personalColorResult)
            intent.putExtra("FaceShape", faceShapeResult)
            startActivity(intent)
        }
    }


    //이미지 플라시크로 전송
    private fun sendImage() {

        //비트맵 이미지를 byte로 변환 -> base64형태로 변환
        val baos = ByteArrayOutputStream()




        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)


        //base64형태로 변환된 이미지 데이터를 플라스크 서버로 전송
        val flask_url = "http://192.168.219.159:5000/sendFrame"

        val request: StringRequest = object : StringRequest(
            Method.POST,
            flask_url,
            Response.Listener { response ->
                progress!!.dismiss()
                personalColorResult = response
                when (response) {
                    "1" -> {
                        Toast.makeText(this@PersonalColorActivity, "봄 웜톤", Toast.LENGTH_LONG)
                            .show()
                    }
                    "2" -> {
                        Toast.makeText(this@PersonalColorActivity, "가을 웜톤", Toast.LENGTH_LONG)
                            .show()
                    }
                    "3" -> {
                        Toast.makeText(this@PersonalColorActivity, "여름 쿨톤", Toast.LENGTH_LONG)
                            .show()
                    }
                    "4" -> {
                        Toast.makeText(this@PersonalColorActivity, "겨울 쿨톤", Toast.LENGTH_LONG)
                            .show()
                    }
                    else -> {
                        Toast.makeText(this@PersonalColorActivity, "Some error occurred!", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            },
            Response.ErrorListener { error ->
                progress!!.dismiss()
                Toast.makeText(
                    this@PersonalColorActivity,
                    "Some error occurred -> $error",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("에러!","$error")
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["image"] = imageString!!
                return params
            }
        }

        // Volley.timeoutError 발생 시 재요청 전송 코드(Retry)
        request.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(request)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            var bitmap: Bitmap = MediaStore.Images.Media
                .getBitmap(getContentResolver(), Uri.fromFile(File(currentPhotoPath)))
            val ei = android.media.ExifInterface(currentPhotoPath!!)
            val orientation: Int = ei.getAttributeInt(
                android.media.ExifInterface.TAG_ORIENTATION,
                android.media.ExifInterface.ORIENTATION_UNDEFINED
            )
            //var rotatedBitmap: Bitmap? = null
            when (orientation) {
                android.media.ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotateImage(bitmap, 90f)
                android.media.ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotateImage(bitmap, 180f)
                android.media.ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotateImage(bitmap, 270f)
                android.media.ExifInterface.ORIENTATION_NORMAL -> bitmap = bitmap
                else -> bitmap = bitmap
            }

            //회전 된 비트맵을 파일로 변환하여 저장
            // 파일 선언 -> 경로는 파라미터에서 받는다
            val file = File(currentPhotoPath)

            // OutputStream 선언 -> bitmap데이터를 OutputStream에 받아 File에 넣어주는 용도
            var out: OutputStream? = null
            try {
                // 파일 초기화
                file.createNewFile()

                // OutputStream에 출력될 Stream에 파일을 넣어준다
                out = FileOutputStream(file)

                // bitmap 압축
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                try {
                    out!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            val picturePhotoURI = Uri.fromFile(file)
            getBitmap(picturePhotoURI)

            img!!.setImageBitmap(bitmap)

        } else if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK) {
            val galleryURI = data!!.data
            //img.setImageURI(galleryURI);
            getBitmap(galleryURI)
            img!!.setImageBitmap(bitmap)
        }
    }

    //Uri에서 bitmap
    private fun getBitmap(picturePhotoURI: Uri?) {
        try {
            //서버로 이미지를 전송하기 위한 비트맵 변환하기
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, picturePhotoURI)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //xml에 정의한 view 초기화
    private fun init() {
        img = binding.imagePreview
        btn_capture = binding.btnCamera
        btn_gallery = binding.btnGallary
        btn_send = binding.btnDetection
        queue = Volley.newRequestQueue(this@PersonalColorActivity)
        requestPermission()
    }

    //카메라, 쓰기, 읽기 권한 체크/요청
    private fun requestPermission() {
        //민감한 권한 사용자에게 허용요청
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) { // 저장소에 데이터를 쓰는 권한을 부여받지 않았다면~
            ActivityCompat.requestPermissions(
                this@PersonalColorActivity, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0
            )
        }
    }

    //갤러리 띄우기
    private fun gallery_open_intent() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GET_GALLERY_IMAGE)
    }

//    //갤러리 사진 저장 기능
//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun saveFile(currentPhotoPath: String?) {
//        var bitmap = BitmapFactory.decodeFile(currentPhotoPath)
//
//
//        val ei = android.media.ExifInterface(currentPhotoPath!!)
//        val orientation: Int = ei.getAttributeInt(
//            android.media.ExifInterface.TAG_ORIENTATION,
//            android.media.ExifInterface.ORIENTATION_UNDEFINED
//        )
//        //var rotatedBitmap: Bitmap? = null
//        when (orientation) {
//            android.media.ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotateImage(bitmap, 90f)
//            android.media.ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotateImage(bitmap, 180f)
//            android.media.ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotateImage(bitmap, 270f)
//            android.media.ExifInterface.ORIENTATION_NORMAL -> bitmap = bitmap
//            else -> bitmap = bitmap
//        }
//
//
//        val values = ContentValues()
//
//        //실제 앨범에 저장될 이미지이름
//        values.put(
//            MediaStore.Images.Media.DISPLAY_NAME,
//            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(
//                Date()
//            ) + ".jpg"
//        )
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
//
//        //저장될 경로 -> /내장 메모리/DCIM/ 에 'AndroidQ' 폴더로 지정
//        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/AndroidQ")
//        val u = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        val uri = contentResolver.insert(u, values) //이미지 Uri를 MediaStore.Images에 저장
//        try {
//            /*
//             ParcelFileDescriptor: 공유 파일 요청 객체
//             ContentResolver: 어플리케이션끼리 특정한 데이터를 주고 받을 수 있게 해주는 기술(공용 데이터베이스)
//                            ex) 주소록이나 음악 앨범이나 플레이리스트 같은 것에도 접근하는 것이 가능
//
//            getContentResolver(): ContentResolver객체 반환
//            */
//            var parcelFileDescriptor: ParcelFileDescriptor? = null
//            parcelFileDescriptor =
//                contentResolver.openFileDescriptor(uri!!, "w", null) //미디어 파일 열기
//            if (parcelFileDescriptor == null) return
//
//            //바이트기반스트림을 이용하여 JPEG파일을 바이트단위로 쪼갠 후 저장
//            val byteArrayOutputStream = ByteArrayOutputStream()
//
//
//            //비트맵 형태 이미지 크기 압축
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//            val b = byteArrayOutputStream.toByteArray()
//            val inputStream: InputStream = ByteArrayInputStream(b)
//            val buffer = ByteArrayOutputStream()
//            val bufferSize = 1024
//            val buffers = ByteArray(bufferSize)
//            var len = 0
//            while (inputStream.read(buffers).also { len = it } != -1) {
//                buffer.write(buffers, 0, len)
//            }
//            val bs = buffer.toByteArray()
//            val fileOutputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
//            fileOutputStream.write(bs)
//            fileOutputStream.close()
//            inputStream.close()
//            parcelFileDescriptor.close()
//            contentResolver.update(
//                uri,
//                values,
//                null,
//                null
//            ) //MediaStore.Images 테이블에 이미지 행 추가 후 업데이트
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        values.clear()
//        values.put(
//            MediaStore.Images.Media.IS_PENDING,
//            0
//        ) //실행하는 기기에서 앱이 IS_PENDING 값을 1로 설정하면 독점 액세스 권한 획득
//    }

    //카메라 호출
    private fun cameraOpenIntent() {
        Log.d("Camera", "카메라실행!")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()

            } catch (ex: IOException) {
                Log.d(TAG, "에러발생!!")
            }
            if (photoFile != null) {
                val providerURI =
                    FileProvider.getUriForFile(this, "com.avengers.maskfitting.camera", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    //카메라 회전
    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    //카메라 촬영 시 임시로 사진을 저장하고 사진위치에 대한 Uri 정보를 가져오는 메소드
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        Log.d(TAG, "사진저장>> " + storageDir.toString())
        currentPhotoPath = image.absolutePath
        return image
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val GET_GALLERY_IMAGE = 2
        const val TAG = "카메라"
    }
}



