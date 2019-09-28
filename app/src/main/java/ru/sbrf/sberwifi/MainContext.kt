package ru.sbrf.sberwifi

import ru.sbrf.sberwifi.vendor.model.VendorService
import ru.sbrf.sberwifi.vendor.model.VendorServiceFactory

enum class MainContext {
    INSTANCE;

    private lateinit var mainActivity: MainActivity

    lateinit var vendorService: VendorService

    fun initialize(mainActivity: MainActivity) {
        val applicationContext = mainActivity.applicationContext
        // val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        this.mainActivity = mainActivity
        this.vendorService = VendorServiceFactory.makeVendorService(mainActivity.resources)
    }
}