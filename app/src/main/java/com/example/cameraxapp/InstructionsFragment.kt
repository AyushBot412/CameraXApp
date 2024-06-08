package com.example.cameraxapp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraxapp.MedicineList.Adapter
import com.example.cameraxapp.MedicineList.Model
import com.example.cameraxapp.QR_Functionality.QRCameraFragment
import com.example.cameraxapp.Room.AppApplication
import com.example.cameraxapp.Room.Dao
import kotlinx.coroutines.launch


class InstructionsFragment : Fragment() {
    private lateinit var adapter : Adapter
    private val modelList: MutableList<Model> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var deletePrescriptionButton: Button
    private lateinit var addMedicineButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instructions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.prescriptionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        deletePrescriptionButton = view.findViewById(R.id.deletePrescriptionButton)
        addMedicineButton = view.findViewById(R.id.addMedicineButton)
        val emptyInstructionsFragment: TextView = view.findViewById(R.id.empty_instructions);
        val application = requireActivity().application as AppApplication
        val dao: Dao = application.db.Dao()

        // Retrieve medicine data from the database
        lifecycleScope.launch {

            try {
                dao.getPrescription().collect { medicineEntities ->
                    val prescription = medicineEntities.map { entity ->
                        Model(
                            medicineName = entity.medicineName,
                            details = Model.Details.fromMedicineEntity(entity),
                            isExpanded = false // Initially set to false for collapsed state
                        )
                    }

                    modelList.clear()
                    modelList.addAll(prescription)

                    // Check if there are prescriptions
                    updateButtonVisibility()

                    addMedicineButton.setOnClickListener {
                        (activity as? MainActivity)?.replaceFragment(QRCameraFragment())
                    }

                    deletePrescriptionButton.setOnClickListener {
                        deletePrescription(modelList)
                    }

                    if (modelList.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        emptyInstructionsFragment.visibility = View.VISIBLE
                    }
                    else {

                        adapter = Adapter(modelList,
                            onExpirationButtonClick = { selectedMedicine ->
                                openExpirationFragment(selectedMedicine)
                            },
                            onDeleteMedicineClick = { selectedMedicine ->
                                deleteMedicine(selectedMedicine)
                            }
                        )
                        recyclerView.adapter = adapter
                    }
                }
            }  catch (e: Exception) {
                Log.e("InstructionFragment", "Error: ${e.message}", e)
            }
        }
    }

    private fun openExpirationFragment(selectedMedicine: Model) {
        val expirationFragment = ExpDateFragment().apply {
            arguments = Bundle().apply {
                putString("medicineName", selectedMedicine.medicineName)
            }
        }
        (activity as? MainActivity)?.replaceFragment(expirationFragment)
    }
    private fun deleteMedicine(selectedMedicine: Model) {
        val application = requireActivity().application as AppApplication
        val dao: Dao = application.db.Dao()

        AlertDialog.Builder(context, R.style.RedBorderAlertDialog)
            .setTitle("Warning!")
            .setMessage("Only Delete Medicine If Instructed By Healthcare Professional")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialogInterface, _ ->

                // using coroutines to ensure that any db operations are executed off the main UI thread to have smooth user experience.
                lifecycleScope.launch {
                    dao.deleteMedicine(selectedMedicine.medicineName)
                    modelList.removeAll { it.medicineName ==  selectedMedicine.medicineName }
                }
                Toast.makeText(context, "Medicine Deleted", Toast.LENGTH_LONG).show()
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                Toast.makeText(context, "Medicine Not Deleted", Toast.LENGTH_LONG).show()
                dialogInterface.dismiss()
            }.show()
    }

    private fun deletePrescription(medicines: MutableList<Model>) {
        val application = requireActivity().application as AppApplication
        val dao: Dao = application.db.Dao()

        AlertDialog.Builder(context, R.style.RedBorderAlertDialog)
            .setTitle("Warning!")
            .setMessage("Only Delete Prescription If Instructed By Healthcare Professional")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialogInterface, _ ->

                // using coroutines to ensure that any db operations are executed off the main UI thread to have smooth user experience.
                lifecycleScope.launch {
                    val medicineNamesList = medicines.map { it.medicineName }
                    dao.deletePrescription(medicineNamesList)
                    modelList.clear()
                    updateButtonVisibility()
                }
                Toast.makeText(context, "Prescription Deleted", Toast.LENGTH_LONG).show()
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                Toast.makeText(context, "Prescription Not Deleted", Toast.LENGTH_LONG).show()
                dialogInterface.dismiss()
            }.show()
    }

    private fun updateButtonVisibility() {
        if (modelList.isNotEmpty()) {
            deletePrescriptionButton.visibility = View.VISIBLE
            addMedicineButton.visibility = View.VISIBLE
        } else {
            deletePrescriptionButton.visibility = View.GONE
            addMedicineButton.visibility = View.GONE
        }
    }

}