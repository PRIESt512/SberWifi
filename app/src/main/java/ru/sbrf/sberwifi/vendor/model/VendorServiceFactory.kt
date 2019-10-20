package ru.sbrf.sberwifi.vendor.model

import android.content.res.Resources

class VendorServiceFactory private constructor() {
    init {
        throw IllegalStateException("Factory class")
    }

    companion object {
        fun makeVendorService(resources: Resources): VendorService {
            return VendorDB(resources)
        }
    }
}