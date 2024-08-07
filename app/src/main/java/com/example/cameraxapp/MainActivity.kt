package com.example.cameraxapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cameraxapp.QR_Functionality.QRScannerButtonFragment
import com.example.cameraxapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)


        val bottleScannerButtonFragment : Fragment = BottleScannerButtonFragment()
        val aboutUsFragment : Fragment = AboutUsFragment()
//      val expDateButtonFragment : Fragment = ExpDateButtonFragment()
        val QRScannerButtonFragment : Fragment = QRScannerButtonFragment()
        val instructionsFragment : Fragment = InstructionsFragment()

        // handle navigation selection
        viewBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.navigation_bottle_scanner -> fragment = bottleScannerButtonFragment
                R.id.navigation_about_us -> fragment = aboutUsFragment
                R.id.navigation_qr ->fragment= QRScannerButtonFragment
                R.id.navigation_instructions -> fragment= instructionsFragment
//              R.id.navigation_exp_date -> fragment= expDateButtonFragment
            }
            replaceFragment(fragment)
            true
        }
        // set default selection
        viewBinding.bottomNavigationView.selectedItemId = R.id.navigation_bottle_scanner
    }

    fun onQRContentDownloaded() {
        supportFragmentManager.popBackStack() // Pop the QRCodeScanningFragment off the back stack
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, InstructionsFragment())
            .commit()

        viewBinding.bottomNavigationView.selectedItemId = R.id.navigation_instructions
    }
    fun replaceFragment(fragment: Fragment){
      supportFragmentManager
          .beginTransaction()
          .replace(R.id.frame_layout, fragment)
          .commit()
    }

}
