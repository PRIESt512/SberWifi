package ru.sbrf.sberwifi.wifi.model

import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.collections4.IterableUtils
import org.apache.commons.collections4.Predicate
import org.apache.commons.collections4.Transformer
import org.apache.commons.lang3.builder.EqualsBuilder
import ru.sbrf.sberwifi.MainContext
import ru.sbrf.sberwifi.vendor.model.VendorService
import java.util.*

class WiFiData(private val wiFiDetails: List<WiFiDetail>, val wiFiConnection: WiFiConnection) {

    fun getConnection(): WiFiDetail {
        val wiFiDetail = IterableUtils.find(wiFiDetails, ConnectionPredicate())
        return if (wiFiDetail == null) WiFiDetail.EMPTY else copyWiFiDetail(wiFiDetail)
    }

    @JvmOverloads
    fun getWiFiDetails(predicate: Predicate<WiFiDetail>, sortBy: SortBy, groupBy: GroupBy = GroupBy.NONE): List<WiFiDetail> {
        var results = getWiFiDetails(predicate)
        if (results.isNotEmpty() && GroupBy.NONE != groupBy) {
            results = sortAndGroup(results, sortBy, groupBy)
        }
        Collections.sort(results, sortBy.comparator())
        return results
    }

    internal fun sortAndGroup(wiFiDetails: List<WiFiDetail>, sortBy: SortBy, groupBy: GroupBy): List<WiFiDetail> {
        val results = ArrayList<WiFiDetail>()
        Collections.sort(wiFiDetails, groupBy.sortOrderComparator())
        var parent: WiFiDetail? = null
        for (wiFiDetail in wiFiDetails) {
            if (parent == null || groupBy.groupByComparator().compare(parent, wiFiDetail) !== 0) {
                if (parent != null) {
                    Collections.sort(parent.getChildren(), sortBy.comparator())
                }
                parent = wiFiDetail
                results.add(parent)
            } else {
                parent.addChild(wiFiDetail)
            }
        }
        if (parent != null) {
            Collections.sort(parent.getChildren(), sortBy.comparator())
        }
        Collections.sort(results, sortBy.comparator())
        return results
    }

    private fun getWiFiDetails(predicate: Predicate<WiFiDetail>): List<WiFiDetail> {
        val selected = CollectionUtils.select(wiFiDetails, predicate)
        val collected = CollectionUtils.collect(selected, Transform())
        return ArrayList(collected)
    }

    fun getWiFiDetails(): List<WiFiDetail> {
        return Collections.unmodifiableList(wiFiDetails)
    }

    private fun copyWiFiDetail(wiFiDetail: WiFiDetail): WiFiDetail {
        val vendorService = MainContext.INSTANCE.vendorService
        val vendorName = vendorService.findVendorName(wiFiDetail.bssid)
        val wiFiAdditional = WiFiAdditional(vendorName, wiFiConnection)
        return WiFiDetail(wiFiDetail, wiFiAdditional)
    }

    private inner class ConnectionPredicate : Predicate<WiFiDetail> {
        override fun evaluate(wiFiDetail: WiFiDetail): Boolean {
            return EqualsBuilder()
                    .append(wiFiConnection.ssid, wiFiDetail.ssid)
                    .append(wiFiConnection.bssid, wiFiDetail.bssid)
                    .isEquals
        }
    }

    private inner class Transform constructor() : Transformer<WiFiDetail, WiFiDetail> {
        private val connection: WiFiDetail
        private val vendorService: VendorService

        init {
            this.connection = getConnection()
            this.vendorService = MainContext.INSTANCE.vendorService
        }

        override fun transform(input: WiFiDetail): WiFiDetail {
            if (input == connection) {
                return connection
            }
            val vendorName = vendorService.findVendorName(input.bssid)
            val wiFiAdditional = WiFiAdditional(vendorName)
            return WiFiDetail(input, wiFiAdditional)
        }
    }

    companion object {
        val EMPTY = WiFiData(emptyList<WiFiDetail>(), WiFiConnection.EMPTY)
    }

}
