package ru.sbrf.sberwifi.wifi.model

import kotlinx.serialization.Serializable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import ru.sbrf.sberwifi.util.UtilsNet

@Serializable
class WiFiConnection(val ssid: String,
                     val bssid: String,
                     val ipAddress: String,
                     val linkSpeed: String,
                     var macAddressDevice: String) {

    init {
        //используется только для основного соединения мобильного устройства
        if (macAddressDevice == DEFAULT_MAC_ADDRESS && ssid != StringUtils.EMPTY) {
            val macAddress = UtilsNet.getMACAddress(UtilsNet.INTERFACE_WLAN)
            if (macAddress.isNotEmpty()) {
                this.macAddressDevice = macAddress
            }
        }
    }

    /* val title by lazy(LazyThreadSafetyMode.NONE) {
         String.format("%s (%s)", ssid, bssid)
     }*/

    val title: String
        get() = String.format("%s (%s)", ssid, bssid)


    val isConnected: Boolean
        get() = EMPTY != this

    override fun equals(o: Any?): Boolean {
        if (this === o) return true

        if (o == null || javaClass != o.javaClass) return false

        val that = o as WiFiConnection?

        return EqualsBuilder()
                .append(ssid, that!!.ssid)
                .append(bssid, that.bssid)
                .append(macAddressDevice, that.macAddressDevice)
                .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
                .append(ssid)
                .append(bssid)
                .append(macAddressDevice)
                .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    companion object {
        public const val LINK_SPEED_INVALID = "LINK_SPEED_UNKNOWN"

        public const val DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00"

        val EMPTY = WiFiConnection(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, LINK_SPEED_INVALID, DEFAULT_MAC_ADDRESS)
    }
}

