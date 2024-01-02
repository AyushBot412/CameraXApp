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
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.MeteringPoint
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
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
import androidx.constraintlayout.widget.ConstraintSet.Motion
import com.example.cameraxapp.R.drawable.flash_off_icon_background
import com.example.cameraxapp.R.drawable.flash_on_icon_background


class CameraFragment : Fragment() {
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
                                        // Text To Speech
                                        //t1?.speak(name, TextToSpeech.QUEUE_ADD, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)

                                        if (name == "ALPHAGAN") {
                                            val mediaPlayer = MediaPlayer.create(context, R.raw.alphagan)
                                            mediaPlayer.start()

                                        } else if (name == "COMBIGAN") {
                                            val mediaPlayer = MediaPlayer.create(context, R.raw.combigan)
                                            mediaPlayer.start()

                                        } else if (name == "DORZOLAMIDE") {
                                            val mediaPlayer = MediaPlayer.create(context, R.raw.dorzolamide)
                                            mediaPlayer.start()

                                        } else if (name == "LATANOPROST") {
                                            val mediaPlayer = MediaPlayer.create(context, R.raw.latanoprost)
                                            mediaPlayer.start()

                                        } else if (name == "PREDFORTE") {
                                            val mediaPlayer = MediaPlayer.create(context, R.raw.predforte)
                                            mediaPlayer.start()

                                        } else if (name == "RHOPRESSA") {
                                            val mediaPlayer = MediaPlayer.create(context, R.raw.rhopressa)
                                            mediaPlayer.start()

                                        } else if (name == "ROCKLATAN") {
                                            val mediaPlayer = MediaPlayer.create(context, R.raw.rocklatan)
                                            mediaPlayer.start()

                                        } else if (name == "VIGAMOX") {
                                            val mediaPlayer = MediaPlayer.create(context, R.raw.vigamox)
                                            mediaPlayer.start()

                                        }


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
                    camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, correctImageAnalyzer)


                    //enableZoomControls(camera!!.cameraInfo, camera!!.cameraControl)


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


    @SuppressLint("ClickableViewAccessibility")
    private fun enableZoomControls(cameraInfo : CameraInfo?, cameraControl: CameraControl) {

        zoomSeekBar = viewBinding.seekbar
        val progressText = viewBinding.zoomProgress

        // Enable zoom controls for Pinch-to-Zoom
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
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
        }

        // Create listener for pinching
        val scaleGestureDetector = ScaleGestureDetector(requireContext(), listener)
        viewBinding.viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }



        // I initially planned to have the seekbar only display on the screen as the user is pinching,
        // but I think for older users, it's better to give them an easier way to zoom.

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
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTapToFocus(cameraControl: CameraControl) {
        viewBinding.viewFinder.setOnTouchListener(OnTouchListener { view: View, motionEvent: MotionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> return@OnTouchListener true
//                MotionEvent.ACTION_POINTER_UP -> {
//                    return@OnTouchListener true
//                }
//                MotionEvent.ACTION_POINTER_DOWN -> {
//                    return@OnTouchListener true
//                }
//                MotionEvent.ACTION_POINTER_INDEX_MASK -> {
//                    return@OnTouchListener true
//                }
//                MotionEvent.ACTION_POINTER_INDEX_SHIFT -> {
//                    return@OnTouchListener true
//                }
//                MotionEvent.AXIS_GESTURE_PINCH_SCALE_FACTOR-> {
//                    return@OnTouchListener true
//                }
                MotionEvent.ACTION_UP -> {
                    // Get the MeteringPointFactory from PreviewView
                    val factory = viewBinding.viewFinder.meteringPointFactory

                    // Create a MeteringPoint from the tap coordinates
                    val point = factory.createPoint(motionEvent.x, motionEvent.y)

                    // Create a MeteringAction from the MeteringPoint, you can configure it to specify the metering mode
                    val action = FocusMeteringAction.Builder(point).build()

                    // Trigger the focus and metering. The method returns a ListenableFuture since the operation
                    // is asynchronous. You can use it get notified when the focus is successful or if it fails.
                    cameraControl.startFocusAndMetering(action)

                    return@OnTouchListener true
                }
                else -> return@OnTouchListener false
            }
        })
    }

    //            val factory = SurfaceOrientedMeteringPointFactory(
//                viewBinding.viewFinder.width.toFloat(), viewBinding.viewFinder.height.toFloat()
//            )
//            val point: MeteringPoint = factory.createPoint(event.x, event.y)
//            try {
//                val action = FocusMeteringAction.Builder(point).build()
//                cameraControl.startFocusAndMetering(action)
//            } catch (e: CameraInfoUnavailableException) {
//                Log.d("CameraFragment", "Cannot access camera info", e)
//            }
//
//            true



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