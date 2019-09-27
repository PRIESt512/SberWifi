package ru.sbrf.sberwifi

enum class WiFiBand private constructor(val textResource: Int, val wiFiChannels: WiFiChannels) {
    GHZ2(R.string.wifi_band_2ghz, WiFiChannelsGHZ2()),
    GHZ5(R.string.wifi_band_5ghz, WiFiChannelsGHZ5());

    val isGHZ5: Boolean
        get() = WiFiBand.GHZ5 == this

    fun toggle(): WiFiBand {
        return if (isGHZ5) WiFiBand.GHZ2 else WiFiBand.GHZ5
    }
}