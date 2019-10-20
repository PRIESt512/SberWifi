package ru.sbrf.sberwifi.wifi.model

import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.collections4.Predicate
import org.apache.commons.collections4.Transformer
import org.apache.commons.lang3.builder.CompareToBuilder
import ru.sbrf.sberwifi.wifi.band.WiFiChannel
import java.util.*
import kotlin.math.max

class ChannelRating {

    private var wiFiDetails: List<WiFiDetail>

    init {
        wiFiDetails = emptyList()
    }

    fun getCount(wiFiChannel: WiFiChannel): Int {
        return collectOverlapping(wiFiChannel).size
    }

    fun getStrength(wiFiChannel: WiFiChannel): Strength {
        var strength = Strength.ZERO
        for (wiFiDetail in collectOverlapping(wiFiChannel)) {
            if (!wiFiDetail.wiFiAdditional.wiFiConnection.isConnected) {
                strength = Strength.values()[max(strength.getNumberOfStrength() - 1, wiFiDetail.wiFiSignal.strength.getNumberOfStrength() - 1)]
            }
        }
        return strength
    }

    private fun removeGuest(wiFiDetails: List<WiFiDetail>): List<WiFiDetail> {
        val results = ArrayList<WiFiDetail>()
        var wiFiDetail = WiFiDetail.EMPTY
        Collections.sort(wiFiDetails, GuestSort())
        for (current in wiFiDetails) {
            if (isGuest(current, wiFiDetail)) {
                continue
            }
            results.add(current)
            wiFiDetail = current
        }
        Collections.sort(results, SortBy.STRENGTH.comparator())
        return results
    }

    internal fun getWiFiDetails(): List<WiFiDetail> {
        return wiFiDetails
    }

    fun setWiFiDetails(wiFiDetails: List<WiFiDetail>) {
        this.wiFiDetails = removeGuest(ArrayList(wiFiDetails))
    }

    private fun isGuest(lhs: WiFiDetail, rhs: WiFiDetail): Boolean {
        if (!isGuestBSSID(lhs.bssid, rhs.bssid)) {
            return false
        }
        var result = lhs.wiFiSignal.primaryFrequency - rhs.wiFiSignal.primaryFrequency
        if (result == 0) {
            result = rhs.wiFiSignal.level - lhs.wiFiSignal.level
            if (result > LEVEL_RANGE_MIN || result < LEVEL_RANGE_MAX) {
                result = 0
            }
        }
        return result == 0
    }

    private fun isGuestBSSID(lhs: String, rhs: String): Boolean {
        return lhs.length == BSSID_LENGTH &&
                lhs.length == rhs.length &&
                lhs.substring(0, 0).equals(rhs.substring(0, 0), ignoreCase = true) &&
                lhs.substring(2, BSSID_LENGTH - 1).equals(rhs.substring(2, BSSID_LENGTH - 1), ignoreCase = true)
    }

    private fun collectOverlapping(wiFiChannel: WiFiChannel): List<WiFiDetail> {
        return ArrayList(CollectionUtils.select(wiFiDetails, InRangePredicate(wiFiChannel)))
    }

    fun getBestChannels(wiFiChannels: List<WiFiChannel>): List<ChannelAPCount> {
        val results = ArrayList(
                CollectionUtils.collect(
                        CollectionUtils.select(wiFiChannels, BestChannelPredicate()), ToChannelAPCount()))
        Collections.sort(results, ChannelAPCountSort())
        return results
    }

    private inner class ChannelAPCountSort : Comparator<ChannelAPCount> {
        override fun compare(lhs: ChannelAPCount, rhs: ChannelAPCount): Int {
            return CompareToBuilder()
                    .append(lhs.count, rhs.count)
                    .append(lhs.wiFiChannel, rhs.wiFiChannel)
                    .toComparison()
        }
    }

    private inner class GuestSort : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            val locale = Locale.getDefault()
            return CompareToBuilder()
                    .append(lhs.bssid.toUpperCase(locale), rhs.bssid.toUpperCase(locale))
                    .append(lhs.wiFiSignal.primaryFrequency, rhs.wiFiSignal.primaryFrequency)
                    .append(rhs.wiFiSignal.level, lhs.wiFiSignal.level)
                    .append(lhs.bssid.toUpperCase(locale), rhs.bssid.toUpperCase(locale))
                    .toComparison()
        }
    }

    private inner class BestChannelPredicate : Predicate<WiFiChannel> {
        override fun evaluate(`object`: WiFiChannel): Boolean {
            val strength = getStrength(`object`)
            return Strength.ZERO == strength || Strength.ONE == strength
        }
    }

    private inner class ToChannelAPCount : Transformer<WiFiChannel, ChannelAPCount> {
        override fun transform(input: WiFiChannel): ChannelAPCount {
            return ChannelAPCount(input, getCount(input))
        }
    }

    private inner class InRangePredicate constructor(private val wiFiChannel: WiFiChannel) : Predicate<WiFiDetail> {

        override fun evaluate(wiFiDetail: WiFiDetail): Boolean {
            return wiFiDetail.wiFiSignal.isInRange(wiFiChannel.frequency)
        }
    }

    companion object {
        internal val LEVEL_RANGE_MIN = -5
        private val LEVEL_RANGE_MAX = 5
        private val BSSID_LENGTH = 17
    }
}
