package ru.sbrf.sberwifi.wifi.band

import androidx.core.util.Pair

class WiFiChannelsGHZ2 : WiFiChannels(RANGE, SETS) {

    override fun getWiFiChannelPairs(): List<Pair<WiFiChannel, WiFiChannel>> {
        return listOf(SET)
    }

    override fun getWiFiChannelPairFirst(countryCode: String): Pair<WiFiChannel, WiFiChannel> {
        return SET
    }

    override fun getAvailableChannels(countryCode: String): List<WiFiChannel> {
        return getAvailableChannels(WiFiChannelCountry[countryCode].channelsGHZ2)
    }

    override fun isChannelAvailable(countryCode: String, channel: Int): Boolean {
        return WiFiChannelCountry[countryCode].isChannelAvailableGHZ2(channel)
    }

    override fun getWiFiChannelByFrequency(frequency: Int, wiFiChannelPair: Pair<WiFiChannel, WiFiChannel>): WiFiChannel {
        return if (isInRange(frequency)) getWiFiChannel(frequency, SET) else WiFiChannel.UNKNOWN
    }

    companion object {

        @JvmStatic
        private val RANGE = Pair(2400, 2499)

        @JvmStatic
        private val SETS = listOf(
                Pair(WiFiChannel(1, 2412), WiFiChannel(13, 2472)),
                Pair(WiFiChannel(14, 2484), WiFiChannel(14, 2484)))

        @JvmStatic
        private val SET = Pair(SETS[0].first, SETS[SETS.size - 1].second)
    }
}