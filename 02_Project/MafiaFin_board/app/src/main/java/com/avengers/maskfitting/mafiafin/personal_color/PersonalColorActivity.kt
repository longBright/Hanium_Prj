package com.avengers.maskfitting.mafiafin.personal_color

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle

import com.android.volley.toolbox.StringRequest
import android.widget.Toast
import kotlin.Throws
import android.content.Intent

//import com.example.camerasendapp.MainActivity

import android.provider.MediaStore
import com.android.volley.toolbox.Volley
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

import com.android.volley.*
import com.avengers.maskfitting.mafiafin.databinding.ActivityPersonalCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.android.synthetic.main.activity_personal_camera.*
import java.io.InputStream



// 퍼스널 컬러 진단 액티비티
// 이전 액티비티(얼굴형 진단)에서 intent 로 넘어온 값 확인 동작 구현 필요
class PersonalColorActivity : AppCompatActivity() {

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
        //String flask_url = "http://172.30.1.6:5000/sendFrame";
//        val flask_url = "http://ec2-43-200-115-71.ap-northeast-2.compute.amazonaws.com:5000/sendFrame"
        val flask_url = "http://220.117.238.117:5000/sendFrame"

        //val flask_url = "http://3.35.13.226:5000/sendFrame"

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
//                if (response == "test") {
//                    Toast.makeText(this@PersonalColorActivity, "Uploaded Successful", Toast.LENGTH_LONG)
//                        .show()
//                } else {
//                    Toast.makeText(this@PersonalColorActivity, "Some error occurred!", Toast.LENGTH_LONG)
//                        .show()
//                }
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
            val picturePhotoURI = Uri.fromFile(File(currentPhotoPath))
            getBitmap(picturePhotoURI)
            img!!.setImageBitmap(bitmap)

            //갤러리에 사진 저장
            saveFile(currentPhotoPath)
        } else if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK) {
            val galleryURI = data!!.data
            //img.setImageURI(galleryURI);
            getBitmap(galleryURI)
            img!!.setImageBitmap(bitmap)
        }
    }

    //Uri에서 bisap
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

    //갤러리 사진 저장 기능
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFile(currentPhotoPath: String?) {
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
        val values = ContentValues()

        //실제 앨범에 저장될 이미지이름
        values.put(
            MediaStore.Images.Media.DISPLAY_NAME,
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(
                Date()
            ) + ".jpg"
        )
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")

        //저장될 경로 -> /내장 메모리/DCIM/ 에 'AndroidQ' 폴더로 지정
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/AndroidQ")
        val u = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val uri = contentResolver.insert(u, values) //이미지 Uri를 MediaStore.Images에 저장
        try {
            /*
             ParcelFileDescriptor: 공유 파일 요청 객체
             ContentResolver: 어플리케이션끼리 특정한 데이터를 주고 받을 수 있게 해주는 기술(공용 데이터베이스)
                            ex) 주소록이나 음악 앨범이나 플레이리스트 같은 것에도 접근하는 것이 가능

            getContentResolver(): ContentResolver객체 반환
            */
            var parcelFileDescriptor: ParcelFileDescriptor? = null
            parcelFileDescriptor =
                contentResolver.openFileDescriptor(uri!!, "w", null) //미디어 파일 열기
            if (parcelFileDescriptor == null) return

            //바이트기반스트림을 이용하여 JPEG파일을 바이트단위로 쪼갠 후 저장
            val byteArrayOutputStream = ByteArrayOutputStream()

            //비트맵 형태 이미지 크기 압축
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val b = byteArrayOutputStream.toByteArray()
            val inputStream: InputStream = ByteArrayInputStream(b)
            val buffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            var len = 0
            while (inputStream.read(buffers).also { len = it } != -1) {
                buffer.write(buffers, 0, len)
            }
            val bs = buffer.toByteArray()
            val fileOutputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
            fileOutputStream.write(bs)
            fileOutputStream.close()
            inputStream.close()
            parcelFileDescriptor.close()
            contentResolver.update(
                uri,
                values,
                null,
                null
            ) //MediaStore.Images 테이블에 이미지 행 추가 후 업데이트
        } catch (e: Exception) {
            e.printStackTrace()
        }
        values.clear()
        values.put(
            MediaStore.Images.Media.IS_PENDING,
            0
        ) //실행하는 기기에서 앱이 IS_PENDING 값을 1로 설정하면 독점 액세스 권한 획득
        contentResolver.update(uri!!, values, null, null)
    }

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

var customProgressDialog: ProgressDialog? = null

//권한처리
val PERM_STROAGE = 9
val PERM_CAMERA = 10

//카메라요청
val REQ_CAMERA = 11
//갤러리요청
val REQ_GALLARY = 12

var image: InputImage? = null

//    private lateinit var binding: ActivityPersonalCameraBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPersonalCameraBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//
//        //로딩창 객체 생성
//        customProgressDialog = ProgressDialog(this)
//        //로딩창을 투명하게
//        customProgressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//
//
//        //1. 공용저장소 권한이 있는지 확인
//        requirePermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERM_STROAGE)
//
//        // High-accuracy landmark detection and face classification
//        // detector에 대한 옵션 설정
//        // High-accuracy landmark detection and face classification
//        // detector에 대한 옵션 설정
//        val highAccuracyOpts = FaceDetectorOptions.Builder()
//            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
//            .build()
//
//        // detector생성
//        val detector: FaceDetector = FaceDetection.getClient(highAccuracyOpts)
//
//        binding.btnDetection.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//
//
//                customProgressDialog!!.show()
//
//
//
//                // 생성한 Input Image를 처리한다.
//                //val result: Task<List<Face>> = detector.process(image!!)
//                val result = detector.process(image!!)
//                    .addOnSuccessListener{ faces -> // 탐지가 성공할 경우
//                        for (face in faces) {
//                            val bounds = face.boundingBox
//                            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
//                            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
//
//                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
//                            // nose available):
//                            val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
//                            leftEye?.let {
//                                val leftEyePos = leftEye.position
//                                face_info.setText("왼쪽눈 : $leftEyePos")
//                                Log.d("얼굴", "가로 : ${bounds.width()}, 세로 : ${bounds.height()}")
//                                Log.d("왼쪽눈", "왼쪽눈 : $leftEyePos")
//                            }
//                            val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
//                            rightEye?.let {
//                                val rightEyePos = rightEye.position
//                                face_info.setText("오른쪽눈 : $rightEyePos")
//                                Log.d("오른쪽눈", "오른쪽눈 : $rightEyePos")
//                            }
//                            val leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)
//                            leftCheek?.let {
//                                val leftCheekPos = leftCheek.position
//                            }
//                            val rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK)
//                            rightCheek?.let {
//                                val rightCheekPos = rightCheek.position
//                            }
//                            val mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)
//                            mouthBottom?.let {
//                                val mouthBottomPos = mouthBottom.position
//                            }
//                            // If contour detection was enabled:
//                            val leftEyebrowBottom = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM)?.points
//                            Log.d("왼쪽눈썹아래", "$leftEyebrowBottom")
//                            val leftEyebrowTop = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
//                            val rightEyebrowBottom = face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM)?.points
//                            val rightEyebrowTop = face.getContour(FaceContour.RIGHT_EYEBROW_TOP)?.points
//                            //face_info.setText("왼쪽눈썹(아래) : $leftEyebrowBottom 왼쪽눈썹(위) : $leftEyebrowTop 오른쪽눈썹(아래) : $rightEyebrowBottom 오른쪽눈썹(위) : $rightEyebrowTop")
//                            //face_info.setText("왼쪽눈 :  $leftEyePos 오른쪽눈 :  $rightEyePos 왼쪽볼 : 오른쪽볼 : 왼쪽눈썹(위) : 왼쪽눈썹(아래) : 오른쪽눈썹(위) : 오른쪽눈썹(아래) : ")
//                        }
//
//                        // 처리한 이미지에 대한 결과(얼굴에 대한 정보)를 텍스트뷰에 띄워준다.
//                            //face_info.setText("왼쪽눈 :  $leftEye")
//
//                        //로딩창 종료료
//                       customProgressDialog?.dismiss()
////                        val intent = Intent(applicationContext, ResultActivity::class.java)
////                        startActivity(intent)
//                        }
//                    .addOnFailureListener{ // 탐지가 실패할 경우
//                            Log.e("faces: ", "실패!")
//                        }
//            }
//        })
//    }

//    fun initViews(){
//        //2. 카메라 요청시 권한을 먼저 체크하고 승인되었으면 카메라를 연다.
//        binding.btnCamera.setOnClickListener{
//            requirePermissions(arrayOf(android.Manifest.permission.CAMERA), PERM_CAMERA)
//        }
//
//        //5. 갤러리 버튼이 클릭되면 갤러리를 연다.
//        binding.btnGallary.setOnClickListener {
//            openGallary()
//        }
//
////        //검사하기 버튼 클릭시
////        binding.btnDetection.setOnClickListener {
////            startDetection()
////        }
//
//        /* 임시 코드 - 검사하기 버튼 */
//        binding.btnDetection.setOnClickListener {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
//    }
//
//
//    //원본 이미지의 주소를 저장할 변수
//    var realUri: Uri? = null
//
//    //3. 카메라에 찍은 사진을 저장하기 위한 URI를 넘겨준다.
//    fun openCamera(){
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        createImageUri(newFileName(), "image/jpg")?.let{ uri ->
//            realUri = uri
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri)
//            startActivityForResult(intent, REQ_CAMERA)
//        }
//    }
//
//    fun openGallary(){
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = MediaStore.Images.Media.CONTENT_TYPE
//        startActivityForResult(intent, REQ_GALLARY)
//    }
//
//    //원본이미지를 저장할 URI를 MediaStore에 생성하는 메서드
//    fun createImageUri(filename:String, mimeType:String) : Uri?{
//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
//        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
//
//        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//    }
//
//    //파일 이름을 생성하는 메서드
//    fun newFileName() :String{
//        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
//        val filename = sdf.format(System.currentTimeMillis())
//
//        return "${filename}.jpg"
//    }
//
//    //원본 이미지를 불러오는 메서드
//    fun loadBitmap(photoUri:Uri) : Bitmap? {
//        try{
//            return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
//                val source = ImageDecoder.createSource(contentResolver, photoUri)
//                ImageDecoder.decodeBitmap(source)
//            }
//            else{
//                MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
//            }
//        }catch(e:Exception){
//            e.printStackTrace()
//        }
//        return null
//    }
//
//    override fun permissionGranted(requestCode: Int) {
//        when(requestCode){
//            PERM_STROAGE -> initViews()
//            PERM_CAMERA -> openCamera()
//        }
//    }
//
//    override fun permissionDenied(requestCode: Int) {
//        when(requestCode){
//            PERM_STROAGE -> {
//                Toast.makeText(this, "공용 저장소 권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//            PERM_CAMERA -> {
//                Toast.makeText(this, "카메라 권한을 승인해야 카메라를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    fun setImage(uri: Uri){
//        try {
//            val `in`: InputStream? = contentResolver.openInputStream(uri)
//            bitmap = BitmapFactory.decodeStream(`in`)
//            image = InputImage.fromBitmap(bitmap?:return, 0)
//            Log.e("setImage", "이미지 to 비트맵")
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//    }
//
//    //4. 카메라를 찍은후에 호출, 갤러리에서 선택 후 호출
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode == RESULT_OK){
//            when(requestCode){
//                REQ_CAMERA -> {
//                        realUri?.let{ uri ->
//                            val bitmap = loadBitmap(uri)
//                            binding.imagePreview.setImageBitmap(bitmap)
//                            setImage(uri)
////                            try {
////                                val `in`: InputStream? = contentResolver.openInputStream(uri)
////                                bitmap = BitmapFactory.decodeStream(`in`)
////                                image = InputImage.fromBitmap(bitmap?:return, 0)
////                                Log.e("setImage", "이미지 to 비트맵")
////                            } catch (e: FileNotFoundException) {
////                                e.printStackTrace()
////                            }
//                            realUri = null
//                    }
//                }
//                REQ_GALLARY -> {
//                    data?.data?.let{ uri ->
//                        binding.imagePreview.setImageURI(uri)
//                        setImage(uri)
////                        try {
////                            val `in`: InputStream? = contentResolver.openInputStream(uri)
////                            bitmap = BitmapFactory.decodeStream(`in`)
////                            image = InputImage.fromBitmap(bitmap?:return, 0)
////                            Log.e("setImage", "이미지 to 비트맵")
////                        } catch (e: FileNotFoundException) {
////                            e.printStackTrace()
////                        }
//                }
//            }
//        }
//    }
//}
//}




//    private lateinit var personalColorCamera: PersonalColorCamera
//    private val binding: ActivityPersonalColorBinding by lazy {
//        ActivityPersonalColorBinding.inflate(layoutInflater)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        createCameraManager()       // 카메라 매니저 생성
//        val view = binding.root
//        setContentView(view)
//
//        // 카메라 이용 권한 권한 확인
//        if (allPermissionsGranted()) {
//            personalColorCamera.startCamera()
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                REQUIRED_PERMISSIONS,
//                REQUEST_CODE_PERMISSIONS
//            )
//        }
//
//        Log.d("PersonalColorActivity", "정상동작!")
//
//        // 검사하기 버튼 터치 시 회원 가입 화면으로 이동
//        binding.btnSearch.setOnClickListener {
//            var intent = Intent(this, RegisterActivity::class.java)
//            // putExtra 구현 해야함(얼굴형 진단 값, 퍼스널컬러 진단 값 동시 전달)
//            startActivity(intent)
//        }
//
//        // 다음으로 버튼
//        binding.btnNext.setOnClickListener {
//            var intent = Intent(this, RegisterActivity::class.java)
//            // 얼굴형 진단 값이 있는 경우 회원가입으로 넘겨줘야함
//            startActivity(intent)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>, grantResults:
//        IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                personalColorCamera.startCamera()
//            } else {
//                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
//                    .show()
//                finish()
//            }
//        }
//    }
//
//    // 카메라 매니저 생성
//    private fun createCameraManager() {
//        personalColorCamera = PersonalColorCamera(
//            this,
//            binding.previewViewFinder,
//            this,
//        )
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    companion object {
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS = arrayOf(
//            android.Manifest.permission.CAMERA,
//            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//    }
//}