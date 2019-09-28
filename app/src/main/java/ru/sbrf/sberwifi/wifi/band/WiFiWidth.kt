package ru.sbrf.sberwifi.wifi.band

enum class WiFiWidth private constructor(val frequencyWidth: Int) {
    MHZ_20(20),
    MHZ_40(40),
    MHZ_80(80),
    MHZ_160(160),
    MHZ_80_PLUS(80); // should be two 80 and 80 - feature support

    val frequencyWidthHalf: Int = frequencyWidth / 2
}