package com.example.cameraxapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class InstructionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_instructions, container, false)
        val expdateButton = view.findViewById<Button>(R.id.expdateButton)

        expdateButton.setOnClickListener{
            val expdateFragment = ExpDateFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, expdateFragment)
                .addToBackStack(null)
                .commit()
        }
        return view

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_instructions, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InstructionsFragment().apply {
            }
    }
}