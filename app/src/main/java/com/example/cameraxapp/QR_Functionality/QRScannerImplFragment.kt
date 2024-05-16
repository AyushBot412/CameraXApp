package com.example.cameraxapp.QR_Functionality

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.cameraxapp.InstructionsFragment
import com.example.cameraxapp.R
import com.example.cameraxapp.Room.AppApplication
import com.example.cameraxapp.Room.PrescriptionDao
import com.example.cameraxapp.Room.PrescriptionEntity
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray


class QRScannerImplFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_qr_scanner_impl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()

        scanCode(activity, scannerView)

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun scanCode(
        activity: FragmentActivity,
        scannerView: CodeScannerView
    ) {
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            formats = CodeScanner.TWO_DIMENSIONAL_FORMATS // list of type BarcodeFormat
            autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            isAutoFocusEnabled = true // Whether to enable auto focus or not
            isFlashEnabled = false // Whether to enable flash or not

            decodeCallback = DecodeCallback {
                activity.runOnUiThread {
                    insertQRInformationToDatabase(it)
                }
            }

            errorCallback = ErrorCallback {
                activity.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error Occurred: Please Try Again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun insertQRInformationToDatabase(it: Result) {
        val contents = it.text
        if (contents != null) {
            try {
                // Parse text content
                val jsonContentFromQrCode = contents

                // Converting to JSON and getting Prescriptions array
                Log.d("QR_CODE_CONTENT", jsonContentFromQrCode)

                val prescriptionsArray = JSONArray(jsonContentFromQrCode)

                // Getting access to be able to perform db operations
                val prescriptionDao =
                    (requireActivity().application as AppApplication).db.prescriptionDao()

                val prescriptionEntities =
                    (0 until prescriptionsArray.length()).map { i ->
                        val prescriptionObject = prescriptionsArray.getJSONObject(i)
                        val eyeSelectionObject = prescriptionObject.getJSONObject("eyeSelection")
                        val leftEyeSelected = eyeSelectionObject.getBoolean("left")
                        val rightEyeSelected = eyeSelectionObject.getBoolean("right")
                        val bothEyesSelected = eyeSelectionObject.getBoolean("both")

                        PrescriptionEntity(
                            medicineName = prescriptionObject.getString("medicineName"),
                            leftEyeSelected = leftEyeSelected,
                            rightEyeSelected = rightEyeSelected,
                            bothEyesSelected = bothEyesSelected,
                            frequency = prescriptionObject.getString("frequency").ifEmpty { "N/A" },
                            specialInstruction = prescriptionObject.getString("specialInstruction")
                                .ifEmpty { "N/A" },
                            expirationDate = "N/A"
                        )
                    }

                // Dialog Box
                initiateDialog(prescriptionDao, prescriptionEntities)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Invalid JSON format", Toast.LENGTH_LONG).show()
                Log.e("QR_CODE_ERROR", e.message.toString())

            }
        }
        else {
            Toast.makeText(context, "QR contents are null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initiateDialog(
        prescriptionDao: PrescriptionDao,
        prescriptionEntities: List<PrescriptionEntity>
    ) {
        AlertDialog.Builder(context)
            .setTitle("Download Instructions?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialogInterface, _ ->

                // using coroutines to ensure that any db operations are executed off the main UI thread to have smooth user experience.
                lifecycleScope.launch(Dispatchers.IO) {

                    // inserting all prescriptions here
                    prescriptionDao.insertAll(prescriptionEntities)
                }
                Toast.makeText(context, "Instructions Uploaded", Toast.LENGTH_LONG).show()
                dialogInterface.dismiss()
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.frame_layout,
                        InstructionsFragment.newInstance()
                    )
                    .addToBackStack("InstructionFragment")
                    .commit()

            }
            .setNegativeButton("No") { dialogInterface, _ ->
                Toast.makeText(context, "Instructions not Uploaded", Toast.LENGTH_LONG).show()
                dialogInterface.dismiss() // nothing is done
            }.show()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            QRScannerImplFragment().apply {
            }
    }
}