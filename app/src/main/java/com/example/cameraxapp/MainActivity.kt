package com.example.cameraxapp


//import kotlinx.android.synthetic.main.activity_main.text_view_id2
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraxapp.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit

    // TODO: add focusing and zooming capability
    // TODO: Implement NDC


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private var t1: TextToSpeech? = null

    private var previousMedicine : String = ""
    private var currentMedicine : String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        t1 = TextToSpeech(this) {
            if (it != TextToSpeech.ERROR) {
                t1?.language = Locale.ENGLISH
            }
        }

        //Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        BottleDictionary.initialize()

    }



    private class YourImageAnalyzer(private val displayText : TextView, private var t1 : TextToSpeech?, private var previousMedicine : String, private var currentMedicine : String) : ImageAnalysis.Analyzer {
        val processor: FrameProcessor = FrameProcessor()
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val map = HashMap<String, Int>()





        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            var name: String

            if (mediaImage != null) {


                val image = InputImage.fromMediaImage(mediaImage, 0)
                val result = recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        name = processor.processVisionText(visionText)

                        if (name != "No Bottle Type Found." ) {
                            println("Bottle Found: $name")
                            displayText.text = name

                            currentMedicine = name
                            if (currentMedicine != previousMedicine) {
                                //t1?.speak(name, TextToSpeech.QUEUE_FLUSH, null)
                            }
                            previousMedicine = name


                            //displayText.setText(name)

                        }
//                        else if (name.isEmpty() || name == "No Bottle Type Found.") {
//                        }


                    }
                    .addOnFailureListener { e -> }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }


            }

        }

    }
    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build() // For Capturing Images

//            val imageAnalyzer = ImageAnalysis.Builder()
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                        Log.d(TAG, "Average luminosity: $luma")
//                    })
//                } // Analyzes Images

            val changedTextView = findViewById<View>(R.id.text_view_id2) as TextView

            val correctImageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, YourImageAnalyzer(changedTextView, t1, previousMedicine, currentMedicine) )
                    //{ text -> identifiedWord = text}
                } // Correctly Analyzes Images to spit out text




            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA



            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, correctImageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    companion object {
        private const val TAG = "CameraXApp"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}