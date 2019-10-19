package ru.sbrf.sberwifi.predicate

import org.apache.commons.collections4.Predicate
import ru.sbrf.sberwifi.wifi.band.WiFiBand
import ru.sbrf.sberwifi.wifi.model.WiFiDetail

class WiFiBandPredicate(private val wiFiBand: WiFiBand) : Predicate<WiFiDetail> {

    override fun evaluate(`object`: WiFiDetail): Boolean {
        return `object`.wiFiSignal.wiFiBand == wiFiBand
    }
}