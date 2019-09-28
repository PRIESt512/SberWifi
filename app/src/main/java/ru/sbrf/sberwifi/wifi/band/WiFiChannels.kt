package ru.sbrf.sberwifi.wifi.band

import androidx.core.util.Pair
import org.apache.commons.collections4.*
import java.util.*
import kotlin.collections.ArrayList

abstract class WiFiChannels
internal constructor(
        private val wiFiRange: Pair<Int, Int>,
        private val wiFiChannelPairs: List<Pair<WiFiChannel, WiFiChannel>>
) {

    val wiFiChannelFirst: WiFiChannel?
        get() = wiFiChannelPairs[0].first

    val wiFiChannelLast: WiFiChannel?
        get() = wiFiChannelPairs[wiFiChannelPairs.size - 1].second

    val wiFiChannels: List<WiFiChannel>
        get() {
            val results = ArrayList<WiFiChannel>()
            IterableUtils.forEach(wiFiChannelPairs, WiFiChannelClosure(results))
            return results
        }

    fun isInRange(frequency: Int): Boolean {
        return frequency >= wiFiRange.first!! && frequency <= wiFiRange.second!!
    }

    fun getWiFiChannelByFrequency(frequency: Int): WiFiChannel {
        var found: Pair<WiFiChannel, WiFiChannel>? = null
        if (isInRange(frequency)) {
            found = IterableUtils.find(wiFiChannelPairs, FrequencyPredicate(frequency))
        }
        return if (found == null) WiFiChannel.UNKNOWN else getWiFiChannel(frequency, found)
    }

    internal fun getWiFiChannelByChannel(channel: Int): WiFiChannel {
        val found = IterableUtils.find(wiFiChannelPairs, ChannelPredicate(channel))
        return if (found == null)
            WiFiChannel.UNKNOWN
        else
            WiFiChannel(channel, found.first!!.frequency + (channel - found.first!!.channel) * FREQUENCY_SPREAD)
    }

    internal fun getWiFiChannel(frequency: Int, wiFiChannelPair: Pair<WiFiChannel, WiFiChannel>): WiFiChannel {
        val first = wiFiChannelPair.first
        val last = wiFiChannelPair.second
        val channel = ((frequency - first!!.frequency).toDouble() / FREQUENCY_SPREAD + first.channel + 0.5).toInt()
        return if (channel >= first.channel && channel <= last!!.channel) {
            WiFiChannel(channel, frequency)
        } else WiFiChannel.UNKNOWN
    }

    abstract fun getAvailableChannels(countryCode: String): List<WiFiChannel>

    abstract fun isChannelAvailable(countryCode: String, channel: Int): Boolean

    abstract fun getWiFiChannelPairs(): List<Pair<WiFiChannel, WiFiChannel>>

    abstract fun getWiFiChannelPairFirst(countryCode: String): Pair<WiFiChannel, WiFiChannel>

    abstract fun getWiFiChannelByFrequency(frequency: Int, wiFiChannelPair: Pair<WiFiChannel, WiFiChannel>): WiFiChannel

    internal fun getAvailableChannels(channels: SortedSet<Int>): List<WiFiChannel> {
        return ArrayList(CollectionUtils.collect(channels, ToWiFiChannel(this)))
    }

    private class ToWiFiChannel(private val wiFiChannels: WiFiChannels) : Transformer<Int, WiFiChannel> {

        override fun transform(input: Int?): WiFiChannel {
            return wiFiChannels.getWiFiChannelByChannel(input!!)
        }
    }

    private inner class FrequencyPredicate constructor(private val frequency: Int) : Predicate<Pair<WiFiChannel, WiFiChannel>> {

        override fun evaluate(wiFiChannelPair: Pair<WiFiChannel, WiFiChannel>): Boolean {
            return WiFiChannel.UNKNOWN != getWiFiChannel(frequency, wiFiChannelPair)
        }
    }

    private inner class ChannelPredicate constructor(private val channel: Int) : Predicate<Pair<WiFiChannel, WiFiChannel>> {

        override fun evaluate(wiFiChannelPair: Pair<WiFiChannel, WiFiChannel>): Boolean {
            return channel >= wiFiChannelPair.first!!.channel && channel <= wiFiChannelPair.second!!.channel
        }
    }

    private inner class WiFiChannelClosure constructor(private val wiFiChannels: MutableList<WiFiChannel>) : Closure<Pair<WiFiChannel, WiFiChannel>> {

        override fun execute(wiFiChannelPair: Pair<WiFiChannel, WiFiChannel>) {
            for (channel in wiFiChannelPair.first!!.channel..wiFiChannelPair.second!!.channel) {
                wiFiChannels.add(getWiFiChannelByChannel(channel))
            }
        }
    }

    companion object {
        val UNKNOWN = Pair(WiFiChannel.UNKNOWN, WiFiChannel.UNKNOWN)
        const val FREQUENCY_SPREAD = 5
        const val CHANNEL_OFFSET = 2
        const val FREQUENCY_OFFSET = FREQUENCY_SPREAD * CHANNEL_OFFSET
    }

}