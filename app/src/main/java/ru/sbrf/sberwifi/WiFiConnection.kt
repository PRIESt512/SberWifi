package ru.sbrf.sberwifi

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder

class WiFiConnection(val ssid: String, val bssid: String, val ipAddress: String, val linkSpeed: Int) {

    val isConnected: Boolean
        get() = EMPTY != this

    override fun equals(o: Any?): Boolean {
        if (this === o) return true

        if (o == null || javaClass != o.javaClass) return false

        val that = o as WiFiConnection?

        return EqualsBuilder()
                .append(ssid, that!!.ssid)
                .append(bssid, that.bssid)
                .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
                .append(ssid)
                .append(bssid)
                .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    companion object {
        private const val LINK_SPEED_INVALID = -1
        val EMPTY = WiFiConnection(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, LINK_SPEED_INVALID)
    }
}

