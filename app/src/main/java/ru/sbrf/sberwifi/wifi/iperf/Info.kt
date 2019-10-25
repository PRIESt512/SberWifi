package ru.sbrf.sberwifi.wifi.iperf

class Info(val uuid: String, val bssid: String, val ssid: String) {
    override fun toString(): String {
        return "\"info\":{\"uuid\":\"${uuid}\",\"bssid\":\"${bssid}\",\"ssid\":\"${ssid}\"}"
    }
}