package ru.sbrf.sberwifi.wifi.model

import kotlinx.serialization.Serializable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import java.util.*

@Serializable
class WiFiDetail constructor(val ssid: String = StringUtils.EMPTY,
                             val bssid: String,
                             val capabilities: String,
                             val wiFiSignal: WiFiSignal,
                             val wiFiAdditional: WiFiAdditional = WiFiAdditional.EMPTY) : Comparable<WiFiDetail>, java.io.Serializable {

    private val children: MutableList<WiFiDetail>

    val security = Security.findOne(capabilities)

    val SSID: String
        get() {
            return if (isHidden) SSID_EMPTY else ssid
        }

    val isHidden = StringUtils.isBlank(ssid)

    val title: String

    init {
        this.children = ArrayList()
        this.title = String.format("%s (%s)", SSID, bssid)
    }

    constructor(wiFiDetail: WiFiDetail, wiFiAdditional: WiFiAdditional) : this(wiFiDetail.ssid, wiFiDetail.bssid, wiFiDetail.capabilities, wiFiDetail.wiFiSignal, wiFiAdditional) {}

    fun getChildren(): List<WiFiDetail> {
        return children
    }

    fun noChildren(): Boolean {
        return getChildren().isNotEmpty()
    }

    fun addChild(wiFiDetail: WiFiDetail) {
        children.add(wiFiDetail)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true

        if (o == null || javaClass != o.javaClass) return false

        val that = o as WiFiDetail?

        return EqualsBuilder()
                .append(SSID, that!!.SSID)
                .append(bssid, that.bssid)
                .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
                .append(SSID)
                .append(bssid)
                .toHashCode()
    }

    override fun compareTo(another: WiFiDetail): Int {
        return CompareToBuilder()
                .append(SSID, another.SSID)
                .append(bssid, another.bssid)
                .toComparison()
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    companion object {
        val EMPTY = WiFiDetail(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, WiFiSignal.EMPTY)
        private const val SSID_EMPTY = "***"
    }

}