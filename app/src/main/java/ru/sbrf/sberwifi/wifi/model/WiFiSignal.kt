package ru.sbrf.sberwifi.wifi.model

import kotlinx.serialization.Serializable
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import ru.sbrf.sberwifi.predicate.FrequencyPredicate
import ru.sbrf.sberwifi.util.EnumUtils
import ru.sbrf.sberwifi.wifi.band.WiFiBand
import ru.sbrf.sberwifi.wifi.band.WiFiChannel
import ru.sbrf.sberwifi.wifi.band.WiFiWidth
import java.util.*

@Serializable
class WiFiSignal(val primaryFrequency: Int, val centerFrequency: Int, val wiFiWidth: WiFiWidth, val level: Int, val is80211mc: Boolean) {
    private val wiFiBand: WiFiBand = EnumUtils.find(WiFiBand::class.java, FrequencyPredicate(primaryFrequency), WiFiBand.GHZ2)

    public val frequencyStart: Int = centerFrequency - wiFiWidth.frequencyWidthHalf

    public val frequencyEnd: Int = centerFrequency + wiFiWidth.frequencyWidthHalf

    val primaryWiFiChannel: WiFiChannel

    private val centerWiFiChannel: WiFiChannel

    public val strength: Strength

    private val distance: String

    private val channelDisplay: String

    init {
        this.primaryWiFiChannel = wiFiBand.wiFiChannels.getWiFiChannelByFrequency(primaryFrequency)
        this.centerWiFiChannel = wiFiBand.wiFiChannels.getWiFiChannelByFrequency(centerFrequency)
        this.strength = Strength.calculate(level)
        this.distance = String.format(Locale.ENGLISH, "~%.1fm", WiFiUtils.calculateDistance(this.primaryFrequency, this.level))
        this.channelDisplay = retrieveChannelDisplay()
    }

    private fun retrieveChannelDisplay(): String {
        val primaryChannel = primaryWiFiChannel.channel
        val centerChannel = centerWiFiChannel.channel
        var channel = primaryChannel.toString()
        if (primaryChannel != centerChannel) {
            channel += "($centerChannel)"
        }
        return channel
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
