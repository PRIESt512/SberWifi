package ru.sbrf.sberwifi.vendor.model

interface VendorService {
    fun findVendorName(macAddress: String): String

    fun findMacAddresses(vendorName: String): List<String>

    fun findVendors(): List<String>

    fun findVendors(filter: String): List<String>
}