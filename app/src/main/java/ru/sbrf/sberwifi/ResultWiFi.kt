package ru.sbrf.sberwifi

import android.net.wifi.ScanResult

public class ResultWiFi constructor(val scan: ScanResult) {
    override fun toString(): String {
        return scan.toString()
    }
}