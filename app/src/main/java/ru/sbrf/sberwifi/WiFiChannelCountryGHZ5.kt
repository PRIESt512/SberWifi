package ru.sbrf.sberwifi

import org.apache.commons.lang3.StringUtils
import java.util.*

internal class WiFiChannelCountryGHZ5 {
    private val channels: SortedSet<Int>
    private val channelsToExclude: MutableMap<String, SortedSet<Int>>

    init {
        val channelsSet1 = TreeSet(listOf(36, 40, 44, 48, 52, 56, 60, 64))
        val channelsSet2 = TreeSet(listOf(100, 104, 108, 112, 116, 120, 124, 128, 132, 136, 140, 144))
        val channelsSet3 = TreeSet(listOf(149, 153, 157, 161, 165))

        val channelsToExcludeCanada = TreeSet(listOf(120, 124, 128))
        val channelsToExcludeIsrael = TreeSet(channelsSet2)
        channelsToExcludeIsrael.addAll(channelsSet3)

        channelsToExclude = HashMap()
        channelsToExclude["AU"] = channelsToExcludeCanada   // Australia
        channelsToExclude["CA"] = channelsToExcludeCanada   // Canada
        channelsToExclude["CN"] = channelsSet2              // China
        channelsToExclude["IL"] = channelsToExcludeIsrael   // Israel
        channelsToExclude["JP"] = channelsSet3              // Japan
        channelsToExclude["KR"] = channelsSet2              // South Korea
        channelsToExclude["TR"] = channelsSet3              // Turkey
        channelsToExclude["ZA"] = channelsSet3              // South Africa

        channels = TreeSet(channelsSet1)
        channels.addAll(channelsSet2)
        channels.addAll(channelsSet3)
    }

    fun findChannels(countryCode: String): SortedSet<Int> {
        val results = TreeSet(channels)
        val exclude = channelsToExclude[StringUtils.capitalize(countryCode)]
        if (exclude != null) {
            results.removeAll(exclude)
        }
        return results
    }

}
