package ru.sbrf.sberwifi.wifi.model

import android.os.Build
import kotlinx.serialization.Serializable
import org.apache.commons.lang3.StringUtils
import ru.sbrf.sberwifi.util.UtilsNet

@Serializable
class DeviceInfo(val model: String, val version: String, val mac: String, @Volatile var ip: String) {

    companion object {

        @Volatile
        var INSTANCE: DeviceInfo? = null

        fun getInstance(wiFiConnection: WiFiConnection) {
            var result = INSTANCE
            val isEmptyIP = result?.ip?.isEmpty()

            if (isEmptyIP != null && isEmptyIP && result != null) {
                INSTANCE?.ip = wiFiConnection.ipAddress
            }

            result ?: synchronized(this) {
                result = INSTANCE
                result
                        ?: DeviceInfo(Build.MODEL, Build.VERSION.RELEASE,
                                UtilsNet.getMACAddress(UtilsNet.INTERFACE_WLAN),
                                wiFiConnection.ipAddress).also { INSTANCE = it }
            }
        }

        fun getInstanceWithOutIP(): DeviceInfo {
            var ins = INSTANCE
            return ins ?: synchronized(this) {
                ins = INSTANCE
                ins
                        ?: DeviceInfo(Build.MODEL, Build.VERSION.RELEASE,
                                UtilsNet.getMACAddress(UtilsNet.INTERFACE_WLAN),
                                StringUtils.EMPTY).also { INSTANCE = it }
            }

        }
    }
}

