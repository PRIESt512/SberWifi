package ru.sbrf.sberwifi.predicate

import org.apache.commons.collections4.Predicate
import ru.sbrf.sberwifi.wifi.band.WiFiBand

class FrequencyPredicate(private val frequency: Int) : Predicate<WiFiBand> {

    override fun evaluate(wiFiBand: WiFiBand): Boolean {
        return wiFiBand.wiFiChannels.isInRange(frequency)
    }
}