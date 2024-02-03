package com.example.cameraxapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_instructions.expdateAnswer

class InstructionsFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_instructions, container, false)
        val expdateButton = view.findViewById<Button>(R.id.expdateButton)

        viewModel.expDate.observe(viewLifecycleOwner) { date ->
            expdateAnswer.text = date
        }

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