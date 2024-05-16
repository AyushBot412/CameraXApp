package com.example.cameraxapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cameraxapp.databinding.FragmentBottleScannerButtonBinding

class BottleScannerButtonFragment : Fragment() {
    private var viewBinding: FragmentBottleScannerButtonBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentBottleScannerButtonBinding.inflate(inflater, container, false)

        viewBinding!!.bottleScanBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, CameraFragment())
            transaction.commit()
        }
        return viewBinding!!.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BottleScannerButtonFragment().apply {
            }
    }
}