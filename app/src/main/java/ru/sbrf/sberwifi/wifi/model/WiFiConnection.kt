package ru.sbrf.sberwifi.wifi.model

import kotlinx.serialization.Serializable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder

@Serializable
class WiFiConnection(val ssid: String,
                     val bssid: String,
                     val ipAddress: String,
                     val linkSpeed: String,
                     val mTxLinkSpeed: String = "LINK_SPEED_UNKNOWN",
                     val mRxLinkSpeed: String = "LINK_SPEED_UNKNOWN") {

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
        public const val LINK_SPEED_INVALID = "LINK_SPEED_UNKNOWN"
        val EMPTY = WiFiConnection(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, LINK_SPEED_INVALID)
    }
}

