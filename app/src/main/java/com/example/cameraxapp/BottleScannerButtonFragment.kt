package com.example.cameraxapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.cameraxapp.databinding.FragmentBottleScannerButtonBinding

class BottleScannerButtonFragment : Fragment() {
    private var viewBinding: FragmentBottleScannerButtonBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): RelativeLayout? {
        // Inflate the layout for this fragment
        viewBinding = FragmentBottleScannerButtonBinding.inflate(inflater, container, false)

        viewBinding?.bottleScanBtn?.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(CameraFragment())
        }
        return viewBinding?.root
    }
}