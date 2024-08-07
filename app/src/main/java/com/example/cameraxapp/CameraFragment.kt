package com.example.cameraxapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cameraxapp.databinding.FragmentCameraBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.TorchState
import com.example.cameraxapp.R.drawable.flash_off_icon_background
import com.example.cameraxapp.R.drawable.flash_on_icon_background
import com.example.cameraxapp.R.drawable.alphagan_textview_attributes
import com.example.cameraxapp.R.drawable.combigan_textview_attributes
import com.example.cameraxapp.R.drawable.dorzolamide_textview_attributes
import com.example.cameraxapp.R.drawable.latanoprost_textview_attributes
import com.example.cameraxapp.R.drawable.predforte_textview_attributes
import com.example.cameraxapp.R.drawable.rhopressa_textview_attributes
import com.example.cameraxapp.R.drawable.rocklatan_textview_attributes
import com.example.cameraxapp.R.drawable.vigamox_textview_attributes



open class CameraFragment : Fragment() {
    private lateinit var viewBinding: FragmentCameraBinding
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private var t1: TextToSpeech? = null

    private var previousMedicine : String = ""
    private var currentMedicine : String = ""

    private var camera: Camera? = null
    private var cameraInformation: CameraInfo? = null
    private var maxZoomRatio: Float = 1f
    private lateinit var enableTorchLF: ListenableFuture<Void>
    private var zoomSeekBar : SeekBar? = null


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

        val torchButton: ImageButton = viewBinding.torch

        torchButton.setOnClickListener {
            // Turn torch on when the button is clicked
            toggleTorch()
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

    private class YourImageAnalyzer(
        private val displayText : TextView,
        private var previousMedicine : String,
        private var currentMedicine : String,
        private val context: Context,
        private val cameraFragment: CameraFragment
    ) : ImageAnalysis.Analyzer {
        val processor: FrameProcessor = FrameProcessor()
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


        private var mediaPlayer: MediaPlayer? = null

        fun changeCameraBackgroundColor(medicineName: String) {
            when (medicineName) {
                "ALPHAGAN" -> cameraFragment.viewBinding.textViewId2.setBackgroundResource(alphagan_textview_attributes)
                "COMBIGAN" -> {cameraFragment.viewBinding.textViewId2.setBackgroundResource(combigan_textview_attributes)
                                cameraFragment.viewBinding.textViewId2.setTextColor(cameraFragment.resources.getColor(R.color.COMBIGAN_TEXT))}
                "DORZOLAMIDE" -> cameraFragment.viewBinding.textViewId2.setBackgroundResource(dorzolamide_textview_attributes)
                "LATANOPROST" -> cameraFragment.viewBinding.textViewId2.setBackgroundResource(latanoprost_textview_attributes)
                "PREDFORTE" -> {cameraFragment.viewBinding.textViewId2.setBackgroundResource(predforte_textview_attributes)
                                cameraFragment.viewBinding.textViewId2.setTextColor(cameraFragment.resources.getColor(R.color.black))}
                "RHOPRESSA" -> cameraFragment.viewBinding.textViewId2.setBackgroundResource(rhopressa_textview_attributes)
                "ROCKLATAN" -> cameraFragment.viewBinding.textViewId2.setBackgroundResource(rocklatan_textview_attributes)
                "VIGAMOX" -> cameraFragment.viewBinding.textViewId2.setBackgroundResource(vigamox_textview_attributes)
            }


        }

        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image


            // Initializing the Image Recognition Inferencer
            val inferencer = InferenceLocal()
            val classification = inferencer.inference(context, imageProxy)

            var name: String
            val image = mediaImage?.let { InputImage.fromMediaImage(it, 0) }
            image?.let {
                        recognizer.process(it)
                            .addOnSuccessListener { visionText ->
                                name = processor.processVisionText(visionText, "bottle_name")
                                if (name != "No Bottle Type Found.") {
                                    Log.w("Bottle Found:", name)
                                    cameraFragment.viewBinding.textViewId2.setTextColor(cameraFragment.resources.getColor(R.color.white))

                                    if (classification == name) { // if image recognition and text recognition are same, then go with image
                                        displayText.text = classification
                                        changeCameraBackgroundColor(classification)
                                    } else if (classification.isBlank()) { // if image is blank, then do text
                                        displayText.text = name
                                        changeCameraBackgroundColor(name)
                                    } else { // if image isn't blank and is different than text, use image
                                        displayText.text = classification
                                        changeCameraBackgroundColor(classification)

                                    }


                                    currentMedicine = name
                                    if (currentMedicine != previousMedicine) {


                                        mediaPlayer?.release()
                                        mediaPlayer = when (name) {
                                            "ALPHAGAN" -> MediaPlayer.create(context, R.raw.alphagan)
                                            "COMBIGAN" -> MediaPlayer.create(context, R.raw.combigan)
                                            "DORZOLAMIDE" -> MediaPlayer.create(context, R.raw.dorzolamide)
                                            "LATANOPROST" -> MediaPlayer.create(context, R.raw.latanoprost)
                                            "PREDFORTE" -> MediaPlayer.create(context, R.raw.predforte)
                                            "RHOPRESSA" -> MediaPlayer.create(context, R.raw.rhopressa)
                                            "ROCKLATAN" -> MediaPlayer.create(context, R.raw.rocklatan)
                                            "VIGAMOX" -> MediaPlayer.create(context, R.raw.vigamox)
                                            else -> null
                                        }
                                        mediaPlayer?.start()

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
                        it.setAnalyzer(cameraExecutor, YourImageAnalyzer(changedTextView, previousMedicine, currentMedicine, requireActivity(), this))}
                        // Correctly Analyzes Images to spit out text

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, correctImageAnalyzer)




                    cameraInformation = camera!!.cameraInfo
                    maxZoomRatio = cameraInformation?.zoomState?.value?.maxZoomRatio ?: 1f

                    setUpTapToFocusAndPinchToZoom(camera!!.cameraControl, cameraInformation)



                } catch(exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }


            }, it)
        }
    }

    private fun toggleTorch() {
        if (camera!!.cameraInfo.hasFlashUnit()) { // checks for flashlight
            if (camera!!.cameraInfo.torchState.value == TorchState.ON) {
                enableTorchLF = camera!!.cameraControl.enableTorch(false) // turning off torch


                viewBinding.torch.setImageResource(R.drawable.flash_off) // changing icon
                viewBinding.torch.setBackgroundResource(flash_off_icon_background) // changing background color


            } else {
                enableTorchLF = camera!!.cameraControl.enableTorch(true)

                viewBinding.torch.setImageResource(R.drawable.flash_on)
                viewBinding.torch.setBackgroundResource(flash_on_icon_background)
            }

            enableTorchLF.addListener({
                try {
                    enableTorchLF.get()
                    // At this point, the torch has been successfully enabled or disabled
                } catch (exc: Exception) {
                    Log.e(TAG, "Torch functionality failed.", exc)
                }
            }, cameraExecutor /* Executor where the runnable callback code is run */)
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTapToFocusAndPinchToZoom(cameraControl: CameraControl, cameraInfo: CameraInfo?) {

        val factory = viewBinding.viewFinder.meteringPointFactory
        zoomSeekBar = viewBinding.seekbar
        val progressText = viewBinding.zoomProgress

        val scaleGestureDetector = ScaleGestureDetector(requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    // Get the current zoom ratio
                    val currentZoomRatio = cameraInfo?.zoomState?.value?.zoomRatio ?: 1f

                    // Calculate the new zoom ratio
                    val newZoomRatio = currentZoomRatio * detector.scaleFactor

                    // Set the new zoom ratio with bounds
                    cameraControl.setZoomRatio(newZoomRatio.coerceIn(1f, cameraInfo!!.zoomState.value!!.maxZoomRatio))

                    // Update SeekBar progress based on zoom ratio
                    val progress = ((newZoomRatio - 1f) / (cameraInfo.zoomState.value!!.maxZoomRatio - 1f) * 100).toInt()
                    zoomSeekBar!!.progress = progress

                    // Update Seekbar when pinch is used to change zoom
                    val percent = "Zoom: $progress%"
                    if (progress in 0..100) {
                        progressText.text = percent
                    }
                    return true
                }
            })

        zoomSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Calculate the zoom ratio based on SeekBar progress
                    val newZoomRatio = 1f + progress / 100f *
                            (cameraInfo!!.zoomState.value!!.maxZoomRatio - 1f)

                    // Set the new zoom ratio with bounds
                    cameraControl.setZoomRatio(newZoomRatio.coerceIn(1f, cameraInfo.zoomState.value!!.maxZoomRatio))

                    // Update Seekbar when seekbar is used to change zoom
                    val percent = "Zoom: $progress%"
                    if (progress in 0..100) {
                        progressText.text = percent
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed for this example
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed for this example
            }
        })

        viewBinding.viewFinder.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Handle tap-to-focus gesture here
                    val point = factory.createPoint(event.x, event.y)
                    val action = FocusMeteringAction.Builder(point).build()
                    cameraControl.startFocusAndMetering(action)
                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener scaleGestureDetector.onTouchEvent(event)
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        activity?.let { it1 -> ContextCompat.checkSelfPermission(it1.baseContext, it) } == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    @Deprecated("Deprecated in Java")
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