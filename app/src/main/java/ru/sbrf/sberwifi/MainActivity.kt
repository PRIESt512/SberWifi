package ru.sbrf.sberwifi

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sbrf.sberwifi.fragment.IperfFragment
import ru.sbrf.sberwifi.fragment.ReportFragment
import ru.sbrf.sberwifi.fragment.WiFiFragment
import ru.sbrf.sberwifi.wifi.model.WiFiDetail


@Suppress("CAST_NEVER_SUCCEEDS")
class MainActivity : AppCompatActivity(),
        WiFiFragment.OnWifiInteractionListener,
        ReportFragment.OnReportInteractionListener {

    private var wiFiDetail: WiFiDetail? = null

    private var currentSelectItemId = R.id.navigation_wifi

    private var currentFragment: Fragment? = null

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFragmentInteraction(wiFiDetail: WiFiDetail) {
        this.wiFiDetail = wiFiDetail
    }

    private lateinit var fragmentStateHelper: FragmentStateHelper

    init {
        FragmentStateHelper.getInstance(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        fragmentStateHelper = FragmentStateHelper.INSTANCE!!

        bottomNavigationInit()
        val mainContext = MainContext.INSTANCE
        mainContext.initialize(this)

        //по умолчанию отображаем фрагмент WiFi
        val transaction = supportFragmentManager.beginTransaction()
        currentFragment = WiFiFragment.newInstance()
        transaction.replace(R.id.frame_layout, currentFragment!!)
        transaction.commit()
    }

    private fun bottomNavigationInit() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener {

            val selectedFragment: Fragment? = when (it.itemId) {
                R.id.navigation_wifi -> {
                    if (currentFragment != null)
                        FragmentStateHelper.INSTANCE?.saveState(currentFragment!!, currentSelectItemId.toString())
                    currentFragment = WiFiFragment.newInstance()
                    fragmentStateHelper.restoreState(currentFragment!!, it.itemId.toString())
                    onActivityAttachFragment(currentFragment!!)
                    currentSelectItemId = it.itemId
                    currentFragment!!
                }
                R.id.navigation_report -> {
                    if (currentFragment != null)
                        FragmentStateHelper.INSTANCE?.saveState(currentFragment!!, currentSelectItemId.toString())
                    currentFragment = ReportFragment.newInstance()
                    fragmentStateHelper.restoreState(currentFragment!!, it.itemId.toString())
                    onActivityAttachFragment(currentFragment!!)
                    currentSelectItemId = it.itemId
                    currentFragment!!
                }
                R.id.navigation_iperf -> {
                    if (currentFragment != null)
                        FragmentStateHelper.INSTANCE?.saveState(currentFragment!!, currentSelectItemId.toString())
                    currentFragment = IperfFragment.newInstance()
                    fragmentStateHelper.restoreState(currentFragment!!, it.itemId.toString())
                    onActivityAttachFragment(currentFragment!!)
                    currentSelectItemId = it.itemId
                    currentFragment!!
                }
                else -> throw UnsupportedOperationException("Невозможно выбрать фрагмент для отображения")
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, selectedFragment!!)
            transaction.commit()
            true
        }
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (currentFragment != null)
            FragmentStateHelper.INSTANCE?.saveState(supportFragmentManager.findFragmentById(currentSelectItemId)!!, currentSelectItemId.toString())

    }*/

    private fun onActivityAttachFragment(fragment: Fragment) {
        when (fragment) {
            is WiFiFragment -> fragment.setOnWiFiListener(this)
            is ReportFragment -> fragment.setOnReportListener(this)
        }
    }
}