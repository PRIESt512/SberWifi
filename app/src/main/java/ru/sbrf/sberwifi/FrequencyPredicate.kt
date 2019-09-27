package ru.sbrf.sberwifi

import org.apache.commons.collections4.Predicate

class FrequencyPredicate(private val frequency: Int) : Predicate<WiFiBand> {

    override fun evaluate(wiFiBand: WiFiBand): Boolean {
        return wiFiBand.getWiFiChannels().isInRange(frequency)
    }
}