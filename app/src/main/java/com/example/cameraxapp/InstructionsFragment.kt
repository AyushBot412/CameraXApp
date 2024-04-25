package com.example.cameraxapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.cameraxapp.Room.AppApplication
import com.example.cameraxapp.Room.PrescriptionDao
import com.example.cameraxapp.Room.PrescriptionEntity
import kotlinx.android.synthetic.main.fragment_instructions.expdateAnswer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class InstructionsFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var prescriptionDao: PrescriptionDao

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireActivity().application as AppApplication
        prescriptionDao = application.db.prescriptionDao()

        // Retrieve list of prescriptions
        lifecycleScope.launch {
            val prescriptions = prescriptionDao.getAllPrescriptions()
            // Now we have list of prescriptions
            // TODO: Next, populate UI
        }
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InstructionsFragment().apply {
            }
    }


}