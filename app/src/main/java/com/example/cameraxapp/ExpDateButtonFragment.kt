package com.example.cameraxapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cameraxapp.databinding.FragmentExpDateButtonBinding
import com.example.cameraxapp.databinding.FragmentQrScannerBinding

class ExpDateButtonFragment : Fragment() {
    private var viewBinding: FragmentExpDateButtonBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentExpDateButtonBinding.inflate(inflater, container, false)

        viewBinding!!.expDateBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, CameraFragment())
            transaction.commit()
        }
        return viewBinding!!.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExpDateButtonFragment().apply {
            }
    }
}