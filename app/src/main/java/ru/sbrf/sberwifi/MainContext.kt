package ru.sbrf.sberwifi

import android.view.LayoutInflater
import ru.sbrf.sberwifi.vendor.model.VendorService
import ru.sbrf.sberwifi.vendor.model.VendorServiceFactory
import ru.sbrf.sberwifi.wifi.model.WiFiData

enum class MainContext {
    INSTANCE;

    private lateinit var mainActivity: MainActivity

    lateinit var vendorService: VendorService

    @Volatile
    lateinit var wiFiData: WiFiData

    fun initialize(mainActivity: MainActivity) {
        this.mainActivity = mainActivity
        this.vendorService = VendorServiceFactory.makeVendorService(mainActivity.resources)
    }

    fun getLayoutInflater(): LayoutInflater {
        return mainActivity.layoutInflater
    }
}