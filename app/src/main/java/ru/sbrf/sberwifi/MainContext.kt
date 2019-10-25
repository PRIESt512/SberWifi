package ru.sbrf.sberwifi

import android.view.LayoutInflater
import ru.sbrf.sberwifi.vendor.model.VendorService
import ru.sbrf.sberwifi.vendor.model.VendorServiceFactory
import ru.sbrf.sberwifi.wifi.model.WiFiData
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

enum class MainContext {
    INSTANCE;

    private lateinit var mainActivity: MainActivity

    lateinit var vendorService: VendorService

    lateinit var sslContext: SSLContext

    lateinit var trustManager: Array<TrustManager>

    @Volatile
    lateinit var wiFiData: WiFiData

    fun initialize(mainActivity: MainActivity) {
        this.mainActivity = mainActivity
        this.vendorService = VendorServiceFactory.makeVendorService(mainActivity.resources)
    }

    fun getLayoutInflater(): LayoutInflater {
        return mainActivity.layoutInflater
    }

    fun initSSL(ssl: SSLContext) {
        sslContext = ssl
    }

    fun initTrust(trust: Array<TrustManager>) {
        trustManager = trust
    }
}