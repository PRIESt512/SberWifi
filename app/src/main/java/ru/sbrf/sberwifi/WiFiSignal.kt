package ru.sbrf.sberwifi

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import java.util.*

class WiFiSignal(val primaryFrequency: Int, val centerFrequency: Int, val wiFiWidth: WiFiWidth, val level: Int, val is80211mc: Boolean) {
    val wiFiBand: WiFiBand

    val frequencyStart: Int
        get() = centerFrequency - wiFiWidth.frequencyWidthHalf

    val frequencyEnd: Int
        get() = centerFrequency + wiFiWidth.frequencyWidthHalf

    val primaryWiFiChannel: WiFiChannel
        get() = wiFiBand.wiFiChannels.getWiFiChannelByFrequency(primaryFrequency)

    val centerWiFiChannel: WiFiChannel
        get() = wiFiBand.wiFiChannels.getWiFiChannelByFrequency(centerFrequency)

    val strength: Strength
        get() = Strength.calculate(level)

    val distance: String
        get() {
            val distance = WiFiUtils.calculateDistance(primaryFrequency, level)
            return String.format(Locale.ENGLISH, "~%.1fm", distance)
        }

    val channelDisplay: String
        get() {
            val primaryChannel = primaryWiFiChannel.channel
            val centerChannel = centerWiFiChannel.channel
            var channel = primaryChannel.toString()
            if (primaryChannel != centerChannel) {
                channel += "($centerChannel)"
            }
            return channel
        }

    init {
        this.wiFiBand = EnumUtils.find(WiFiBand::class.java, FrequencyPredicate(primaryFrequency), WiFiBand.GHZ2)
    }

    fun isInRange(frequency: Int): Boolean {
        return frequency in frequencyStart..frequencyEnd
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true

        if (o == null || javaClass != o.javaClass) return false

        val that = o as WiFiSignal?

        return EqualsBuilder()
                .append(primaryFrequency, that!!.primaryFrequency)
                .append(wiFiWidth, that.wiFiWidth)
                .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
                .append(primaryFrequency)
                .append(wiFiWidth)
                .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    companion object {
        val EMPTY = WiFiSignal(0, 0, WiFiWidth.MHZ_20, 0, false)
        val FREQUENCY_UNITS = "MHz"
    }

}
