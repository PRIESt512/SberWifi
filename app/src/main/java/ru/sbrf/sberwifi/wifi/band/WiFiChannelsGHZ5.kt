package ru.sbrf.sberwifi.wifi.band

import androidx.core.util.Pair
import org.apache.commons.collections4.IterableUtils
import org.apache.commons.collections4.Predicate
import org.apache.commons.lang3.StringUtils

class WiFiChannelsGHZ5 internal constructor() : WiFiChannels(RANGE, SETS) {

    override fun getWiFiChannelPairFirst(countryCode: String): Pair<WiFiChannel, WiFiChannel> {
        var found: Pair<WiFiChannel, WiFiChannel>? = null
        if (StringUtils.isNotBlank(countryCode)) {
            found = IterableUtils.find(getWiFiChannelPairs(), WiFiChannelPredicate(countryCode))
        }
        return found ?: SET1
    }

    override fun getWiFiChannelPairs(): List<Pair<WiFiChannel, WiFiChannel>> {
        return ArrayList(SETS)
    }

    override fun getAvailableChannels(countryCode: String): List<WiFiChannel> {
        return getAvailableChannels(WiFiChannelCountry[countryCode].channelsGHZ5)
    }

    override fun isChannelAvailable(countryCode: String, channel: Int): Boolean {
        return WiFiChannelCountry[countryCode].isChannelAvailableGHZ5(channel)
    }

    override fun getWiFiChannelByFrequency(frequency: Int, wiFiChannelPair: Pair<WiFiChannel, WiFiChannel>): WiFiChannel {
        return if (isInRange(frequency)) getWiFiChannel(frequency, wiFiChannelPair) else WiFiChannel.UNKNOWN
    }

    private inner class WiFiChannelPredicate constructor(private val countryCode: String) : Predicate<Pair<WiFiChannel, WiFiChannel>> {

        override fun evaluate(wiFiChannelPair: Pair<WiFiChannel, WiFiChannel>): Boolean {
            return isChannelAvailable(countryCode, wiFiChannelPair.first!!.channel)
        }
    }

    companion object {
        val SET1 = Pair(WiFiChannel(36, 5180), WiFiChannel(64, 5320))
        val SET2 = Pair(WiFiChannel(100, 5500), WiFiChannel(144, 5720))
        val SET3 = Pair(WiFiChannel(149, 5745), WiFiChannel(165, 5825))
        val SETS = listOf(SET1, SET2, SET3)
        private val RANGE = Pair(4900, 5899)
    }
}