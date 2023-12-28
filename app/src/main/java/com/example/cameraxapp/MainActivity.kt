package com.example.cameraxapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cameraxapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        // define fragments
        val bottleScannerButtonFragment : Fragment = BottleScannerButtonFragment()
        //val QRScannerFragment : Fragment = QRScannerFragment()
        //val instructionsFragment : Fragment = InstructionsFragment()
        val aboutUsFragment : Fragment = AboutUsFragment()

        // handle navigation selection
        viewBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.navigation_bottle_scanner -> fragment = bottleScannerButtonFragment
               // R.id.navigation_qr ->fragment= QRScannerFragment
               // R.id.navigation_instructions -> fragment= instructionsFragment
                R.id.navigation_about_us -> fragment = aboutUsFragment
            }
            replaceFragment(fragment)
            true
        }

        // set default selection
        viewBinding.bottomNavigationView.selectedItemId = R.id.navigation_bottle_scanner
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}

