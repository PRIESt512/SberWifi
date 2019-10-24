package ru.sbrf.sberwifi.wifi.model

import org.apache.commons.lang3.builder.CompareToBuilder
import java.util.*

enum class SortBy private constructor(private val comparator: Comparator<WiFiDetail>) {
    STRENGTH(StrengthComparator()),
    SSID(SSIDComparator()),
    CHANNEL(ChannelComparator());

    internal fun comparator(): Comparator<WiFiDetail> {
        return comparator
    }

    internal class StrengthComparator : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            val locale = Locale.getDefault()
            return CompareToBuilder()
                    .append(rhs.wiFiSignal.level, lhs.wiFiSignal.level)
                    .append(lhs.SSID.toUpperCase(locale), rhs.SSID.toUpperCase(locale))
                    .append(lhs.bssid.toUpperCase(locale), rhs.bssid.toUpperCase(locale))
                    .toComparison()
        }
    }


    internal class SSIDComparator : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            val locale = Locale.getDefault()
            return CompareToBuilder()
                    .append(lhs.SSID.toUpperCase(locale), rhs.SSID.toUpperCase(locale))
                    .append(rhs.wiFiSignal.level, lhs.wiFiSignal.level)
                    .append(lhs.bssid.toUpperCase(locale), rhs.bssid.toUpperCase(locale))
                    .toComparison()
        }
    }

    internal class ChannelComparator : Comparator<WiFiDetail> {
        override fun compare(lhs: WiFiDetail, rhs: WiFiDetail): Int {
            val locale = Locale.getDefault()
            return CompareToBuilder()
                    .append(lhs.wiFiSignal.primaryWiFiChannel.channel, rhs.wiFiSignal.primaryWiFiChannel.channel)
                    .append(rhs.wiFiSignal.level, lhs.wiFiSignal.level)
                    .append(lhs.SSID.toUpperCase(locale), rhs.SSID.toUpperCase(locale))
                    .append(lhs.bssid.toUpperCase(locale), rhs.bssid.toUpperCase(locale))
                    .toComparison()
        }
    }

}
