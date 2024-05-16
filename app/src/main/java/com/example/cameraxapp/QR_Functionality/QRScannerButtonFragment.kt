package com.example.cameraxapp.QR_Functionality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.cameraxapp.R
import com.example.cameraxapp.databinding.FragmentQrScannerButtonBinding


class QRScannerButtonFragment : Fragment() {
    private var viewBinding: FragmentQrScannerButtonBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): RelativeLayout? {
        viewBinding = FragmentQrScannerButtonBinding.inflate(inflater, container, false)

        viewBinding?.qrScannerBtn?.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, QRScannerImplFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        return viewBinding?.root
    }
}