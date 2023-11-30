package com.example.cameraxapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cameraxapp.databinding.FragmentCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: add focusing and zooming capability

class CameraFragment : Fragment() {
    private lateinit var viewBinding: FragmentCameraBinding
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private var t1: TextToSpeech? = null

    private var previousMedicine : String = ""
    private var currentMedicine : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding =  FragmentCameraBinding.inflate(inflater, container, false)

        t1 = TextToSpeech(activity) {
            if (it != TextToSpeech.ERROR) {
                t1?.language = Locale.ENGLISH
            }
        }

        //Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()

        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
            startCamera()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        BottleDictionary.initialize()

        return viewBinding.root
    }

    private class YourImageAnalyzer(private val displayText : TextView, private var t1 : TextToSpeech?, private var previousMedicine : String, private var currentMedicine : String, private val context: Context) : ImageAnalysis.Analyzer {
        val processor: FrameProcessor = FrameProcessor()
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image


//            // Initializing the Image Recognition Inferencer
            val inferencer = InferenceLocal()
            val classification = inferencer.inference(context, imageProxy)

            var name: String
            val image = mediaImage?.let { InputImage.fromMediaImage(it, 0) }
            image?.let {
                        recognizer.process(it)
                            .addOnSuccessListener { visionText ->
                                name = processor.processVisionText(visionText)
                                if (name != "No Bottle Type Found.") {
                                    Log.w("Bottle Found:", name)
                                    if (classification == name) { // if image recognition and text recognition are same, then go with text
                                        displayText.text = name
                                    } else if (classification.isBlank()) { // if image is blank, then do text
                                        displayText.text = name
                                    } else { // if image isn't blank and is different than text, use image
                                        displayText.text = classification
                                    }


                                    currentMedicine = name
                                    if (currentMedicine != previousMedicine) {
                                        //t1?.speak(name, TextToSpeech.QUEUE_FLUSH, null)
                                    }
                                    previousMedicine = name

                                }

                            }
                            .addOnFailureListener { _ -> }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    }
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = activity?.let { ProcessCameraProvider.getInstance(it) }

        activity?.let { ContextCompat.getMainExecutor(it) }?.let { it ->
            cameraProviderFuture?.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder().build() // For Capturing Images

                val changedTextView = viewBinding.textViewId2

                val correctImageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, YourImageAnalyzer(changedTextView, t1, previousMedicine, currentMedicine, requireActivity()))}
                        // Correctly Analyzes Images to spit out text

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

            }, it)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        activity?.let { it1 -> ContextCompat.checkSelfPermission(it1.baseContext, it) } == PackageManager.PERMISSION_GRANTED
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
                Toast.makeText(activity,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                //finish()
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