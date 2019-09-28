package ru.sbrf.sberwifi.wifi.band

import org.apache.commons.lang3.StringUtils
import java.util.*

internal class WiFiChannelCountryGHZ2 {
    private val countries: Set<String>
    private val channels: SortedSet<Int>
    private val world: SortedSet<Int>

    init {
        countries = HashSet(listOf(
                "AS", "CA", "CO", "DO", "FM", "GT", "GU", "MP", "MX", "PA", "PR", "UM", "US", "UZ", "VI")
        )
        channels = TreeSet(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
        world = TreeSet(channels)
        world.add(12)
        world.add(13)
    }

    fun findChannels(countryCode: String): SortedSet<Int> {
        var results: SortedSet<Int> = TreeSet(world)
        val code = StringUtils.capitalize(countryCode)
        if (countries.contains(code)) {
            results = TreeSet(channels)
        }
        return results
    }
}