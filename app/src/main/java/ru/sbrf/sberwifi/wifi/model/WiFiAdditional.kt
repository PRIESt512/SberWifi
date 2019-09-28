package ru.sbrf.sberwifi.wifi.model

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.ToStringBuilder

class WiFiAdditional @JvmOverloads constructor(val vendorName: String, val wiFiConnection: WiFiConnection = WiFiConnection.EMPTY) {

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    companion object {
        val EMPTY = WiFiAdditional(StringUtils.EMPTY)
    }
}