package ru.sbrf.sberwifi

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sbrf.sberwifi.fragment.ReportFragment
import ru.sbrf.sberwifi.fragment.WiFiFragment
import ru.sbrf.sberwifi.livemodel.DetectorViewModel
import ru.sbrf.sberwifi.wifi.model.WiFiData

@Suppress("CAST_NEVER_SUCCEEDS")
class MainActivity : AppCompatActivity(),
        WiFiFragment.OnWifiInteractionListener,
        ReportFragment.OnReportInteractionListener {

    private lateinit var viewModel: DetectorViewModel

    private lateinit var dataScan: WiFiData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeUpdateScanData()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            val selectedFragment: Fragment? = when (it.itemId) {
                R.id.navigation_menu -> {
                    val fragment = WiFiFragment.newInstance()
                    onActivityAttachFragment(fragment)
                    fragment
                }
                R.id.navigation_report -> {
                    val fragment = ReportFragment.newInstance(dataScan as ArrayList<ResultWiFi>)
                    onActivityAttachFragment(fragment)
                    fragment
                }
                //R.id.navigation_settings -> selectedFragment = SettingsFragment.newInstance()
                else -> throw UnsupportedOperationException("Невозможно выбрать фрагмент для отображения")
            }

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, selectedFragment!!)
            transaction.commit()
            true
        }

        //по умолчанию отображаем фрагмент WiFi
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, WiFiFragment.newInstance())
        transaction.commit()
    }

    private fun subscribeUpdateScanData() {
        viewModel = ViewModelProviders.of(this).get(DetectorViewModel::class.java)

        viewModel.getResultScanLiveData().observe(this, Observer {
            dataScan = it
        })
    }

    private fun onActivityAttachFragment(fragment: Fragment) {
        when (fragment) {
            is WiFiFragment -> fragment.setOnWiFiListener(this)
            is ReportFragment -> fragment.setOnReportListener(this)
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
    }
}