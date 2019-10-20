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

    private lateinit var currentFragment: Fragment

    private lateinit var currentFragmentEnum: FragmentEnum

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
        currentFragmentEnum = FragmentEnum.WiFiFragment
        transaction.replace(R.id.frame_layout, currentFragment!!)
        transaction.commit()
    }

    private fun bottomNavigationInit() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener {

            val selectedFragment: Fragment? = when (it.itemId) {
                FragmentEnum.WiFiFragment.idFragment -> {
                    val newFragment = WiFiFragment.newInstance()
                    replaceFragment(newFragment, FragmentEnum.WiFiFragment)
                    newFragment
                }
                FragmentEnum.ReportFragment.idFragment -> {
                    val newFragment = ReportFragment.newInstance()
                    replaceFragment(newFragment, FragmentEnum.ReportFragment)
                    newFragment
                }
                FragmentEnum.IperfFragment.idFragment -> {
                    val newFragment = IperfFragment.newInstance()
                    replaceFragment(newFragment, FragmentEnum.IperfFragment)
                    newFragment
                }
                else -> throw UnsupportedOperationException("Невозможно выбрать фрагмент для отображения")
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, selectedFragment!!)
            transaction.commit()
            true
        }
    }

    private fun replaceFragment(newFragment: Fragment, newFragmentEnum: FragmentEnum) {
        FragmentStateHelper.INSTANCE?.saveState(currentFragmentEnum, currentFragment)
        fragmentStateHelper.restoreState(newFragmentEnum, newFragment)
        onActivityAttachFragment(newFragment)
        currentFragmentEnum = newFragmentEnum
        currentFragment = newFragment
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