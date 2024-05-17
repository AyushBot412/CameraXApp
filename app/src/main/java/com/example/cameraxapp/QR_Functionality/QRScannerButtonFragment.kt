package com.example.cameraxapp.QR_Functionality

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.cameraxapp.databinding.FragmentQrScannerButtonBinding


class QRScannerButtonFragment : Fragment() {
    private var viewBinding: FragmentQrScannerButtonBinding? = null

    // Use interface and listener so fragment communicates action to Main.
    // When defining the interface and listener, fragment will send a callback to Main when button is clicked.
    interface QRScannerButtonListener {
        fun onQRScannerButtonClicked()
    }
    private var listener: QRScannerButtonListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): RelativeLayout? {
        viewBinding = FragmentQrScannerButtonBinding.inflate(inflater, container, false)

        viewBinding?.qrScannerBtn?.setOnClickListener{
            listener?.onQRScannerButtonClicked()
        }

        return viewBinding?.root
    }

    // The following two functions are lifecycle methods which help the fragment and the activity know when
    // they are connected and when they are not, and to communicate to each other using the interface.

    // onAttach: attaches this fragment to Main, helping the fragment to know that Main has implemented
    // the Listener so they can communicate
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is QRScannerButtonListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnQRScannerListener")
        }
    }

    // onDetach: where the fragment is no longer connected to Main, Listener becomes null.
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = QRScannerButtonFragment()
    }
}