package com.example.cameraxapp.QR_Functionality

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.cameraxapp.MainActivity
import com.example.cameraxapp.databinding.FragmentQrScannerButtonBinding

class QRScannerButtonFragment : Fragment() {
    private var viewBinding: FragmentQrScannerButtonBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): RelativeLayout? {
        viewBinding = FragmentQrScannerButtonBinding.inflate(inflater, container, false)

        val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                (activity as? MainActivity)?.replaceFragment(QRCameraFragment())
            } else {
                Toast.makeText(context, "Need camera permission", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding?.qrScannerBtn?.setOnClickListener{
            requestCamera.launch(android.Manifest.permission.CAMERA)
        }

        return viewBinding?.root
    }


}