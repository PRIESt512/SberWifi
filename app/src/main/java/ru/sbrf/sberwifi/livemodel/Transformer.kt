package ru.sbrf.sberwifi.livemodel

import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiInfo.LINK_SPEED_UNITS
import android.net.wifi.WifiInfo.LINK_SPEED_UNKNOWN
import org.apache.commons.collections4.CollectionUtils
import ru.sbrf.sberwifi.util.BuildUtils
import ru.sbrf.sberwifi.util.EnumUtils
import ru.sbrf.sberwifi.wifi.band.WiFiWidth
import ru.sbrf.sberwifi.wifi.model.*
import kotlin.math.abs

internal class Transformer {

    private fun transformWifiInfo(wifiInfo: WifiInfo?): WiFiConnection {
        return if (wifiInfo == null || wifiInfo.networkId == -1) {
            WiFiConnection.EMPTY
        } else WiFiConnection(
                WiFiUtils.convertSSID(wifiInfo.ssid),
                wifiInfo.bssid,
                WiFiUtils.convertIpAddress(wifiInfo.ipAddress),
                transformLinkSpeedUnits(wifiInfo.linkSpeed),
                wifiInfo.macAddress)
    }

    private fun transformLinkSpeedUnits(value: Int): String {
        return if (value == LINK_SPEED_UNKNOWN) {
            "LINK_SPEED_UNKNOWN"
        } else "$value $LINK_SPEED_UNITS"
    }

    private fun transformScanResult(scanResult: List<ScanResult>): List<WiFiDetail> {
        return ArrayList(CollectionUtils.collect(scanResult, ToWiFiDetail()))
    }

    fun transformToWiFiData(scanResult: List<ScanResult>, wifiInfo: WifiInfo?): WiFiData {
        val wiFiDetails = transformScanResult(scanResult)
        val wiFiConnection = transformWifiInfo(wifiInfo)
        return WiFiData(wiFiDetails, wiFiConnection)
    }

    fun getWiFiWidth(scanResult: ScanResult): WiFiWidth {
        return try {
            EnumUtils.find(WiFiWidth::class.java, getFieldValue(scanResult, Fields.channelWidth), WiFiWidth.MHZ_20)
        } catch (e: Exception) {
            WiFiWidth.MHZ_20
        }
    }

    fun getCenterFrequency(scanResult: ScanResult, wiFiWidth: WiFiWidth): Int {
        return try {
            var centerFrequency = getFieldValue(scanResult, Fields.centerFreq0)
            if (centerFrequency == 0) {
                centerFrequency = scanResult.frequency
            } else if (isExtensionFrequency(scanResult, wiFiWidth, centerFrequency)) {
                centerFrequency = (centerFrequency + scanResult.frequency) / 2
            }
            centerFrequency
        } catch (e: Exception) {
            scanResult.frequency
        }
    }

    private fun isExtensionFrequency(scanResult: ScanResult, wiFiWidth: WiFiWidth, centerFrequency: Int): Boolean {
        return WiFiWidth.MHZ_40 == wiFiWidth && abs(scanResult.frequency - centerFrequency) >= WiFiWidth.MHZ_40.frequencyWidthHalf
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun getFieldValue(scanResult: ScanResult, field: Fields): Int {
        val declaredField = scanResult.javaClass.getDeclaredField(field.name)
        return declaredField.get(scanResult) as Int
    }

    private fun is80211mc(scanResult: ScanResult): Boolean {
        return BuildUtils.isMinVersionM && scanResult.is80211mcResponder
    }

    internal enum class Fields {
        centerFreq0,
        //        centerFreq1,
        channelWidth
    }

    private inner class ToWiFiDetail : org.apache.commons.collections4.Transformer<ScanResult, WiFiDetail> {
        override fun transform(input: ScanResult): WiFiDetail {
            val wiFiWidth = getWiFiWidth(input)
            val centerFrequency = getCenterFrequency(input, wiFiWidth)
            val wiFiSignal = WiFiSignal(input.frequency, centerFrequency, wiFiWidth, input.level, is80211mc(input))
            return WiFiDetail(input.SSID, input.BSSID, input.capabilities, wiFiSignal)
        }
    }
}
