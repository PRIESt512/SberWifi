package ru.sbrf.sberwifi

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sbrf.sberwifi.fragment.IperfFragment
import ru.sbrf.sberwifi.fragment.ReportFragment
import ru.sbrf.sberwifi.fragment.WiFiFragment
import ru.sbrf.sberwifi.livemodel.scheduler.AlarmWiFiScanService
import ru.sbrf.sberwifi.util.FileUtils
import ru.sbrf.sberwifi.wifi.model.WiFiDetail
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


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

        scheduleJob()
        setSSL()
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

    private fun setSSL() {
        try {
            val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
            val caInput = FileUtils.getInputStream(resources, R.raw.team_a)
            val ca: X509Certificate = caInput.use {
                cf.generateCertificate(it) as X509Certificate
            }

            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType).apply {
                load(null, null)
                setCertificateEntry("ca", ca)
            }
            val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
            val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
                init(keyStore)
            }
            val context: SSLContext = SSLContext.getInstance("TLS").apply {
                init(null, tmf.trustManagers, null)
            }
            MainContext.INSTANCE.initSSL(context)
            MainContext.INSTANCE.trustManager = tmf.trustManagers
        } catch (ex: Exception) {
            Log.e("SSL", ex.message)
        }
    }

    private fun scheduleJob() {
        val intent = Intent(applicationContext, AlarmWiFiScanService::class.java)
        val alarmIntent = PendingIntent.getService(applicationContext, 0, intent, 0)

        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 5)
        val afterFiveSecond = calendar.timeInMillis

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, afterFiveSecond, 60000, alarmIntent)
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