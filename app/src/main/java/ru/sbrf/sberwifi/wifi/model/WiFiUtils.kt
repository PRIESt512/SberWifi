package ru.sbrf.sberwifi.wifi.model

import org.apache.commons.lang3.StringUtils

import java.math.BigInteger
import java.net.InetAddress
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

class WiFiUtils private constructor() {

    init {
        throw IllegalStateException("Utility class")
    }

    companion object {
        private const val DISTANCE_MHZ_M = 27.55
        private const val MIN_RSSI = -100
        private const val MAX_RSSI = -55
        private const val QUOTE = "\""

        fun calculateDistance(frequency: Int, level: Int): Double {
            return 10.0.pow((DISTANCE_MHZ_M - 20 * log10(frequency.toDouble()) + abs(level)) / 20.0)
        }

        fun calculateSignalLevel(rssi: Int, numLevels: Int): Int {
            if (rssi <= MIN_RSSI) {
                return 0
            }
            return if (rssi >= MAX_RSSI) {
                numLevels - 1
            } else (rssi - MIN_RSSI) * (numLevels - 1) / (MAX_RSSI - MIN_RSSI)
        }

        fun convertSSID(ssid: String): String {
            return StringUtils.removeEnd(StringUtils.removeStart(ssid, QUOTE), QUOTE)
        }

        fun convertIpAddress(ipAddress: Int): String {
            return try {
                val ipBytes = BigInteger.valueOf(
                        (if (ByteOrder.LITTLE_ENDIAN == ByteOrder.nativeOrder())
                            Integer.reverseBytes(ipAddress)
                        else
                            ipAddress).toLong())
                        .toByteArray()
                InetAddress.getByAddress(ipBytes).hostAddress
            } catch (e: Exception) {
                StringUtils.EMPTY
            }
        }
    }
}