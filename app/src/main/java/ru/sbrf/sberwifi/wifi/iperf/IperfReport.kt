package ru.sbrf.sberwifi.wifi.iperf

class IperfReport(val info: Info, val iperf: Any) {

    override fun toString(): String {
        return "{${info.toString()}, \"iperf\":${iperf}}}"
    }
}