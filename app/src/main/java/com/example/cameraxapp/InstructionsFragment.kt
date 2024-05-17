package com.example.cameraxapp

import PrescriptionAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraxapp.PrescriptionList.PrescriptionModel
import com.example.cameraxapp.Room.AppApplication
import com.example.cameraxapp.Room.PrescriptionDao
import kotlinx.coroutines.launch
import androidx.fragment.app.activityViewModels


class InstructionsFragment : Fragment() {
    private lateinit var adapter : PrescriptionAdapter
    private val prescriptionModelList: MutableList<PrescriptionModel> = mutableListOf()
    private lateinit var prescriptionDao: PrescriptionDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instructions, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        val recyclerView: RecyclerView = view.findViewById(R.id.prescriptionRecyclerView)
        adapter = PrescriptionAdapter({ selectedPrescription ->
            handlePrescriptionClick(selectedPrescription)
        }) { selectedPrescription ->
            openExpirationFragment(selectedPrescription)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val application = requireActivity().application as AppApplication
        prescriptionDao = application.db.prescriptionDao()

        // Retrieve medicine data from the database
        lifecycleScope.launch {

            prescriptionDao.getAllPrescriptions().collect() {prescriptionEntities ->
                val prescriptions = prescriptionEntities.map { entity ->
                    PrescriptionModel(
                        medicineName = entity.medicineName,
                        details = PrescriptionModel.Details.fromPrescriptionEntity(entity),
                        isExpanded = false // Initially set to false for collapsed state
                    )
                }
                prescriptionModelList.clear()
                prescriptionModelList.addAll(prescriptions)
                adapter.updateList(prescriptionModelList)
            }
        }

    }

    private fun handlePrescriptionClick(selectedPrescription: PrescriptionModel) {
        selectedPrescription.isExpanded = !selectedPrescription.isExpanded
        adapter.notifyItemChanged(prescriptionModelList.indexOf(selectedPrescription))
    }

    private fun openExpirationFragment(selectedPrescription: PrescriptionModel) {
        val expirationFragment = ExpDateFragment().apply {
            arguments = Bundle().apply {
                putString("prescriptionName", selectedPrescription.medicineName)
            }
        }
        (activity as? MainActivity)?.replaceFragment(expirationFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = InstructionsFragment()
    }

}