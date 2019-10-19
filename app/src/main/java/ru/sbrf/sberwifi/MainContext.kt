package ru.sbrf.sberwifi

import android.view.LayoutInflater
import ru.sbrf.sberwifi.vendor.model.VendorService
import ru.sbrf.sberwifi.vendor.model.VendorServiceFactory
import ru.sbrf.sberwifi.wifi.model.WiFiData

enum class MainContext {
    INSTANCE;

    private lateinit var mainActivity: MainActivity

    public lateinit var vendorService: VendorService

    @Volatile
    public lateinit var wiFiData: WiFiData

    fun initialize(mainActivity: MainActivity) {
        //val applicationContext = mainActivity.applicationContext
        // val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        this.mainActivity = mainActivity
        this.vendorService = VendorServiceFactory.makeVendorService(mainActivity.resources)
    }

    fun getLayoutInflater(): LayoutInflater {
        return mainActivity.layoutInflater
    }
}