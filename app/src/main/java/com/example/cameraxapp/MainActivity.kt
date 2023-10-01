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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.cameraxapp.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


//typealias LumaListener = (luma: Double) -> Unit

    // TODO: add focusing and zooming capability
    // TODO: Implement NDC


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        // define fragments
        val bottleScannerButtonFragment : Fragment = BottleScannerButtonFragment()
        val QRScannerFragment : Fragment = QRScannerFragment()
        val instructionsFragment : Fragment = InstructionsFragment()

        // handle navigation selection
        viewBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.navigation_bottle_scanner -> fragment = bottleScannerButtonFragment
                R.id.navigation_qr ->fragment= QRScannerFragment
                R.id.navigation_instructions -> fragment= instructionsFragment
            }
            replaceFragment(fragment)
            true
        }
        // set default selection
        viewBinding.bottomNavigationView.selectedItemId = R.id.navigation_bottle_scanner
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}

