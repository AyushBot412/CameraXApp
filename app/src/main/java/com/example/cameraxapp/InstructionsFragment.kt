package com.example.cameraxapp

import Adapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraxapp.PrescriptionList.Model
import com.example.cameraxapp.Room.AppApplication
import com.example.cameraxapp.Room.Dao
import kotlinx.coroutines.launch


class InstructionsFragment : Fragment() {
    private lateinit var adapter : Adapter
    private val modelList: MutableList<Model> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instructions, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        val recyclerView: RecyclerView = view.findViewById(R.id.prescriptionRecyclerView)
        val emptyInstructionsFragment: TextView = view.findViewById(R.id.empty_instructions);

        adapter = Adapter({ selectedPrescription -> handleMedicineClick(selectedPrescription) })
        { selectedPrescription -> openExpirationFragment(selectedPrescription)}

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val application = requireActivity().application as AppApplication
        val dao: Dao = application.db.Dao()

        // Retrieve medicine data from the database
        lifecycleScope.launch {

            dao.getPrescription().collect() { medicineEntities ->
                val prescription = medicineEntities.map { entity ->
                    Model(
                        medicineName = entity.medicineName,
                        details = Model.Details.fromMedicineEntity(entity),
                        isExpanded = false // Initially set to false for collapsed state
                    )
                }
                modelList.clear()
                modelList.addAll(prescription)
                if(modelList.isEmpty()){
                    recyclerView.visibility = View.GONE
                    emptyInstructionsFragment.visibility = View.VISIBLE
                }
                adapter.updateList(modelList)
            }
        }


    }


    private fun handleMedicineClick(selectedMedicine: Model) {
        selectedMedicine.isExpanded = !selectedMedicine.isExpanded
        adapter.notifyItemChanged(modelList.indexOf(selectedMedicine))
    }

    private fun openExpirationFragment(selectedMedicine: Model) {
        val expirationFragment = ExpDateFragment().apply {
            arguments = Bundle().apply {
                putString("medicineName", selectedMedicine.medicineName)
            }
        }
        (activity as? MainActivity)?.replaceFragment(expirationFragment)
    }
}