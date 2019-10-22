package ru.sbrf.sberwifi.wifi.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.ToStringBuilder

@Serializable
class WiFiAdditional constructor(var vendorName: String, @Transient val wiFiConnection: WiFiConnection = WiFiConnection.EMPTY) {

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    companion object {
        val EMPTY = WiFiAdditional(StringUtils.EMPTY)
    }
}