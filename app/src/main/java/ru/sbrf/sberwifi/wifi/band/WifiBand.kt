package ru.sbrf.sberwifi.wifi.band

import ru.sbrf.sberwifi.R

enum class WiFiBand constructor(val textResource: Int, val wiFiChannels: WiFiChannels) {
    GHZ2(R.string.wifi_band_2ghz, WiFiChannelsGHZ2()),
    GHZ5(R.string.wifi_band_5ghz, WiFiChannelsGHZ5());

    val isGHZ5: Boolean
        get() = GHZ5 == this

    fun toggle(): WiFiBand {
        return if (isGHZ5) GHZ2 else GHZ5
    }
}