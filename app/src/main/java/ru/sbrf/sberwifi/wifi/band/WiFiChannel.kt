package ru.sbrf.sberwifi.wifi.band

import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder

class WiFiChannel : Comparable<WiFiChannel> {

    val channel: Int
    val frequency: Int

    private constructor() {
        frequency = 0
        channel = frequency
    }

    constructor(channel: Int, frequency: Int) {
        this.channel = channel
        this.frequency = frequency
    }

    fun isInRange(frequency: Int): Boolean {
        return frequency >= this.frequency - ALLOWED_RANGE && frequency <= this.frequency + ALLOWED_RANGE
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true

        if (o == null || javaClass != o.javaClass) return false

        val that = o as WiFiChannel?

        return EqualsBuilder()
                .append(channel, that!!.channel)
                .append(frequency, that.frequency)
                .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
                .append(channel)
                .append(frequency)
                .toHashCode()
    }

    override fun compareTo(another: WiFiChannel): Int {
        return CompareToBuilder()
                .append(channel, another.channel)
                .append(frequency, another.frequency)
                .toComparison()
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    companion object {
        val UNKNOWN = WiFiChannel()

        private val ALLOWED_RANGE = WiFiChannels.FREQUENCY_SPREAD / 2
    }
}