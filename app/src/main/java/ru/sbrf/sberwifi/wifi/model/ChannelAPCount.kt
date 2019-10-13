package ru.sbrf.sberwifi.wifi.model

import org.apache.commons.lang3.builder.ToStringBuilder
import ru.sbrf.sberwifi.wifi.band.WiFiChannel

class ChannelAPCount(val wiFiChannel: WiFiChannel, internal val count: Int) {

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}
