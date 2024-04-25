package com.example.cameraxapp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cameraxapp.Room.PrescriptionEntity
import com.example.cameraxapp.databinding.FragmentQrScannerBinding
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import org.json.JSONObject
import com.example.cameraxapp.Room.AppApplication
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class QRScannerFragment : Fragment() {
    private var viewBinding: FragmentQrScannerBinding? = null
    private lateinit var barLauncher: ActivityResultLauncher<ScanOptions>
    private val prescriptions = mutableListOf<PrescriptionEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentQrScannerBinding.inflate(inflater, container, false)


        // registering barLauncher in lifecycle to prevent errors
        barLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult? ->
            if (result?.contents != null) {

                val jsonContentFromQrCode = result.contents
                // Log parsed instructions
                Log.d("JSON", jsonContentFromQrCode)

                try {

                    // Dialog Box
                    val builder = AlertDialog.Builder(context)
                        .setTitle("Download Instructions?")
                        //.setMessage(prettyJson)
                        .setCancelable(true)
                        .setPositiveButton("Yes") { dialogInterface, _ ->
                            lifecycleScope.launch(IO) {
                                // Parse text content as JSON
                                val jsonObject = JSONObject(jsonContentFromQrCode)
                                val prescriptionsArray = jsonObject.getJSONArray("prescriptions")
                                val prescriptionDao = (requireActivity().application as AppApplication).db.prescriptionDao()

                                val prescriptionEntities = (0 until prescriptionsArray.length()).map { i ->
                                    val prescriptionObject = prescriptionsArray.getJSONObject(i)
                                    PrescriptionEntity(
                                        name = prescriptionObject.getString("name"),
                                        eye = prescriptionObject.getString("eye"),
                                        frequency = prescriptionObject.getString("frequency"),
                                        specialInstructions = prescriptionObject.getString("specialInstructions"),
                                        expirationDate = prescriptionObject.getString("expiryDate")
                                    )
                                }

                                prescriptionDao.insertAll(prescriptionEntities)
                            }
                            Toast.makeText(context, "Instructions Uploaded", Toast.LENGTH_LONG).show()
                            dialogInterface.dismiss()

                        }
                        .setNegativeButton("No"){dialogInterface, _ ->
                            Toast.makeText(context, "Instructions not Uploaded", Toast.LENGTH_LONG).show()
                            dialogInterface.dismiss() // nothing is done
                        }.show()
                }
                catch (e:JsonSyntaxException){
                    Toast.makeText(requireContext(), "Invalid JSON format", Toast.LENGTH_LONG).show()

                }
            }
        }

        viewBinding!!.qrScannerBtn.setOnClickListener{
            scanCode()
        }

        return viewBinding!!.root
    }

    // QRCode functionality
    private fun scanCode() {

        val options = ScanOptions()
        options.setPrompt("Volume up to flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.captureActivity = CaptureAct::class.java
        barLauncher.launch(options)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
         QRScannerFragment().apply {

         }
    }

}