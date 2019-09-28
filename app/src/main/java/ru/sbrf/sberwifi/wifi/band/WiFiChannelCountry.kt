package ru.sbrf.sberwifi.wifi.band

import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.collections4.Transformer
import org.apache.commons.lang3.StringUtils
import ru.sbrf.sberwifi.util.LocaleUtils
import java.util.*

class WiFiChannelCountry private constructor(private val country: Locale) {

    val countryCode: String
        get() {
            var countryCode: String? = country.country
            if (countryCode == null) {
                countryCode = StringUtils.EMPTY
            }
            return countryCode
        }

    val channelsGHZ2: SortedSet<Int>
        get() = WIFI_CHANNEL_GHZ2.findChannels(country.country)

    val channelsGHZ5: SortedSet<Int>
        get() = WIFI_CHANNEL_GHZ5.findChannels(country.country)

    fun getCountryName(currentLocale: Locale): String {
        var countryName: String? = country.getDisplayCountry(currentLocale)
        if (countryName == null) {
            countryName = StringUtils.EMPTY
        }
        return if (country.country == countryName) countryName + UNKNOWN else countryName
    }

    internal fun isChannelAvailableGHZ2(channel: Int): Boolean {
        return channelsGHZ2.contains(channel)
    }

    internal fun isChannelAvailableGHZ5(channel: Int): Boolean {
        return channelsGHZ5.contains(channel)
    }

    private class ToCountry : Transformer<Locale, WiFiChannelCountry> {
        override fun transform(input: Locale): WiFiChannelCountry {
            return WiFiChannelCountry(input)
        }
    }

    companion object {
        private const val UNKNOWN = "-Unknown"

        private val WIFI_CHANNEL_GHZ2 = WiFiChannelCountryGHZ2()
        private val WIFI_CHANNEL_GHZ5 = WiFiChannelCountryGHZ5()

        operator fun get(countryCode: String): WiFiChannelCountry {
            return WiFiChannelCountry(LocaleUtils.findByCountryCode(countryCode))
        }

        val all: List<WiFiChannelCountry>
            get() = ArrayList(CollectionUtils.collect(LocaleUtils.getAllCountries(), ToCountry()))
    }
}