package com.avengers.maskfitting.mafiafin.personal_color

import android.R
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avengers.maskfitting.mafiafin.account.RegisterActivity
import com.avengers.maskfitting.mafiafin.databinding.ActivityPersonalCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.android.synthetic.main.activity_personal_camera.*
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat


// 퍼스널 컬러 진단 액티비티
// 이전 액티비티(얼굴형 진단)에서 intent 로 넘어온 값 확인 동작 구현 필요
class PersonalColorActivity : PersonalBaseActivity() {

    var customProgressDialog: ProgressDialog? = null

    //권한처리
    val PERM_STROAGE = 9
    val PERM_CAMERA = 10

    //카메라요청
    val REQ_CAMERA = 11
    //갤러리요청
    val REQ_GALLARY = 12

    var image: InputImage? = null
    var bitmap: Bitmap? = null

    private lateinit var binding: ActivityPersonalCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //로딩창 객체 생성
        customProgressDialog = ProgressDialog(this)
        //로딩창을 투명하게
        customProgressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))



        //1. 공용저장소 권한이 있는지 확인
        requirePermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERM_STROAGE)

        // High-accuracy landmark detection and face classification
        // detector에 대한 옵션 설정
        // High-accuracy landmark detection and face classification
        // detector에 대한 옵션 설정
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()

        // detector생성
        val detector: FaceDetector = FaceDetection.getClient(highAccuracyOpts)

        binding.btnDetection.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {


                customProgressDialog!!.show()



                // 생성한 Input Image를 처리한다.
                //val result: Task<List<Face>> = detector.process(image!!)
                val result = detector.process(image!!)
                    .addOnSuccessListener{ faces -> // 탐지가 성공할 경우
                        for (face in faces) {
                            val bounds = face.boundingBox
                            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                            // nose available):
                            val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
                            leftEye?.let {
                                val leftEyePos = leftEye.position
                                face_info.setText("왼쪽눈 : $leftEyePos")
                                Log.d("얼굴", "가로 : ${bounds.width()}, 세로 : ${bounds.height()}")
                                Log.d("왼쪽눈", "왼쪽눈 : $leftEyePos")
                            }
                            val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
                            rightEye?.let {
                                val rightEyePos = rightEye.position
                                face_info.setText("오른쪽눈 : $rightEyePos")
                                Log.d("오른쪽눈", "오른쪽눈 : $rightEyePos")
                            }
                            val leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)
                            leftCheek?.let {
                                val leftCheekPos = leftCheek.position
                            }
                            val rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK)
                            rightCheek?.let {
                                val rightCheekPos = rightCheek.position
                            }
                            val mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)
                            mouthBottom?.let {
                                val mouthBottomPos = mouthBottom.position
                            }
                            // If contour detection was enabled:
                            val leftEyebrowBottom = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM)?.points
                            Log.d("왼쪽눈썹아래", "$leftEyebrowBottom")
                            val leftEyebrowTop = face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points
                            val rightEyebrowBottom = face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM)?.points
                            val rightEyebrowTop = face.getContour(FaceContour.RIGHT_EYEBROW_TOP)?.points
                            //face_info.setText("왼쪽눈썹(아래) : $leftEyebrowBottom 왼쪽눈썹(위) : $leftEyebrowTop 오른쪽눈썹(아래) : $rightEyebrowBottom 오른쪽눈썹(위) : $rightEyebrowTop")
                            //face_info.setText("왼쪽눈 :  $leftEyePos 오른쪽눈 :  $rightEyePos 왼쪽볼 : 오른쪽볼 : 왼쪽눈썹(위) : 왼쪽눈썹(아래) : 오른쪽눈썹(위) : 오른쪽눈썹(아래) : ")
                        }

                        // 처리한 이미지에 대한 결과(얼굴에 대한 정보)를 텍스트뷰에 띄워준다.
                            //face_info.setText("왼쪽눈 :  $leftEye")

                        //로딩창 종료료
                       customProgressDialog?.dismiss()
//                        val intent = Intent(applicationContext, ResultActivity::class.java)
//                        startActivity(intent)
                        }
                    .addOnFailureListener{ // 탐지가 실패할 경우
                            Log.e("faces: ", "실패!")
                        }
            }
        })
    }

    fun initViews(){
        //2. 카메라 요청시 권한을 먼저 체크하고 승인되었으면 카메라를 연다.
        binding.btnCamera.setOnClickListener{
            requirePermissions(arrayOf(android.Manifest.permission.CAMERA), PERM_CAMERA)
        }

        //5. 갤러리 버튼이 클릭되면 갤러리를 연다.
        binding.btnGallary.setOnClickListener {
            openGallary()
        }

//        //검사하기 버튼 클릭시
//        binding.btnDetection.setOnClickListener {
//            startDetection()
//        }

        /* 임시 코드 - 검사하기 버튼 */
        binding.btnDetection.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    //원본 이미지의 주소를 저장할 변수
    var realUri: Uri? = null

    //3. 카메라에 찍은 사진을 저장하기 위한 URI를 넘겨준다.
    fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        createImageUri(newFileName(), "image/jpg")?.let{ uri ->
            realUri = uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri)
            startActivityForResult(intent, REQ_CAMERA)
        }
    }

    fun openGallary(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_GALLARY)
    }

    //원본이미지를 저장할 URI를 MediaStore에 생성하는 메서드
    fun createImageUri(filename:String, mimeType:String) : Uri?{
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    //파일 이름을 생성하는 메서드
    fun newFileName() :String{
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())

        return "${filename}.jpg"
    }

    //원본 이미지를 불러오는 메서드
    fun loadBitmap(photoUri:Uri) : Bitmap? {
        try{
            return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
                val source = ImageDecoder.createSource(contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            }
            else{
                MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
        return null
    }

    override fun permissionGranted(requestCode: Int) {
        when(requestCode){
            PERM_STROAGE -> initViews()
            PERM_CAMERA -> openCamera()
        }
    }

    override fun permissionDenied(requestCode: Int) {
        when(requestCode){
            PERM_STROAGE -> {
                Toast.makeText(this, "공용 저장소 권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            PERM_CAMERA -> {
                Toast.makeText(this, "카메라 권한을 승인해야 카메라를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setImage(uri: Uri){
        try {
            val `in`: InputStream? = contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(`in`)
            image = InputImage.fromBitmap(bitmap?:return, 0)
            Log.e("setImage", "이미지 to 비트맵")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    //4. 카메라를 찍은후에 호출, 갤러리에서 선택 후 호출
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                REQ_CAMERA -> {
                        realUri?.let{ uri ->
                            val bitmap = loadBitmap(uri)
                            binding.imagePreview.setImageBitmap(bitmap)
                            setImage(uri)
//                            try {
//                                val `in`: InputStream? = contentResolver.openInputStream(uri)
//                                bitmap = BitmapFactory.decodeStream(`in`)
//                                image = InputImage.fromBitmap(bitmap?:return, 0)
//                                Log.e("setImage", "이미지 to 비트맵")
//                            } catch (e: FileNotFoundException) {
//                                e.printStackTrace()
//                            }
                            realUri = null
                    }
                }
                REQ_GALLARY -> {
                    data?.data?.let{ uri ->
                        binding.imagePreview.setImageURI(uri)
                        setImage(uri)
//                        try {
//                            val `in`: InputStream? = contentResolver.openInputStream(uri)
//                            bitmap = BitmapFactory.decodeStream(`in`)
//                            image = InputImage.fromBitmap(bitmap?:return, 0)
//                            Log.e("setImage", "이미지 to 비트맵")
//                        } catch (e: FileNotFoundException) {
//                            e.printStackTrace()
//                        }
                }
            }
        }
    }
}
}




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