package ru.sbrf.sberwifi.vendor.model

import android.content.res.Resources
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.collections4.IterableUtils
import org.apache.commons.collections4.Predicate
import org.apache.commons.collections4.PredicateUtils
import org.apache.commons.lang3.StringUtils
import ru.sbrf.sberwifi.R
import ru.sbrf.sberwifi.util.FileUtils
import java.util.*

class VendorDB(private val resources: Resources) : VendorService {
    private val vendors: MutableMap<String, List<String>>
    private val macs: MutableMap<String, String>
    private var loaded: Boolean = false

    init {
        this.vendors = TreeMap()
        this.macs = TreeMap()
        this.loaded = false
    }

    override fun findVendorName(address: String): String {
        val result = getMacs()[VendorUtils.clean(address)]
        return result ?: StringUtils.EMPTY
    }

    override fun findMacAddresses(vendorName: String): List<String> {
        if (StringUtils.isBlank(vendorName)) {
            return ArrayList()
        }
        val locale = Locale.getDefault()
        val results = getVendors()[vendorName.toUpperCase(locale)]
        return results ?: ArrayList()
    }

    override fun findVendors(filter: String): List<String> {
        if (StringUtils.isBlank(filter)) {
            return ArrayList(getVendors().keys)
        }
        val locale = Locale.getDefault()
        val filterToUpperCase = filter.toUpperCase(locale)
        val predicates = listOf(StringContains(filterToUpperCase), MacContains(filterToUpperCase))
        return ArrayList(CollectionUtils.select(getVendors().keys, PredicateUtils.anyPredicate(predicates)))
    }

    override fun findVendors(): List<String> {
        return findVendors(StringUtils.EMPTY)
    }

    fun getVendors(): Map<String, List<String>> {
        load(resources)
        return vendors
    }

    fun getMacs(): Map<String, String> {
        load(resources)
        return macs
    }

    private fun load(resources: Resources) {
        if (!loaded) {
            loaded = true
            val lines = FileUtils.readFile(resources, R.raw.data).split("\n")
            for (data in lines) {
                if (data != null) {
                    val parts = data!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (parts.size == 2) {
                        val addresses = ArrayList<String>()
                        val name = parts[0]
                        vendors[name] = addresses
                        val length = parts[1].length
                        var i = 0
                        while (i < length) {
                            val mac = parts[1].substring(i, i + VendorUtils.MAX_SIZE)
                            addresses.add(VendorUtils.toMacAddress(mac))
                            macs[mac] = name
                            i += VendorUtils.MAX_SIZE
                        }
                    }
                }
            }
        }
    }

    private inner class StringContains constructor(private val filter: String) : Predicate<String> {

        override fun evaluate(`object`: String): Boolean {
            return `object`.contains(filter)
        }
    }

    private inner class MacContains constructor(private val filter: String) : Predicate<String> {

        override fun evaluate(`object`: String): Boolean {
            return IterableUtils.matchesAny(findMacAddresses(`object`), StringContains(filter))
        }
    }
}
