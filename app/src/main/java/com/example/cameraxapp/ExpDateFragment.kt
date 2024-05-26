package com.example.cameraxapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
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
import com.example.cameraxapp.databinding.FragmentExpCameraBinding
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.cameraxapp.Room.AppApplication
import com.example.cameraxapp.Room.Dao
import kotlinx.android.synthetic.main.fragment_exp_camera.view.conditionalLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ExpDateFragment : Fragment() {
    private lateinit var viewBinding: FragmentExpCameraBinding
    val instructionsFragment : Fragment = InstructionsFragment()
    private var imageCapture: ImageCapture? = null
    private val viewModel: SharedViewModel by activityViewModels()

    private lateinit var cameraExecutor: ExecutorService

    private var t1: TextToSpeech? = null

    private var currentMedicine : String = ""

    private var camera: Camera? = null
    private var cameraInformation: CameraInfo? = null
    private var maxZoomRatio: Float = 1f
    private lateinit var enableTorchLF: ListenableFuture<Void>
    private var zoomSeekBar : SeekBar? = null

    private lateinit var dao: Dao

    private lateinit var retryButton: Button
    private lateinit var conditionalLayout: View

    private var isTimedOut: Boolean = false

    private val timer = object: CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) { }
        override fun onFinish() {
            // reached 20 seconds
            Log.w("timer test", "asdf")

            conditionalLayout.visibility = VISIBLE
            isTimedOut = true

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding =  FragmentExpCameraBinding.inflate(inflater, container, false)
        val rootView = viewBinding.root

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

        conditionalLayout = rootView.findViewById(R.id.conditionalLayout)
        retryButton = conditionalLayout.findViewById(R.id.conditionalButton)

        retryButton.setOnClickListener {
            Log.w("retry button", "clicked")

            // reset timer to 0
            startTimer()
            // set dim to false

            isTimedOut = false
            conditionalLayout.visibility = INVISIBLE

        }

        startTimer()

        val application = requireActivity().application as AppApplication
        dao = application.db.Dao()

        // Retrieve prescription name from arguments
        val medicineName = arguments?.getString("medicineName")

        // Observe changes to the expiration date
        viewModel.expDate.observe(viewLifecycleOwner) { date ->
            // Update the expiration date in the database
            medicineName?.let { name ->
                //Toast.makeText(requireContext(), "Here is ${name} with ${date}", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        dao.editExpirationDate(name, date)
                    }
                }
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        BottleDictionary.initialize()

        return viewBinding.root
    }

    inner class YourImageAnalyzer(
        private val displayText: TextView,
        private var t1: TextToSpeech?,
        private var currentMedicine: String,
        private val context: Context,
        viewModel: SharedViewModel
    ) : ImageAnalysis.Analyzer {
        val processor: FrameProcessor = FrameProcessor()
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image


//            // Initializing the Image Recognition Inferencer
            val inferencer = InferenceLocal()

            var date: String
            val image = mediaImage?.let { InputImage.fromMediaImage(it, 0) }
            image?.let {
                recognizer.process(it)
                    .addOnSuccessListener { visionText ->
                        date = processor.processVisionText(visionText, "exp_date")
                        if (date != "No Date Found.") {

                            Log.w("Classified Date:", date)
                            displayText.text = date

                            setExpDate(date)
                        }
                    }
                    .addOnFailureListener { _ -> }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }
        fun setExpDate(date: String) {
            if (isTimedOut) {
                // dont do popup
                return
            }
            timer.cancel()
            viewModel.setExpDate(date)
            // create toast for success, then after like 2 seconds reroute to instructions fragment
            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()

            val newFragment = InstructionsFragment()  // Replace with your specific fragment
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, newFragment)
            transaction.commit()
//            Handler(Looper.getMainLooper()).postDelayed({
//                // Navigate back to the previous fragment
//
//            }, 2000)



//            val count = parentFragmentManager.backStackEntryCount
//            Log.d("BackStack", "Total Entries: $count")
//            for (i in 0 until count) {
//                val entry = parentFragmentManager.getBackStackEntryAt(i)
//                Log.d("BackStack", "Entry $i: ${entry.name}")
//            }


            // (go back previous fragment state)
//            Handler(Looper.getMainLooper()).postDelayed({
//                // Navigate back to the previous fragment
//                parentFragmentManager.popBackStack()
//            }, 2000)
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
                        it.setAnalyzer(cameraExecutor, YourImageAnalyzer(changedTextView, t1, currentMedicine, requireActivity(), viewModel))}
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

    private fun startTimer() {
        timer.start()
    }

    private fun stopTimer() {
        timer.cancel()
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