package ru.sbrf.sberwifi.util

import android.annotation.SuppressLint
import android.util.Log
import org.apache.commons.lang3.StringUtils
import java.net.NetworkInterface
import java.util.*

class UtilsNet {

    companion object {

        /**
         * Возвращает MAC адрес по имени интерфейса
         *
         * @param interfaceName eth0, wlan0
         * @return mac адрес или пустая строка
         */
        fun getMACAddress(interfaceName: String = INTERFACE_WLAN): String {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (networkInterface in interfaces) {
                    if (!networkInterface.name.equals(interfaceName, ignoreCase = true)) continue
                    val mac = networkInterface.hardwareAddress ?: return StringUtils.EMPTY
                    val buf = StringBuilder()
                    for (aMac in mac) buf.append(String.format("%02X:", aMac))
                    if (buf.isNotEmpty()) buf.deleteCharAt(buf.length - 1)
                    return buf.toString()
                }
            } catch (ignored: Exception) {
                Log.e("UtilNet", ignored.toString())
            }
            return StringUtils.EMPTY
        }

        /**
         * Получить ip-адрес не localhost интерфейса
         *
         * @param useIPv4 true=возвращает ipv4, false=возвращает ipv6
         * @return адрес или пустая строка
         */
        @SuppressLint("DefaultLocale")
        fun getIPAddress(useIPv4: Boolean): String {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress
                            val isIPv4 = sAddr.indexOf(':') < 0

                            if (useIPv4) {
                                if (isIPv4)
                                    return sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                    return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                                }
                            }
                        }
                    }
                }
            } catch (ignored: Exception) {
                Log.e("UtilNet", ignored.toString())
            }
            // for now eat exceptions
            return StringUtils.EMPTY
        }

        public const val INTERFACE_WLAN = "wlan0"
    }
}
