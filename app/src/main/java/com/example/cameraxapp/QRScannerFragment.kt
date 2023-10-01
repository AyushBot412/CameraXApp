package com.example.cameraxapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.example.cameraxapp.databinding.FragmentQrScannerBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class QRScannerFragment : Fragment() {
    private var viewBinding: FragmentQrScannerBinding? = null
    private lateinit var barLauncher: ActivityResultLauncher<ScanOptions>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentQrScannerBinding.inflate(inflater, container, false)

        // registering barLauncher in lifecycle to prevent errors
        barLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult? ->
            if (result?.contents != null) {

                // Dialog Box
                val builder = AlertDialog.Builder(context)
                    .setTitle("Download Instructions?")
                    .setMessage(result.contents)
                    .setCancelable(true)
                    .setPositiveButton("Yes") { dialogInterface, _ ->
                        Toast.makeText(context, "Instructions Uploaded", Toast.LENGTH_LONG).show()
                        dialogInterface.dismiss() // where instructions should upload
                    }
                    .setNegativeButton("No"){dialogInterface, _ ->
                        Toast.makeText(context, "Instructions not Uploaded", Toast.LENGTH_LONG).show()
                        dialogInterface.dismiss() // nothing is done
                    }.show()

            }
        }

        viewBinding!!.qrScannerBtn.setOnClickListener {
            scanCode()
        }

        return viewBinding!!.root
    }

    // QRCode functionality
    private fun scanCode() {
        val options = ScanOptions()
        options.setPrompt("Volume up to flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.captureActivity = CaptureAct::class.java
        barLauncher.launch(options)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
         QRScannerFragment().apply {

         }
    }

}