package ru.sbrf.sberwifi.wifi.model

import org.apache.commons.lang3.builder.CompareToBuilder
import java.util.*

enum class GroupBy private constructor(private val sortOrderComparator: Comparator<WiFiDetail>, private val groupByComparator: Comparator<WiFiDetail>) {
    NONE(None(), None()),
    SSID(SSIDSortOrder(), SSIDGroupBy()),
    CHANNEL(ChannelSortOrder(), ChannelGroupBy());

    internal fun sortOrderComparator(): Comparator<WiFiDetail> {
        return sortOrderComparator
    }

    internal fun groupByComparator(): Comparator<WiFiDetail> {
        return groupByComparator
    }

    internal class None : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            return if (lhs.equals(rhs)) 0 else 1
        }
    }

    internal class SSIDSortOrder : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            val locale = Locale.getDefault()
            return CompareToBuilder()
                    .append(lhs.ssid.toUpperCase(locale), rhs.ssid.toUpperCase(locale))
                    .append(rhs.wiFiSignal.level, lhs.wiFiSignal.level)
                    .append(lhs.bssid.toUpperCase(locale), rhs.bssid.toUpperCase(locale))
                    .toComparison()
        }
    }

    internal class SSIDGroupBy : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            val locale = Locale.getDefault()
            return CompareToBuilder()
                    .append(lhs.ssid.toUpperCase(locale), rhs.ssid.toUpperCase(locale))
                    .toComparison()
        }
    }

    internal class ChannelSortOrder : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            val locale = Locale.getDefault()
            return CompareToBuilder()
                    .append(lhs.wiFiSignal.primaryWiFiChannel.channel, rhs.wiFiSignal.primaryWiFiChannel.channel)
                    .append(rhs.wiFiSignal.level, lhs.wiFiSignal.level)
                    .append(lhs.ssid.toUpperCase(locale), rhs.ssid.toUpperCase(locale))
                    .append(lhs.bssid.toUpperCase(locale), rhs.bssid.toUpperCase(locale))
                    .toComparison()
        }
    }

    internal class ChannelGroupBy : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            return CompareToBuilder()
                    .append(lhs.wiFiSignal.primaryWiFiChannel.channel, rhs.wiFiSignal.primaryWiFiChannel.channel)
                    .toComparison()
        }
    }

}
